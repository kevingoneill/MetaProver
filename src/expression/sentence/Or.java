package expression.sentence;

import expression.Sort;
import logicalreasoner.inference.Branch;
import logicalreasoner.inference.Decomposition;
import logicalreasoner.inference.Inference;
import logicalreasoner.truthassignment.TruthAssignment;

import java.util.ArrayList;

/**
 * The Or class represents the generalized logical disjunction
 * <p>
 * For example, (or A B), (or X Y Z)
 */
public class Or extends Sentence {
  public static String NAME = "or", SYMBOL = "∨";

  public Or(ArrayList<Sentence> a) {
    super(a, NAME, SYMBOL, Sort.BOOLEAN);
  }

  public Boolean eval(TruthAssignment h) {
    //return pre-mapped value for this
    //if (h.isMapped(this))
    //    return h.models(this);

    //if any atoms are unmapped, return null
    if (args.contains(null))
      return null;

    //Create a new mapping in h for this with newly computed value
    //boolean val =
    return args.stream().anyMatch(arg -> arg.eval(h));
    //h.set(this, val);
    //return val;
  }

  @Override
  public Inference reason(TruthAssignment h, int inferenceNum, int justificationNum) {
    h.setDecomposed(this);
    if (h.models(this)) {
      if (args.stream().allMatch(a -> a.equals(args.get(0)))) {
        Decomposition d = new Decomposition(h, this, inferenceNum, justificationNum);
        d.setTrue(args.get(0));
        return d;
      }
      Branch b = new Branch(h, this, inferenceNum, justificationNum);
      args.forEach(arg -> {
        TruthAssignment t = new TruthAssignment(-1);
        t.setTrue(arg, inferenceNum);
        b.addBranch(t);
      });
      return b;
    } else {
      Decomposition d = new Decomposition(h, this, inferenceNum, justificationNum);
      args.forEach(d::setFalse);
      return d;
    }
  }
}
