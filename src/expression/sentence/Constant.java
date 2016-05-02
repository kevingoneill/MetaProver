package expression.sentence;

import logicalreasoner.inference.Inference;
import logicalreasoner.truthassignment.TruthAssignment;

/**
 * The Constant represents logical tautology and contradiction
 */
public class Constant extends Atom {
  public static final Constant TRUE = new Constant(true),
          FALSE = new Constant(false);

  private boolean value;

  private Constant(boolean b) {
    super(b ? "⊤" : "⊥");
    value = b;
  }

  public Boolean eval(TruthAssignment h) {
    return value;
  }

  @Override
  public Inference reason(TruthAssignment h, int inferenceNum) {
    if (!h.isDecomposed(this))
      h.setDecomposed(this);
    return null;
  }
}
