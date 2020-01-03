/*
 [The "BSD licence"]
 Copyright (c) 2020 Kevin Jones
 All rights reserved.

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

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package com.nawforce.vsext.vscode

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@js.native
@JSImport("vscode", "Position")
class Position(line: Int, character: Int) extends js.Object {
}

@js.native
@JSImport("vscode", "Range")
class Range(start: Position, end: Position) extends js.Object {
}

object DiagnosticSeverity {
  val ERROR: Int = 0
  val WARNING: Int = 1
  val INFORMATION: Int = 2
  val HINT: Int = 3
}

@js.native
@JSImport("vscode", "Diagnostic")
class Diagnostic(range: Range, message: String, severity: Int) extends js.Object {

}

trait DiagnosticCollection extends Disposable {
  val name: String

  def clear(): Unit
  def delete(uri: URI): Unit
  def has(uri: URI): Boolean
  def set(uri: URI, diagnostics: js.Array[Diagnostic])
}

@js.native
@JSImport("vscode", "languages")
object Languages extends js.Object {
  def createDiagnosticCollection(name: String): DiagnosticCollection = js.native
}
