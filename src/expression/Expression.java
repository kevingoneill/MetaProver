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

    public abstract String toSymbol();
}
