package expression.sentence;

import expression.Sort;

import java.util.ArrayList;

/**
 * An atom is a logical Sentence with no arguments. It can be a
 * proposition, a constant, or a variable.
 */
public abstract class Atom extends Sentence {
  public Atom(String name, Sort s) {
    super(new ArrayList<>(), name, name, s);
  }
}
