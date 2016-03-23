package expression.metasentence;

import expression.Expression;

import java.util.ArrayList;

/**
 * The MetaSentence represents a metalogical expression
 */
public abstract class MetaSentence extends Expression {
    ArrayList<Expression> args;

    protected MetaSentence(ArrayList<Expression> a, String n, String s) {
        super(n, s);
        args = a;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[").append(name);
        args.forEach(arg -> builder.append(" ").append(arg));
        builder.append("]");
        return builder.toString();
    }

    public String toSymbol() {
        if (!args.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            builder.append("(");
            for (int i = 0; i < args.size() - 1; ++i) {
                builder.append(args.get(i).toSymbol()).append(" ").append(symbol).append(" ");
            }
            builder.append(args.get(args.size() - 1).toSymbol()).append(")");
            return builder.toString();
        }
        return symbol;
    }
}
