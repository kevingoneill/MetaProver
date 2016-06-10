package expression.sentence;

import expression.Sort;
import logicalreasoner.inference.Decomposition;
import logicalreasoner.inference.Inference;
import logicalreasoner.inference.UniversalInstantiation;
import logicalreasoner.truthassignment.TruthAssignment;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The ForAll Sentence is a quantifier over any number of variables,
 * with exactly one argument.
 */
public class ForAll extends Sentence {

  // TODO: store list of un-instantiated constants instead (eliminate leaf traversal)
  private Set<Sentence> instantiations;

  public ForAll(Variable v, Sentence s) {
    super(new ArrayList<>(Arrays.asList(v, s)), "forAll", "∀", Sort.BOOLEAN);
    instantiations = new HashSet<>();
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
  public Sentence makeCopy() {
    return new ForAll(getVariable(), getSentence().makeCopy());
  }

  @Override
  public Boolean eval(TruthAssignment h) {

    return null;
  }

  @Override
  public Inference reason(TruthAssignment h, int inferenceNum, int justificationNum) {
    if (h.isMapped(this)) {
      if (h.models(this)) {
        List<Sentence> a = h.getLeaves().flatMap(l -> l.getConstants().stream()).filter(c ->
                !instantiations.contains(c)).distinct().collect(Collectors.toList());

        if (a.isEmpty()) {
          return null;
        }

        //Collections.sort(a, Constant.constantComparator);
        //Sentence c = a.get(0);

        UniversalInstantiation i = new UniversalInstantiation(h, this, inferenceNum, justificationNum, a, getVariable());
        //instantiations.add(c);
        instantiations.addAll(a);
        return i;
      } else {
        h.setDecomposed(this);
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
  public Set<Sentence> getConstants() {
    return getSentence().getConstants();
  }

  public Set<Sentence> getInstantiations() {
    return instantiations;
  }

  @Override
  public Sentence instantiate(Sentence c, Variable v) {
    if (v.equals(getVariable()))
      return getSentence().instantiate(c, v);

    return new ForAll(getVariable(), getSentence().instantiate(c, v));
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
