package expression.sentence;

import expression.Sort;

import java.util.*;

/**
 * A Constant is a known object with a unique name.
 */
public class Constant extends Atom {

  public static Map<String, Constant> constants = new HashMap<>();
  private static long newConstants = 1;

  public static Comparator<Sentence> constantComparator = (c1, c2) -> {
    if (c1 instanceof Constant) {
      if (c2 instanceof Constant) {
        if (c1.getName().startsWith("#")) {
          if (c2.getName().startsWith("#"))
            return Integer.parseInt(c1.getName().replaceAll("#", "")) - Integer.parseInt(c2.getName().replaceAll("#", ""));
          return 1;
        }
        if (c2.getName().startsWith("#"))
          return -1;
        return c2.getName().charAt(0) - c1.getName().charAt(0);
      }
      return -1;
    }
    if (c2 instanceof Constant)
      return 1;
    return 0;
  };


  protected Constant(String name, Sort s) {
    super(name, s);
  }

  protected Constant(String name) {
    super(name, Sort.OBJECT);
  }

  public static boolean constantExists(String name) {
    return constants.containsKey(name);
  }

  public static boolean constantExists(String name, Sort s) {
    Constant c = constants.get(name);
    return c != null && c.getSort() == s;
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

  public static Constant getConstant(String name) {
    return constants.get(name);
  }

  public static void clearConstants() {
    constants.clear();
    newConstants = 1;
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

  @Override
  public Set<Sentence> getConstants() {
    return Collections.singleton(this);
  }

  @Override
  public Sentence instantiate(Sentence c, Variable v) {
    return this;
  }
}
