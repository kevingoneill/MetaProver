package expression.sentence;

import expression.Sort;
import logicalreasoner.inference.Branch;
import logicalreasoner.inference.Decomposition;
import logicalreasoner.inference.Inference;
import logicalreasoner.truthassignment.TruthAssignment;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * The Implies class represents logical implication,
 * if X then Y
 * <p>
 * For example, (implies A B), (implies X Y)
 */
public class Implies extends Sentence {
  public static String NAME = "implies", SYMBOL = "⟶";

  public Implies(Sentence ifExpr, Sentence thenExpr) {
    super(new ArrayList<>(Arrays.asList(ifExpr, thenExpr)), NAME, SYMBOL, Sort.BOOLEAN);
  }

  public Boolean eval(TruthAssignment h) {
    Boolean antecedent = h.models(args.get(0)),
            consequent = h.models(args.get(1));

    //Return null if any atoms are unmapped
    if (antecedent == null || consequent == null)
      return null;
    return !(antecedent && !consequent);
  }

  @Override
  public Inference reason(TruthAssignment h, int inferenceNum, int justificationNum) {
    h.setDecomposed(this);
    if (h.models(this)) {
      Branch b = new Branch(h, this, inferenceNum, justificationNum);
      TruthAssignment t = new TruthAssignment(-1),
              t1 = new TruthAssignment(-1);
      t.setFalse(args.get(0), inferenceNum);
      b.addBranch(t);
      t1.setTrue(args.get(1), inferenceNum);
      b.addBranch(t1);
      return b;
    } else {
      Decomposition d = new Decomposition(h, this, inferenceNum, justificationNum);
      d.setTrue(args.get(0));
      d.setFalse(args.get(1));
      return d;
    }
  }
}
