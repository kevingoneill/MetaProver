package expression.sentence;

import expression.Sort;
import logicalreasoner.inference.Inference;
import logicalreasoner.truthassignment.TruthAssignment;

import java.util.HashSet;
import java.util.Set;

/**
 * A variable is an unbound, or quantified, placeholder for a Sort.OBJECT.
 * It can be instantiated with any subtype of OBJECT, or it can be restricted to
 * a subsort of another sort.
 */
public class Variable extends Atom {
  public Variable(String name, Sort s) {
    super(name, s);
  }

  public String toString() {
    return name;
  }

  public String toSymbol() {
    return name;
  }

  @Override
  public Set<Constant> getConstants() {
    return new HashSet<>();
  }

  @Override
  public Sentence instantiate(Sentence c, Variable v) {
    if (this.equals(v))
      return c;
    return this;
  }

  @Override
  public Boolean eval(TruthAssignment h) {
    return null;
  }

  @Override
  public Inference reason(TruthAssignment h, int inferenceNum, int justificationNum) {
    return null;
  }
}
