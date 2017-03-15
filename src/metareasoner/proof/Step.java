package metareasoner.proof;

import expression.metasentence.MetaSentence;
import metareasoner.metainference.MetaInference;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * The Step class represents the state of a proof step
 * for a given MetaSentence in the proof
 */
public class Step {
  private MetaSentence metaSentence;
  private MetaInference justification;
  private boolean isDecomposed, isForwards;
  private Step parent;
  private ArrayList<Step> children;

  protected Step(MetaSentence s, MetaInference i, Step p, boolean forwards) {
    metaSentence = s;
    justification = i;
    isDecomposed = false;
    children = new ArrayList<>();
    parent = p;
    if (parent != null)
      parent.addChild(this);
    isForwards = forwards;
  }

  public MetaSentence getMetaSentence() {
    return metaSentence;
  }

  public MetaInference getJustification() {
    return justification;
  }

  public void setJustification(MetaInference i) {
    justification = i;
  }

  public Step getParent() {
    return parent;
  }

  public ArrayList<Step> getChildren() {
    return children;
  }

  public ArrayList<Step> getLeaves() {
    if (children.isEmpty()) {
      ArrayList<Step> a = new ArrayList<>();
      a.add(this);
      return a;
    }

    ArrayList<Step> leaves = new ArrayList<>();
    leaves.addAll(children.stream().flatMap(s -> s.getLeaves().stream())
            .collect(Collectors.toList()));
    return leaves;
  }

  public void addChild(Step s) {
    children.add(s);
  }

  public void setDecomposed() {
    isDecomposed = true;
  }

  public boolean isDecomposed() {
    return isDecomposed;
  }

  public boolean isForwards() {
    return isForwards;
  }

  public int hashCode() {
    return metaSentence.hashCode();
  }

  public boolean equals(Object o) {
    if (o instanceof Step) {
      Step s = (Step) o;
      if (justification == null)
        return isDecomposed == s.isDecomposed && metaSentence.equals(s.metaSentence) && s.justification == null;
      return isDecomposed == s.isDecomposed && metaSentence.equals(s.metaSentence) && s.justification.equals(justification);
    }
    return false;
  }

  public String toString() {
    return metaSentence.toSExpression();
  }

  public String getReason() {

    if (isForwards) {
      if (justification != null)
        return justification.getReason();
      return "Premise";
    }

    if (justification != null)
      return justification.getReason();
    return "...";
    /*
    if (metaSentence instanceof MODELS)
      return "(sem. of " + ((MODELS)metaSentence).getSentence().getSymbol() + ")";
    else if (metaSentence instanceof IS)
      return  "(def. of " + ((IS) metaSentence).getConstant().getSymbol() + ")";
    return "(def. of " + metaSentence.getSymbol() + ")";
    */
  }
}
