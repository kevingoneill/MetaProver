package expression.sentence;

import expression.Sort;
import logicalreasoner.inference.Branch;
import logicalreasoner.inference.Inference;
import logicalreasoner.truthassignment.TruthAssignment;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * The Iff class represents the logical iff
 * (if and only if) operator
 */
public class Iff extends Sentence {
  public static String NAME = "iff", SYMBOL = "⟷";

  public Iff(Sentence expr1, Sentence expr2) {
    super(new ArrayList<>(Arrays.asList(expr1, expr2)), NAME, SYMBOL, Sort.BOOLEAN);
  }

  public Boolean eval(TruthAssignment h) {
    Boolean antecedent = h.models(args.get(0)),
            consequent = h.models(args.get(1));

    if (antecedent == null || consequent == null)
      return null;
    return (antecedent && consequent) || (!antecedent && !consequent);
  }

  @Override
  public Inference reason(TruthAssignment h, int inferenceNum, int justificationNum) {
    h.setDecomposed(this);
    if (h.models(this)) {
      Branch b = new Branch(h, this, inferenceNum, justificationNum);
      TruthAssignment t = new TruthAssignment(-1),
              t1 = new TruthAssignment(-1);
      t.setTrue(args.get(0), inferenceNum);
      t.setTrue(args.get(1), inferenceNum);
      b.addBranch(t);
      t1.setFalse(args.get(0), inferenceNum);
      t1.setFalse(args.get(1), inferenceNum);
      b.addBranch(t1);
      return b;
    } else {
      Branch b = new Branch(h, this, inferenceNum, justificationNum);
      TruthAssignment t = new TruthAssignment(-1),
              t1 = new TruthAssignment(-1);
      t.setTrue(args.get(0), inferenceNum);
      t.setFalse(args.get(1), inferenceNum);
      b.addBranch(t);
      t1.setFalse(args.get(0), inferenceNum);
      t1.setTrue(args.get(1), inferenceNum);
      b.addBranch(t1);
      return b;
    }
  }
}
