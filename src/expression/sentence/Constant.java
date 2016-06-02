package expression.sentence;

import expression.Sort;
import logicalreasoner.inference.Inference;
import logicalreasoner.truthassignment.TruthAssignment;

import java.util.*;

/**
 * A Constant is a known object with a unique name.
 */
public class Constant extends Atom {

  public static Map<String, Constant> constants = new HashMap<>();
  private static long newConstants = 1;
  public static Comparator<Constant> constantComparator = (c1, c2) -> {
    if (c1.getName().startsWith("#")) {
      if (c2.getName().startsWith("#"))
        return Integer.parseInt(c1.getName().replaceAll("#", "")) - Integer.parseInt(c2.getName().replaceAll("#", ""));
      return 1;
    }
    if (c2.getName().startsWith("#"))
      return -1;
    return c1.getName().charAt(0) - c2.getName().charAt(0);
  };


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
    if (TOSTRING == null)
      TOSTRING = name;
    return TOSTRING;
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
