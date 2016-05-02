package expression.sentence;

import logicalreasoner.inference.Inference;
import logicalreasoner.truthassignment.TruthAssignment;

import java.util.ArrayList;

/**
 * The Atom class represents a single propositional variable
 */
public class Atom extends Sentence {

  /**
   * Create a new Atom object with the given name
   *
   * @param n the name of the Atom
   */
  public Atom(String n) {
    super(new ArrayList<>(), n, n);
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
  public Inference reason(TruthAssignment h, int inferenceNum) {
    if (!h.isDecomposed(this))
      h.setDecomposed(this);
    return null;
  }


  public String toString() {
    return name;
  }

  public String toSymbol() {
    return name;
  }

  public int hashCode() {
    return name.hashCode();
  }
}
