package expression.sentence;

import expression.Sort;
import logicalreasoner.truthassignment.TruthAssignment;

import java.util.Collections;
import java.util.Set;

/**
 * The BooleanSentence represents logical tautology and contradiction
 */
public class BooleanSentence extends Atom {
  public static final BooleanSentence TRUE = new BooleanSentence(true),
          FALSE = new BooleanSentence(false);

  private boolean value;

  private BooleanSentence(boolean b) {
    super(b ? "⊤" : "⊥", Sort.BOOLEAN);
    value = b;
  }

  @Override
  public String toSExpression() {
    return Boolean.toString(value);
  }

  @Override
  public String toString() {
    return name;
  }

  public Boolean eval(TruthAssignment h) {
    return value;
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
