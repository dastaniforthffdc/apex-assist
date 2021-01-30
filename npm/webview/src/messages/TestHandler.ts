import { GraphData } from "../components/Graph";
import { Handler } from "./Handler";
import { Reciever } from "./Receiver";

export class TestHandler implements Handler {
  private reciever: Reciever;

  private dependents: GraphData = {
    nodeData: [
      { id: 1, name: "A" },
      { id: 2, name: "B" },
    ],
    linkData: [
      {
        source: 1,
        target: 2,
      },
    ],
  };

  constructor(reciever: Reciever) {
    this.reciever = reciever;
  }

  requestDependents(identifier: string, depth: number): void {
    console.log("Request dependent: " + identifier + " with depth " + depth);
    setTimeout(() => {
      this.reciever.onDependents(this.dependents);
    }, 2000);
  }
}
