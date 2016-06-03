package expression.sentence;

import expression.Sort;
import logicalreasoner.inference.Inference;
import logicalreasoner.truthassignment.TruthAssignment;

import java.util.ArrayList;

/**
 * The Predicate class represents a logical predicate over
 * some constants (assume no objects are variables)
 * <p>
 * ie, (P x), (W x y z), and (H a) are all predicates
 */
public class Predicate extends Sentence {
  /**
   * Create a new Predicate object with the given name
   * @param n the name of the Predicate
   */
  public Predicate(String n, ArrayList<Sentence> vars) {
    super(vars, n, n, Sort.BOOLEAN);
  }

  public Sentence makeCopy() {
    ArrayList<Sentence> a = new ArrayList<>();
    args.forEach(arg -> a.add(arg.makeCopy()));
    return new Predicate(name, a);
  }

  public String toString() {
    if (TOSTRING == null)
      TOSTRING = print(true);
    return TOSTRING;
  }

  public String toSymbol() {
    if (TOSTRING == null)
      TOSTRING = print(true);
    return TOSTRING;
  }

  private String print(boolean prefix) {
    StringBuilder builder;
    if (prefix)
      builder = new StringBuilder().append(name).append("(");
    else
      builder = new StringBuilder().append("(").append(name).append(" ");

    String delim = prefix ? ", " : " ";
    ArrayList<Sentence> a = new ArrayList<>(args);
    if (!a.isEmpty())
      a.remove(a.size() - 1);

    a.forEach(arg -> {
      if (prefix)
        builder.append(arg.toSymbol()).append(delim);
      else
        builder.append(arg.toString()).append(delim);
    });

    if (!args.isEmpty())
      builder.append(args.get(args.size() - 1).toSymbol()).append(")");
    else
      builder.append(")");
    return builder.toString();
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
  public Sentence instantiate(Sentence c, Variable v) {
    ArrayList<Sentence> a = new ArrayList<>();
    args.forEach(arg -> a.add(arg.instantiate(c, v)));
    return new Predicate(name, a);
  }
}
