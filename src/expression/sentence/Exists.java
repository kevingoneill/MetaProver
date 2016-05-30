package expression.sentence;

import expression.Sort;
import logicalreasoner.inference.Branch;
import logicalreasoner.inference.Decomposition;
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
        System.out.println(h.getConstants());

        if (h.getConstants().isEmpty()) {
          System.out.println("----------------CREATING NEW CONSTANT------------------");
          Decomposition d = new Decomposition(h, this, inferenceNum, justificationNum);
          d.setTrue(getSentence().instantiate(Constant.getNewUniqueConstant(), getVariable()));
          return d;
        }

        Branch b = new Branch(h, this, inferenceNum, justificationNum);
        h.getConstants().forEach(c -> {
          TruthAssignment t = new TruthAssignment();
          t.setTrue(getSentence().instantiate(c, getVariable()), inferenceNum);
          b.addBranch(t);
        });
        TruthAssignment t = new TruthAssignment();
        t.setTrue(getSentence().instantiate(Constant.getNewUniqueConstant(), getVariable()), inferenceNum);
        b.addBranch(t);
        return b;
      } else {
        Decomposition d = new Decomposition(h, this, inferenceNum, justificationNum);
        ArrayList<Sentence> a = new ArrayList<>();
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
  public Sentence instantiate(Constant c, Variable v) {
    if (v.equals(getVariable()))
      return getSentence().instantiate(c, v);

    return new Exists(getVariable(), getSentence().instantiate(c, v));
  }
}
