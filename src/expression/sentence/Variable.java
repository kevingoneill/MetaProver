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
  protected static Variable EMPTY_VAR = new Variable("_#EMPTY#VAR#_", Sort.OBJECT);

  public Variable(String name, Sort s) {
    super(name, s);
  }

  public String toString() {
    return name;
    //return "(" + getSort() + " " + name + ")";
  }

  public String toSExpression() {
    return name;
  }

  @Override
  public Set<Sentence> getConstants() {
    return new HashSet<>();
  }

  @Override
  public boolean equals(Object o) {
    return this == o || (o instanceof Variable && o.toString().equals(toString()) && ((Variable) o).getSort() == getSort());
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
