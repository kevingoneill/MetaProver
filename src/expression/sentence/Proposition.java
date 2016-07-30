package expression.sentence;

import expression.Sort;

import java.util.Collections;
import java.util.Set;

/**
 * The Proposition class represents a single atomic sentence.
 * It can be a proposition, constant, or variable.
 */
public class Proposition extends Constant {

  /**
   * Create a new Proposition object with the given name
   *
   * @param n the name of the Proposition
   */
  public Proposition(String n) {
    super(n, Sort.BOOLEAN);
  }

  @Override
  public String toSExpression() {
    return name;
  }

  @Override
  public String toString() {
    return name;
  }

  @Override
  public Set<Sentence> getConstants() {
    return Collections.emptySet();
  }

  @Override
  public Sentence instantiate(Sentence c, Variable v) {
    return this;
  }
}
