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
  public Iff(Sentence expr1, Sentence expr2) {
    super(new ArrayList<>(Arrays.asList(expr1, expr2)), "iff", "⟷", Sort.BOOLEAN);
  }

  public Sentence makeCopy() {
    return new Iff(args.get(0).makeCopy(), args.get(1).makeCopy());
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
    if (h.isMapped(this)) {
      if (h.models(this)) {
        Branch b = new Branch(h, this, inferenceNum, justificationNum);
        TruthAssignment t = new TruthAssignment();
        t.setTrue(args.get(0), inferenceNum);
        t.setTrue(args.get(1), inferenceNum);
        b.addBranch(t);
        TruthAssignment t1 = new TruthAssignment();
        t1.setFalse(args.get(0), inferenceNum);
        t1.setFalse(args.get(1), inferenceNum);
        b.addBranch(t1);
        return b;
      } else {
        Branch b = new Branch(h, this, inferenceNum, justificationNum);
        TruthAssignment t = new TruthAssignment();
        t.setTrue(args.get(0), inferenceNum);
        t.setFalse(args.get(1), inferenceNum);
        b.addBranch(t);
        TruthAssignment t1 = new TruthAssignment();
        t1.setFalse(args.get(0), inferenceNum);
        t1.setTrue(args.get(1), inferenceNum);
        b.addBranch(t1);
        return b;
      }
    }

    return null;
  }

  @Override
  public Sentence instantiate(Sentence c, Variable v) {
    return new Iff(args.get(0).instantiate(c, v), args.get(1).instantiate(c, v));
  }
}
