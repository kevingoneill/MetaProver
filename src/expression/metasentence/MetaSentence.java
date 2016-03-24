package expression.metasentence;

import expression.Expression;
import metareasoner.metainference.MetaInference;
import metareasoner.proof.Proof;

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

    public abstract MetaInference reasonForwards(Proof p, int inferenceNum);

    public abstract MetaInference reasonBackwards(Proof p, int inferenceNum);

    public int hashCode() { return toString().hashCode(); }

    public boolean equals(Object o) {
        if (o instanceof MetaSentence) {
            MetaSentence s = (MetaSentence)o;
            if (!s.name.equals(name) || s.args.size() != args.size())
                return false;

            for (int i = 0; i < args.size(); ++i) {
                if (!args.get(i).equals(s.args.get(i)))
                    return false;
            }

            return true;
        }
        return false;
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
