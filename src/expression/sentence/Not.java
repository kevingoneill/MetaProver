package expression.sentence;

import expression.Sort;
import logicalreasoner.inference.Decomposition;
import logicalreasoner.inference.Inference;
import logicalreasoner.truthassignment.TruthAssignment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

/**
 * The Not class represents a logical negation
 * <p>
 * (not A)
 */
public class Not extends Sentence {

  public Not(Sentence e) {
    super(new ArrayList<>(Arrays.asList(e)), "not", "¬", Sort.BOOLEAN);
  }

  public boolean equals(Object o) {
    if (o instanceof Not)
      return super.equals(o);
    return false;
  }

  public Sentence makeCopy() {
    return new Not(args.get(0).makeCopy());
  }

  public String toSymbol() {
    //if (args.get(0) instanceof Proposition || args.get(0) instanceof Predicate)
    return symbol + args.get(0).toSymbol();
    //return symbol + "(" + args.get(0).toSymbol() + ")";
  }

  public Boolean eval(TruthAssignment h) {
    //if (h.isMapped(this))
    //    return h.models(this);

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
  public Sentence instantiate(Sentence c, Variable v) {
    return new Not(args.get(0).instantiate(c, v));
  }
}
