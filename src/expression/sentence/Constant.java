package expression.sentence;

import expression.Sort;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;

/**
 * A Constant is a known object with a unique name.
 */
public class Constant extends Atom {

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
    return Sentence.instances.containsKey(name);
  }

  public static boolean constantExists(String name, Sort s) {
    Sentence c = Sentence.instances.get(name);
    return c != null && c instanceof Constant && c.getSort() == s;
  }

  public static Constant getConstant(String name, Sort s) {
    Sentence c = Sentence.instances.get(name);
    if (c != null) {
      if (!(c instanceof Constant) || !c.getSort().equals(s))
        throw new RuntimeException("Cannot create a constant with an existing name");
      return (Constant) c;
    }
    c = new Constant(name, s);
    Function.addDeclaration(name, s, new ArrayList<>());
    Sentence.instances.put(name, c);
    return (Constant) c;
  }

  public static Constant getConstant(String name) {
    return (Constant) Sentence.instances.get(name);
  }

  public static Constant removeConstant(String name) {
    if (!constantExists(name))
      return null;
    Function.removeDeclaration(name);
    return (Constant) Sentence.instances.remove(name);
  }

  public static void clearConstants() {
    newConstants = 1;
  }

  public static Constant getNewUniqueConstant() {
    String name = getNextConstantName();
    Constant c = new Constant(name);
    Sentence.instances.put(name, c);
    Function.addDeclaration(name, c.getSort(), new ArrayList<>());
    return c;
  }

  public static Constant getNewUniqueConstant(Sort s) {
    String name = getNextConstantName();
    Constant c = new Constant(name, s);
    Sentence.instances.put(name, c);
    Function.addDeclaration(name, c.getSort(), new ArrayList<>());
    return c;
  }

  private static String getNextConstantName() {
    String name = "#" + newConstants;
    while (Sentence.instances.containsKey(name)) { // Increment until a unique constant is found
      ++newConstants;
      name = "#" + newConstants;
    }
    return name;
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
