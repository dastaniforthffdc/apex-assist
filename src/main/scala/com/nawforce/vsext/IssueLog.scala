/*
 Copyright (c) 2020 Kevin Jones, All rights reserved.
 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:
 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
    derived from this software without specific prior written permission.
 */
package com.nawforce.vsext

import com.nawforce.pkgforce.diagnostics._
import com.nawforce.rpc.Server

import scala.collection.compat.immutable.ArraySeq
import scala.collection.mutable
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js
import scala.scalajs.js.JSConverters._

class IssueLog(server: Server, diagnostics: DiagnosticCollection) {

  private final val showWarningsConfig = "apex-assist.errorsAndWarnings.showWarnings"
  private final val showWarningsOnChangeConfig =
    "apex-assist.errorsAndWarnings.showWarningsOnChange"
  private val warningsAllowed = new mutable.HashSet[String]()

  VSCode.workspace.onDidChangeConfiguration(onConfigChanged, js.undefined, js.Array())

  def onConfigChanged(event: ConfigurationChangeEvent): Unit = {
    if (event.affectsConfiguration(showWarningsConfig) || event.affectsConfiguration(
          showWarningsOnChangeConfig)) {
      LoggerOps.info(s"$showWarningsConfig or $showWarningsOnChangeConfig Configuration Changed")
      refreshDiagnostics()
    }
  }

  def clear(): Unit = {
    diagnostics.clear()
  }

  def allowWarnings(td: TextDocument): Unit = {
    warningsAllowed.add(td.uri.fsPath)
  }

  def refreshDiagnostics(): Unit = {
    val showWarnings =
      VSCode.workspace.getConfiguration().get[Boolean](showWarningsConfig).getOrElse(false)

    val showWarningsOnChange =
      VSCode.workspace.getConfiguration().get[Boolean](showWarningsOnChangeConfig).getOrElse(true)

    server
      .getIssues(includeWarnings = true, includeZombies = false)
      .map(issuesResult => {
        diagnostics.clear()

        val issueMap = issuesResult.issues
          .filter(i => allowIssues(i, showWarnings, showWarningsOnChange))
          .groupBy(_.path)
          .map { case (x, xs) => (x, xs) }
        issueMap.keys.foreach(path => {
          diagnostics.set(
            VSCode.Uri.file(path),
            issueMap(path).sortBy(_.diagnostic.location.startLine).map(issueToDiagnostic).toJSArray)
        })
      })
  }

  def setLocalDiagnostics(td: TextDocument, issues: ArraySeq[Issue]): Unit = {
    val nonSyntax = diagnostics.get(td.uri).getOrElse(js.Array()).filter(_.code != "Syntax")
    diagnostics.set(td.uri, nonSyntax ++ issues.map(issueToDiagnostic).toJSArray)
  }

  private def allowIssues(issue: Issue,
                          showWarnings: Boolean,
                          showWarningsOnChange: Boolean): Boolean = {
    if (showWarnings || (showWarningsOnChange && warningsAllowed.contains(issue.path)))
      return true

    issue.diagnostic.category != WARNING_CATEGORY && issue.diagnostic.category != UNUSED_CATEGORY
  }

  private def issueToDiagnostic(issue: Issue): com.nawforce.vsext.Diagnostic = {
    val diag = VSCode.newDiagnostic(locationToRange(issue.diagnostic.location),
                                    issue.diagnostic.message,
                                    issue.diagnostic.category match {
                                      case WARNING_CATEGORY => DiagnosticSeverity.WARNING
                                      case UNUSED_CATEGORY  => DiagnosticSeverity.WARNING
                                      case _                => DiagnosticSeverity.ERROR
                                    })
    diag.code = issue.diagnostic.category.value
    diag
  }

  private def locationToRange(location: Location): Range = {
    VSCode.newRange(location.startLine - 1,
                    location.startPosition,
                    location.endLine - 1,
                    location.endPosition)
  }

}

object IssueLog {
  def apply(server: Server, diagnostics: DiagnosticCollection): IssueLog = {
    new IssueLog(server, diagnostics)
  }
}
