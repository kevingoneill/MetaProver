package expression.sentence;

import expression.Sort;
import logicalreasoner.inference.Decomposition;
import logicalreasoner.inference.ExistentialInstantiation;
import logicalreasoner.inference.Inference;
import logicalreasoner.truthassignment.TruthAssignment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

/**
 * Created by kevin on 5/27/16.
 */
public class Exists extends Sentence {

  public Exists(Variable v, Sentence s) {
    super(new ArrayList<>(Arrays.asList(v, s)), "exists", "∃", Sort.BOOLEAN);
    HASH_CODE = instantiate(new Variable("", Sort.OBJECT), getVariable()).hashCode();
  }

  public String toSymbol() {
    return symbol + getVariable().toSymbol() + getSentence().toSymbol();
  }

  public Sentence getSentence() {
    return args.get(1);
  }

  public Variable getVariable() {
    return (Variable) args.get(0);
  }

  @Override
  public Boolean eval(TruthAssignment h) {
    return null;
  }

  @Override
  public Inference reason(TruthAssignment h, int inferenceNum, int justificationNum) {
    if (h.isMapped(this)) {
      if (h.models(this)) {
        Set<Constant> s = h.getConstants();
        if (s.isEmpty()) {
          Decomposition d = new Decomposition(h, this, inferenceNum, justificationNum);
          d.setTrue(getSentence().instantiate(Constant.getNewUniqueConstant(), getVariable()));
          return d;
        }
        return new ExistentialInstantiation(h, this, inferenceNum, justificationNum, s);
      } else {
        Decomposition d = new Decomposition(h, this, inferenceNum, justificationNum);
        d.setTrue(new ForAll(getVariable(), new Not(getSentence())));
        return d;
      }
    }

    return null;
  }

  @Override
  public boolean isQuantifier() {
    return true;
  }

  @Override
  public Set<Constant> getConstants() {
    return getSentence().getConstants();
  }

  @Override
  public Sentence instantiate(Sentence c, Variable v) {
    if (v.equals(getVariable()))
      return getSentence().instantiate(c, v);

    return new Exists(getVariable(), getSentence().instantiate(c, v));
  }

  public boolean equals(Object o) {
    if (o instanceof Exists) {
      Exists e = (Exists) o;
      if (e.getVariable().equals(getVariable()))
        return e.getSentence().equals(getSentence());
      return e.instantiate(getVariable(), e.getVariable()).equals(getSentence());
    }
    return false;
  }
}
