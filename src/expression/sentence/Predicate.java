package expression.sentence;

import expression.Sort;
import logicalreasoner.inference.Inference;
import logicalreasoner.truthassignment.TruthAssignment;

import java.util.List;

/**
 * The Predicate class represents a function over
 * some constants which returns a Boolean
 * <p>
 * ie, (P x), (W x y z), and (H a) are all predicates
 */
public class Predicate extends Function {
  /**
   * Create a new Predicate object with the given name
   * @param n the name of the Predicate
   */
  public Predicate(String n, List<Sentence> vars) {
    super(n, Sort.BOOLEAN, vars);
  }

  public Predicate(String n, Sentence... sentences) {
    super(n, Sort.BOOLEAN, sentences);
  }

  @Override
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
