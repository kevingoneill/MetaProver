package expression.sentence;

import expression.Sort;
import logicalreasoner.inference.Inference;
import logicalreasoner.truthassignment.TruthAssignment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * The Predicate class represents a logical predicate over
 * some constants (assume no objects are variables)
 * <p>
 * ie, (P x), (W x y z), and (H a) are all predicates
 */
public class Predicate extends Sentence {
  /**
   * Create a new Proposition object with the given name
   *
   * @param n the name of the Proposition
   */
  public Predicate(String n, ArrayList<Sentence> vars) {
    super(vars, n, n, Sort.BOOLEAN);
  }

  public String toString() {
    return print(true);
  }

  public String toSymbol() {
    return print(true);
  }

  private String print(boolean prefix) {
    StringBuilder builder;
    if (prefix)
      builder = new StringBuilder().append(name).append("(");
    else
      builder = new StringBuilder().append("(").append(name).append(" ");

    String delim = prefix ? ", " : " ";
    Iterator<Sentence> itr = args.iterator();
    int i = 0;
    while (i < args.size() - 1 && itr.hasNext()) {
      ++i;
      if (prefix)
        builder.append(itr.next().toSymbol()).append(delim);
      else
        builder.append(itr.next().toString()).append(delim);
    }

    if (itr.hasNext())
      builder.append(itr.next().toSymbol()).append(")");
    else
      builder.append(")");
    return builder.toString();
  }

  @Override
  public Set<Constant> getConstants() {
    Set<Constant> s = new HashSet<>();
    args.stream().filter(a -> a instanceof Constant).forEach(a -> s.add((Constant) a));
    return s;
  }

  /**
   * Return the value of this in the given TruthAssignment
   *
   * @param h the TruthAssignment used to evaluate this
   * @return null if h does not contain this,
   * true if h models this, or false if h models (not this)
   */
  public Boolean eval(TruthAssignment h) {
    if (h.isMapped(this))
      return h.models(this);

    return null;
  }

  @Override
  public Inference reason(TruthAssignment h, int inferenceNum, int justificationNum) {
    if (!h.isDecomposed(this))
      h.setDecomposed(this);
    return null;
  }

  @Override
  public Sentence instantiate(Constant c, Variable v) {
    ArrayList<Sentence> a = new ArrayList<>();
    args.forEach(arg -> a.add(arg.instantiate(c, v)));
    return new Predicate(name, a);
  }
}
