package gui.truthtreevisualization;

import java.util.List;

import logicalreasoner.inference.Inference;

public class TruthTree {
  /**
   * Representation of the TruthAssignments used in the truth-functional and meta-proofs
   * Allows for easy creation of visual truth trees that represent these TruthAssignments
   */

  TreeBranch root;

  public TruthTree(TreeBranch r) {
    root = r;
  }

  public TreeBranch getRoot() {
    return root;
  }

  public void print() {
    root.print("");
  }

  public void setInferences(List<Inference> inferenceList) {
	root.setInferences(inferenceList);
  }
  
  public void placeStatements() {
	root.placeStatements();
  }
}


