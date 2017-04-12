package expression.sentence;

import expression.Sort;
import logicalreasoner.inference.Branch;
import logicalreasoner.inference.Decomposition;
import logicalreasoner.inference.Inference;
import logicalreasoner.truthassignment.TruthAssignment;

import java.util.Arrays;
import java.util.List;

/**
 * The And class represents the generalized logical conjunction
 * <p>
 * For example, (and A B), (and X Y Z)
 */
public class And extends Sentence {
  public static String NAME = "and", SYMBOL = "∧";

  public And(List<Sentence> a) {
    super(a, NAME, SYMBOL, Sort.BOOLEAN);
  }

  public And(Sentence... sentences) {
    super(Arrays.asList(sentences), NAME, SYMBOL, Sort.BOOLEAN);
  }

  public Boolean eval(TruthAssignment h) {
    //if any atoms are unmapped, return null
    if (args.contains(null))
      return null;

    return args.stream().map(arg -> arg.eval(h)).distinct().reduce(true, (a, b) -> {
      if (a == null || b == null)
        return null;
      return a && b;
    });
  }

  @Override
  public Inference reason(TruthAssignment h, int inferenceNum, int justificationNum) {
    h.setDecomposed(this);
    if (h.models(this)) {
      Decomposition d = new Decomposition(h, this, inferenceNum, justificationNum);
      args.forEach(d::setTrue);
      return d;
    } else {
      if (args.stream().allMatch(a -> a.equals(args.get(0)))) {
        Decomposition d = new Decomposition(h, this, inferenceNum, justificationNum);
        d.setFalse(args.get(0));
        return d;
      }
      Branch b = new Branch(h, this, inferenceNum, justificationNum);
      args.forEach(arg -> {
        TruthAssignment t = new TruthAssignment(-1);
        t.setFalse(arg, inferenceNum);
        b.addBranch(t);
      });
      return b;
    }
  }

  @Override
  protected int expectedBranchCount(boolean truthValue, TruthAssignment h) {
    // This And will Decompose, not Branch
    if (truthValue)
      return args.stream().mapToInt(a -> a.expectedBranchCount(true, h)).sum();

    // This And will lead to a branch
    return args.size() + args.stream().mapToInt(a -> a.expectedBranchCount(false, h)).sum();
  }
}
