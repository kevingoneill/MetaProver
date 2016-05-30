package expression.sentence;

import expression.Sort;
import logicalreasoner.inference.Inference;
import logicalreasoner.truthassignment.TruthAssignment;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A Constant is a known object with a unique name.
 */
public class Constant extends Atom {

  public static Map<String, Constant> constants = new HashMap<>();
  private static long newConstants = 1;

  private Constant(String name, Sort s) {
    super(name, s);
  }

  private Constant(String name) {
    super(name, Sort.OBJECT);
  }

  public static Constant getConstant(String name, Sort s) {
    if (constants.containsKey(name)) {
      Constant c = constants.get(name);
      if (!c.getSort().equals(s))
        throw new RuntimeException("Cannot create a constant with an existing name");
      return c;
    }

    constants.put(name, new Constant(name, s));
    return constants.get(name);
  }

  public static Constant getNewUniqueConstant() {
    String name = "#" + newConstants;
    while (constants.containsKey(name)) { // Increment until a unique constant is found
      ++newConstants;
      name = "#" + newConstants;
    }
    constants.put(name, new Constant(name));
    return constants.get(name);
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
  public Sentence instantiate(Constant c, Variable v) {
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
