package expression.sentence;

import expression.Sort;
import logicalreasoner.inference.Decomposition;
import logicalreasoner.inference.Inference;
import logicalreasoner.truthassignment.TruthAssignment;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The ForAll Sentence is a quantifier over any number of variables,
 * with exactly one argument.
 */
public class ForAll extends Sentence {

  private Set<Constant> instantiations;
  private static Comparator<Constant> constantComparator =
          (c1, c2) -> {
            if (c1.getName().startsWith("#")) {
              if (c2.getName().startsWith("#"))
                return Integer.parseInt(c1.getName().replaceAll("#", "")) - Integer.parseInt(c2.getName().replaceAll("#", ""));
              return 1;
            }
            if (c2.getName().startsWith("#"))
              return -1;
            return 0;
          };


  public ForAll(Variable v, Sentence s) {
    super(new ArrayList<>(Arrays.asList(v, s)), "forAll", "∀", Sort.BOOLEAN);
    instantiations = new HashSet<>();
  }

  public ForAll(ForAll f) {
    super(new ArrayList<>(Arrays.asList(f.getVariable(), f.getSentence())), "forAll", "∀", Sort.BOOLEAN);
    instantiations = new HashSet<>();
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
        Decomposition d = new Decomposition(h, this, inferenceNum, justificationNum);
        //System.out.println(h.getConstants());
        List<Constant> a = h.getConstants().stream().filter(c ->
                !instantiations.contains(c)).collect(Collectors.toList());

        if (a.isEmpty())
          return null;

        Collections.sort(a, constantComparator);
        Constant c = a.get(0);
        Sentence s = getSentence().instantiate(c, getVariable());
        //System.out.println(s);
        d.setTrue(s);
        instantiations.add(c);

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

  public Set<Constant> getInstantiations() {
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

  /**
   * This method is overridden to ensure that equal statements
   * quantified over different variables are equal
   *
   * @return a unique hashcode for this Exists.
   */
  public int hashCode() {
    return instantiate(new Variable("", Sort.OBJECT), getVariable()).hashCode();
  }
}
