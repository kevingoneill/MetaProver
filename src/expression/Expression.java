package expression;

import java.io.Serializable;

/**
 * An Expression is a statement which makes a claim or
 * captures an idea or object.
 */
public abstract class Expression implements Comparable<Expression>, Serializable {
  protected String name, symbol;
  protected Integer HASH_CODE = null;
  protected String TOSTRING = null, TOSEXPR = null;

  public Expression(String n, String s) {
    name = n;
    symbol = s;
  }

  public String toString() {
    if (TOSTRING == null)
      TOSTRING = name;
    return TOSTRING;
  }

  public String toSExpression() {
    if (TOSEXPR == null)
      TOSEXPR = symbol;
    return TOSEXPR;
  }

  public String getName() {
    return name;
  }

  public String getSymbol() {
    return symbol;
  }

  public int hashCode() {
    if (HASH_CODE == null) {
      HASH_CODE = toString().hashCode();
    }
    return HASH_CODE;
  }

  public boolean equals(Object o) {
    if (o instanceof Expression)
      return this.name.equals(((Expression) o).name);

    return false;
  }

  public int compareTo(Expression e) {
    return name.compareTo(e.getName());
  }
}
