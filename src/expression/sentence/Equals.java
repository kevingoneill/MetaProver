package expression.sentence;

import expression.Sort;
import logicalreasoner.inference.Inference;
import logicalreasoner.truthassignment.TruthAssignment;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * The Equals Sentence describes equality of two Sentences.
 * Traditionally, these sentences are Constants, Variables, or Functions.
 */
public class Equals extends Sentence {

  public static String NAME = "=", SYMBOL = "=";

  public Equals(Sentence expr1, Sentence expr2) {
    super(new ArrayList<>(Arrays.asList(expr1, expr2)), NAME, SYMBOL, Sort.BOOLEAN);
  }

  @Override
  public Boolean eval(TruthAssignment h) {
    return null;
  }

  @Override
  public Inference reason(TruthAssignment h, int inferenceNum, int justificationNum) {
    h.setDecomposed(this);
    return null;
  }

  @Override
  protected int expectedBranchCount(boolean truthValue, TruthAssignment h) {
    return 0;
  }
}
