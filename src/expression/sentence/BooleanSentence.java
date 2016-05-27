package expression.sentence;

import logicalreasoner.inference.Inference;
import logicalreasoner.truthassignment.TruthAssignment;

import java.util.HashSet;
import java.util.Set;

/**
 * The BooleanSentence represents logical tautology and contradiction
 */
public class BooleanSentence extends Proposition {
  public static final BooleanSentence TRUE = new BooleanSentence(true),
          FALSE = new BooleanSentence(false);

  private boolean value;

  private BooleanSentence(boolean b) {
    super(b ? "⊤" : "⊥");
    value = b;
  }

  public Boolean eval(TruthAssignment h) {
    return value;
  }

  @Override
  public Inference reason(TruthAssignment h, int inferenceNum, int justificationNum) {
    if (!h.isDecomposed(this))
      h.setDecomposed(this);
    return null;
  }

  @Override
  public Set<Constant> getConstants() {
    return new HashSet<>();
  }

  @Override
  public Sentence instantiate(Constant c, Variable v) {
    return this;
  }
}
