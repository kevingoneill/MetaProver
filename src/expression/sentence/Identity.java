package expression.sentence;

import expression.Sort;
import logicalreasoner.inference.Decomposition;
import logicalreasoner.inference.Inference;
import logicalreasoner.truthassignment.TruthAssignment;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * The Identity Sentence describes equality of two Sentences.
 * Traditionally, these sentences are Constants, Variables, or Functions.
 */
public class Identity extends Sentence {

  public static String NAME = "=", SYMBOL = "=";

  public Identity(Sentence expr1, Sentence expr2) {
    super(new ArrayList<>(Arrays.asList(expr1, expr2)), NAME, SYMBOL, Sort.BOOLEAN);
  }

  @Override
  public Boolean eval(TruthAssignment h) {
    return null;
  }

  @Override
  public Inference reason(TruthAssignment h, int inferenceNum, int justificationNum) {
    h.setDecomposed(this);


    if (!h.models(this) && args.get(0).equals(args.get(1))) {
      Decomposition d = new Decomposition(h, this, inferenceNum, justificationNum);
      d.setTrue(this);
      return d;
    }

    return null;
  }

  @Override
  protected int expectedBranchCount(boolean truthValue, TruthAssignment h) {
    return 0;
  }
}
