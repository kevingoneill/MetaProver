package expression.sentence;

import expression.Sort;
import logicalreasoner.inference.Decomposition;
import logicalreasoner.inference.ExistentialInstantiation;
import logicalreasoner.inference.Inference;
import logicalreasoner.truthassignment.TruthAssignment;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

/**
 * Exists is a quantified Sentence, which states that there must be
 * some constant, which when instantiated over its subsentence,
 * is satisfied.
 */
public class Exists extends Sentence {
  public static String NAME = "exists", SYMBOL = "∃";


  public Exists(Variable v, Sentence s) {
    super(Arrays.asList(v, s), NAME, SYMBOL, Sort.BOOLEAN);
    HASH_CODE = hashString(v).hashCode();
  }

  public String toString() {
    if (TOSTRING == null) {
      if (getVariable().getSort() == Sort.OBJECT)
        TOSTRING = symbol + getVariable() + getSentence();
      else
        TOSTRING = symbol + "(" + getVariable().getSort() + " " + getVariable() + ")" + getSentence();
    }
    return TOSTRING;
  }

  @Override
  public String toSExpression() {
    if (TOSEXPR == null)
      TOSEXPR = "(" + name + " (" + getVariable().getSort().getName() + " " + getVariable().toSExpression() + ") " + getSentence().toSExpression() + ")";
    return TOSEXPR;
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
    h.setDecomposed(this);
    if (h.models(this)) {
      //Decomposition d = new Decomposition(h, this, inferenceNum, justificationNum);
      //d.setTrue(getSentence().instantiate(Constant.getNewUniqueConstant(), getVariable()));

      return new ExistentialInstantiation(h, this, inferenceNum, justificationNum, Constant.getNewUniqueConstant(), getVariable());

      /*
       Set<Sentence> s = h.getConstants();
       if (s.isEmpty()) {
         Decomposition d = new Decomposition(h, this, inferenceNum, justificationNum);
         d.setTrue(getSentence().instantiate(Constant.getNewUniqueConstant(), getVariable()));
         return d;
       }
       return new BranchingExistentialInstantiation(h, this, inferenceNum, justificationNum, s);
       */
    } else {
      Decomposition d = new Decomposition(h, this, inferenceNum, justificationNum);
      d.setTrue(Sentence.makeSentence(ForAll.NAME, getVariable(),
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

    return Sentence.makeSentence(name, getVariable(), getSentence().instantiate(c, v));
  }

  @Override
  protected int expectedBranchCount(boolean truthValue, TruthAssignment h) {
    //This will be instantiated at most once
    if (truthValue)
      return getSentence().expectedBranchCount(true, h);

    // This will turn into a ForAll, leading to n times the expected branching statements
    return getSentence().expectedBranchCount(false, h) * Math.max(1, h.getConstants(getVariable().getSort()).size());
  }

  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (hashCode() == o.hashCode() && o instanceof ForAll) {
      ForAll f = (ForAll) o;
      if (f.getVariable().equals(getVariable()))
        return f.getSentence().equals(getSentence());
      else if (f.getVariable().getSort() == getVariable().getSort())
        return f.instantiate(getVariable(), f.getVariable()).equals(getSentence());
      return false;
    }
    return false;
  }
}
