package expression.sentence;

import expression.Sort;
import logicalreasoner.inference.Inference;
import logicalreasoner.truthassignment.TruthAssignment;

import java.util.Collections;

/**
 * An atom is a logical Sentence with no arguments. It can be a
 * proposition, a constant, or a variable.
 */
public abstract class Atom extends Function {
  public Atom(String name, Sort s) {
    super(name, s, Collections.emptyList());
  }

  @Override
  public String toSExpression() {
    return name;
  }

  @Override
  public String toString() {
    return name;
  }

  /**
   * Return the value of this in the given TruthAssignment
   *
   * @param h the TruthAssignment used to evaluate this
   * @return null if h does not contain this,
   * true if h models this, or false if h models (not this)
   */
  public Boolean eval(TruthAssignment h) {
    if (h.isMapped(this))
      return h.models(this);
    return null;
  }

  @Override
  public Inference reason(TruthAssignment h, int inferenceNum, int justificationNum) {
    h.setDecomposed(this);
    return null;
  }
}
