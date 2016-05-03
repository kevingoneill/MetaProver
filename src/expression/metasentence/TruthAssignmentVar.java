package expression.metasentence;

import expression.sentence.Atom;
import expression.sentence.Sentence;
import logicalreasoner.inference.Decomposition;
import logicalreasoner.inference.Inference;
import logicalreasoner.prover.SemanticProver;
import logicalreasoner.truthassignment.TruthAssignment;
import metareasoner.metainference.MetaInference;
import metareasoner.proof.Proof;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * TruthAssignmentVar is a wrapper class for TruthAssignment which
 * allows the meta-reasoner to make judgements about particular
 * (but arbitrary as a whole) TruthAssignments.
 */
public class TruthAssignmentVar extends MetaSentence {
  private TruthAssignment truthAssignment;
  private ArrayList<Inference> inferences;
  private int currInference = 0;

  public TruthAssignmentVar(TruthAssignment t) {
    super(new ArrayList<>(), t.getRoot().getName(), t.getRoot().getName(), new HashSet<>());
    truthAssignment = t;
    inferences = new ArrayList<>();
    reduce();
  }

  public Boolean isConsistent(TruthAssignment h) {
    return truthAssignment.isConsistent();
  }

  public void setTrue(Sentence s, int inferenceNum) {
    Decomposition d
    truthAssignment.setTrue(s, inferenceNum);
    reduce();
  }

  public void setFalse(Sentence s, int inferenceNum) {
    truthAssignment.setFalse(s, inferenceNum);
    reduce();
  }

  public TruthAssignmentVar addChild(TruthAssignment h) {
    truthAssignment.addChildren(Collections.singletonList(h));
    return new TruthAssignmentVar(truthAssignment.getChildren().get(truthAssignment.getChildren().size() - 1));
  }

  public boolean models(Sentence s) {
    return truthAssignment.models(s);
  }

  public MetaInference reasonForwards(Proof p, int inferenceNum) {
    return null;
  }

  public MetaInference reasonBackwards(Proof p, int inferenceNum) {
    return null;
  }

  public TruthAssignment getTruthAssignment() {
    return truthAssignment;
  }

  public List<Inference> getInferences() {
    return inferences;
  }

  public Inference getNextInference() {
    if (currInference < inferences.size())
      return inferences.get(currInference++);
    return null;
  }

  @Override
  public String toString() {
    return name;
  }

  @Override
  public String toSymbol() {
    return name;
  }

  public int hashCode() {
    return name.hashCode();
  }

  public boolean equals(Object o) {
    if (o instanceof TruthAssignmentVar) {
      TruthAssignment t = ((TruthAssignmentVar) o).truthAssignment;

      return truthAssignment.keySet().stream().filter(s -> s instanceof Atom).allMatch(s -> truthAssignment.models(s) == t.models(s));
    }
    return false;
  }

  private void reduce() {
    SemanticProver.decompose(truthAssignment, inferences);
  }
}
