package expression;

/**
 * Created by kevin on 3/22/16.
 */
public abstract class Expression {
  protected String name, symbol;

  public Expression(String n, String s) {
    name = n;
    symbol = s;
  }

  public String toString() {
    return name;
  }

  public abstract String toSymbol();

  public String getName() {
    return name;
  }

  public String getSymbol() {
    return symbol;
  }

  public int hashCode() {
    return toString().hashCode();
  }

  public boolean equals(Object o) {
    if (o instanceof Expression)
      return this.name.equals(((Expression) o).name);

    return false;
  }
}
