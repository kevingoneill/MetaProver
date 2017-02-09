package expression.sentence;

import expression.Sort;
import logicalreasoner.inference.Decomposition;
import logicalreasoner.inference.Inference;
import logicalreasoner.truthassignment.TruthAssignment;

import java.util.Arrays;
import java.util.Set;

/**
 * The Not class represents a logical negation
 * <p>
 * (not A)
 */
public class Not extends Sentence {
  public static String NAME = "not", SYMBOL = "¬";

  public Not(Sentence e) {
    super(Arrays.asList(e), NAME, SYMBOL, Sort.BOOLEAN);
  }

  public String toString() {
    if (TOSTRING == null)
      TOSTRING = symbol + args.get(0).toString();
    return TOSTRING;
  }

  public Boolean eval(TruthAssignment h) {
    Boolean val = args.get(0).eval(h);
    if (val == null)
      return null;
    return !val;
  }

  @Override
  public Inference reason(TruthAssignment h, int inferenceNum, int justificationNum) {
    h.setDecomposed(this);
    if (h.models(this)) {
      Decomposition d = new Decomposition(h, this, inferenceNum, justificationNum);
      d.setFalse(args.get(0));
      return d;
    } else {
      Decomposition d = new Decomposition(h, this, inferenceNum, justificationNum);
      d.setTrue(args.get(0));
      return d;
    }
  }

  @Override
  public Set<Sentence> getConstants() {
    return args.get(0).getConstants();
  }

  @Override
  protected int expectedBranchCount(boolean truthValue, TruthAssignment h) {
    return args.get(0).expectedBranchCount(!truthValue, h);
  }
}
