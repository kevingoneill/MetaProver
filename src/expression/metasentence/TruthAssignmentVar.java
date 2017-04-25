package expression.metasentence;

import expression.sentence.Proposition;
import expression.sentence.Sentence;
import logicalreasoner.inference.Decomposition;
import logicalreasoner.inference.Inference;
import logicalreasoner.prover.Prover;
import logicalreasoner.truthassignment.TruthAssignment;
import metareasoner.metainference.MetaInference;
import metareasoner.proof.Proof;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TruthAssignmentVar is a wrapper class for TruthAssignment which
 * allows the meta-reasoner to make judgements about particular
 * (but arbitrary as a whole) TruthAssignments.
 */
public class TruthAssignmentVar extends MetaSentence {
  private TruthAssignment truthAssignment;
  private TruthAssignmentVar parent;
  private ArrayList<Inference> inferences;
  private int currInference = 0;

  public TruthAssignmentVar(TruthAssignment t, TruthAssignmentVar v) {
    super(new ArrayList<>(), t.getRoot().getName(), t.getRoot().getName(), new HashSet<>());
    truthAssignment = t;
    inferences = new ArrayList<>();
    parent = v;

    if (v == null)
      reduce();
    else
      inferences.addAll(v.inferences.stream().filter(i -> i.getParent() == truthAssignment).collect(Collectors.toList()));
  }

  public TruthAssignmentVar(TruthAssignment t) {
    this(t, null);
  }

  public Boolean isConsistent(TruthAssignment h) {
    return truthAssignment.isConsistent();
  }

  public void setTrue(Sentence s, int inferenceNum) {
    //truthAssignment.setTrue(s, inferenceNum);
    Decomposition d = new Decomposition(truthAssignment, s, -1, -1);
    d.setTrue(s);
    d.infer(truthAssignment);
    addInference(d);
    ++currInference;
    reduce();
  }

  public void setFalse(Sentence s, int inferenceNum) {
    //truthAssignment.setFalse(s, inferenceNum);
    Decomposition d = new Decomposition(truthAssignment, s, -1, -1);
    d.setFalse(s);
    d.infer(truthAssignment);
    addInference(d);
    ++currInference;
    reduce();
  }

  public void addInference(Inference i) {
    inferences.add(i);
    if (parent != null)
      parent.addInference(i);
  }

  public TruthAssignmentVar addChild(TruthAssignment h) {
    truthAssignment.addChildren(Collections.singletonList(h)).count();
    return new TruthAssignmentVar(truthAssignment.getChildren().get(truthAssignment.getChildren().size() - 1), this);
  }

  public TruthAssignmentVar getChild(TruthAssignment h) {
    for (TruthAssignment child : truthAssignment.getChildren()) {
      if (child.assignmentsEqual(h))
        return new TruthAssignmentVar(child, this);
    }

    return null;
  }

  public boolean models(Sentence s) {
    return truthAssignment.models(s);
  }

  public MetaInference reason(Proof p, int inferenceNum) {
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
  public String toSExpression() {
    return name;
  }

  public int hashCode() {
    return name.hashCode();
  }

  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o instanceof TruthAssignmentVar) {
      TruthAssignment t = ((TruthAssignmentVar) o).truthAssignment;

      return truthAssignment.keySet().stream().filter(s -> s instanceof Proposition).allMatch(s -> truthAssignment.models(s) == t.models(s));
    }
    return false;
  }

  private void reduce() {
    Prover.decompose(truthAssignment, inferences);
  }

  @Override
  public MetaSentence toplevelCopy(HashSet<TruthAssignmentVar> vars) {
    return this;
  }
}
