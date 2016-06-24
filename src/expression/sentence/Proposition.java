package expression.sentence;

import expression.Sort;
import logicalreasoner.inference.Inference;
import logicalreasoner.truthassignment.TruthAssignment;

import java.util.HashSet;
import java.util.Set;

/**
 * The Proposition class represents a single atomic sentence.
 * It can be a proposition, constant, or variable.
 */
public class Proposition extends Atom {

  /**
   * Create a new Proposition object with the given name
   *
   * @param n the name of the Proposition
   */
  public Proposition(String n) {
    super(n, Sort.BOOLEAN);
  }

  public Sentence makeCopy() {
    return this;
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


  public String toString() {
    return name;
  }

  public String toSExpression() {
    return name;
  }

  public int hashCode() {
    return name.hashCode();
  }

  @Override
  public Set<Sentence> getConstants() {
    return new HashSet<>();
  }

  @Override
  public Sentence instantiate(Sentence c, Variable v) {
    return this;
  }
}
