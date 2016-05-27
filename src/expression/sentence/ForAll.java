package expression.sentence;

import expression.Sort;
import logicalreasoner.inference.Decomposition;
import logicalreasoner.inference.Inference;
import logicalreasoner.truthassignment.TruthAssignment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * The ForAll Sentence is a quantifier over any number of variables,
 * with exactly one argument.
 */
public class ForAll extends Sentence {

  private Set<Constant> instantiations;

  public ForAll(Variable v, Sentence s) {
    super(new ArrayList<>(Arrays.asList(v, s)), "forAll", "∀", Sort.BOOLEAN);
    instantiations = new HashSet<>();
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
        Decomposition d = new Decomposition(h, this, inferenceNum, justificationNum);
        h.getConstants().stream().filter(c -> !instantiations.contains(c)).forEach(c -> {
          Sentence s = getSentence().instantiate(c, getVariable());
          System.out.println(s);
          d.setTrue(s);
          instantiations.add(c);
        });
        if (!d.getAdditions().isEmpty())
          return d;
      } else {
        Decomposition d = new Decomposition(h, this, inferenceNum, justificationNum);
        d.setTrue(new Exists(getVariable(), new Not(getSentence())));
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

    return new ForAll(getVariable(), getSentence().instantiate(c, v));
  }
}
