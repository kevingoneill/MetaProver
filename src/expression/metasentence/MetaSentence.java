package expression.metasentence;

import expression.Expression;
import metareasoner.metainference.MetaInference;
import metareasoner.proof.Proof;

import java.util.ArrayList;
import java.util.Set;

/**
 * The MetaSentence represents a metalogical expression
 */
public abstract class MetaSentence extends Expression {
    protected ArrayList<Expression> args;
    protected Set<TruthAssignmentVar> vars;

    protected MetaSentence(ArrayList<Expression> a, String n, String s, Set<TruthAssignmentVar> v) {
        super(n, s);
        args = a;
        vars = v;
    }

    public abstract MetaInference reasonForwards(Proof p, int inferenceNum);

    public abstract MetaInference reasonBackwards(Proof p, int inferenceNum);

    public int hashCode() { return toString().hashCode(); }

    public Set<TruthAssignmentVar> getVars() {
        return vars;
    }

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

        vars.forEach(v -> builder.append("∀").append(v.toSymbol()));
        if (!vars.isEmpty())
            builder.append(" ");

        builder.append("[").append(name);
        args.forEach(arg -> {
            if (arg instanceof MetaSentence)
                builder.append(" ").append(((MetaSentence)arg).toString(false));
            else
                builder.append(" ").append(arg);
        });
        builder.append("]");
        return builder.toString();
    }

    public String toString(boolean isTopLevel) {
        if (isTopLevel)
            return toString();

        StringBuilder builder = new StringBuilder();
        builder.append("[").append(name);
        args.forEach(arg -> {
            if (arg instanceof MetaSentence)
                builder.append(" ").append(((MetaSentence)arg).toString(isTopLevel));
            else
                builder.append(" ").append(arg);
        });
        builder.append("]");
        return builder.toString();
    }

    public String toSymbol() {
        if (!args.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            vars.forEach(v -> builder.append("∀").append(v.toSymbol()));
            if (!vars.isEmpty())
                builder.append(" ");

            builder.append("[");
            for (int i = 0; i < args.size() - 1; ++i) {
                builder.append(args.get(i).toSymbol()).append(" ").append(symbol).append(" ");
            }
            builder.append(args.get(args.size() - 1).toSymbol()).append("]");
            return builder.toString();
        }
        return symbol;
    }

    public String toSymbol(boolean isTopLevel) {
        if (isTopLevel)
            return toSymbol();

        StringBuilder builder = new StringBuilder();

        builder.append("[");
        for (int i = 0; i < args.size() - 1; ++i) {
            if (args.get(i) instanceof MetaSentence)
                builder.append(((MetaSentence)args.get(i)).toSymbol(false)).append(" ").append(symbol).append(" ");
            else
                builder.append(args.get(i).toSymbol()).append(" ").append(symbol).append(" ");
        }
        builder.append(args.get(args.size() - 1).toSymbol()).append("]");

        return builder.toString();
    }
}
