package expression.sentence;

import expression.Sort;
import logicalreasoner.inference.Decomposition;
import logicalreasoner.inference.Inference;
import logicalreasoner.inference.UniversalInstantiation;
import logicalreasoner.truthassignment.TruthAssignment;
import logicalreasoner.truthassignment.TruthValue;

import java.util.*;

/**
 * The ForAll Sentence is a quantifier over any number of variables,
 * with exactly one argument.
 */
public class ForAll extends Sentence {
  public static String NAME = "forAll", SYMBOL = "∀";

  public ForAll(Variable v, Sentence s) {
    super(new ArrayList<>(Arrays.asList(v, s)), NAME, SYMBOL, Sort.BOOLEAN);
    HASH_CODE = instantiate(Variable.EMPTY_VAR, getVariable()).hashCode();
  }

  public String toString() {
    if (TOSTRING == null)
      TOSTRING = symbol + getVariable() + getSentence();
    return TOSTRING;
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
    TruthValue v = h.getTruthValue(this);
    if (v == null)
      return null;

    if (v.isModelled()) {
      List<Sentence> a = new ArrayList<>(v.getUninstantiatedConstants());
      if (a.isEmpty())
        return null;
      UniversalInstantiation i = new UniversalInstantiation(h, this, inferenceNum, justificationNum, a, getVariable());
      v.getInstantiatedConstants().addAll(a);
      v.getUninstantiatedConstants().clear();
      return i;
    } else {
      h.setDecomposed(this);
      Decomposition d = new Decomposition(h, this, inferenceNum, justificationNum);
      //d.setTrue(new Exists(getVariable(), new Not(getSentence())));
      d.setTrue(Sentence.makeSentence(Exists.NAME, getVariable(),
              Sentence.makeSentence(Not.NAME, Collections.singletonList(getSentence()))));
      return d;
    }
  }

  @Override
  public boolean isQuantifier() {
    return true;
  }

  @Override
  public Set<Sentence> getConstants() {
    return getSentence().getConstants();
  }

  @Override
  public Sentence instantiate(Sentence c, Variable v) {
    if (v.equals(getVariable()))
      return getSentence().instantiate(c, v);

    //return new ForAll(getVariable(), getSentence().instantiate(c, v));
    return Sentence.makeSentence(name, getVariable(), getSentence().instantiate(c, v));
  }

  public boolean equals(Object o) {
    if (o instanceof ForAll) {
      ForAll f = (ForAll) o;
      if (f.getVariable().equals(getVariable()))
        return f.getSentence().equals(getSentence());
      return f.instantiate(getVariable(), f.getVariable()).equals(getSentence());
    }
    return false;
  }

}
