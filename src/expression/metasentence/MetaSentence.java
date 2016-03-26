package expression.metasentence;

import expression.Expression;
import metareasoner.metainference.MetaInference;
import metareasoner.proof.Proof;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * The MetaSentence represents a metalogical expression
 */
public abstract class MetaSentence extends Expression {
    protected ArrayList<Expression> args;
    protected HashMap<TruthAssignmentVar, Boolean> vars;

    protected MetaSentence(ArrayList<Expression> a, String n, String s, Set<TruthAssignmentVar> v) {
        super(n, s);
        args = a;
        vars = new HashMap<>();
        v.forEach(var -> vars.put(var, displayQuantifier(var)));
    }

    public abstract MetaInference reasonForwards(Proof p, int inferenceNum);

    public abstract MetaInference reasonBackwards(Proof p, int inferenceNum);

    public int hashCode() { return toString().hashCode(); }

    public Set<TruthAssignmentVar> getVars() {
        return vars.keySet();
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

        vars.forEach((v, b) -> {
            if(b)
                builder.append("∀").append(v.toSymbol());
        });
        if (!vars.isEmpty())
            builder.append(" ");

        builder.append("[").append(name);
        args.forEach(arg -> {
            if (arg instanceof MetaSentence) {
                MetaSentence m = (MetaSentence)arg;
                Set<TruthAssignmentVar> s = m.getVars();
                s.removeIf(e -> vars.get(e));
                builder.append(" ").append(m.toString(false));
            } else
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
                builder.append(" ").append(((MetaSentence)arg).toString(false));
            else
                builder.append(" ").append(arg);
        });
        builder.append("]");
        return builder.toString();
    }

    public String toSymbol() {
        if (!args.isEmpty()) {
            StringBuilder builder = new StringBuilder();

            vars.forEach((v, b) -> {
                if(b)
                    builder.append("∀").append(v.toSymbol());
            });
            if (vars.keySet().stream().anyMatch(vars::get))
                builder.append(" ");

            //builder.append("[");
            for (int i = 0; i < args.size() - 1; ++i) {
                if (args.get(i) instanceof MetaSentence) {
                    MetaSentence m = (MetaSentence)args.get(i);
                    Set<TruthAssignmentVar> s = m.getVars();
                    s.removeIf(e -> vars.get(e));
                    builder.append(m.toSymbol(false, s)).append(" ").append(symbol).append(" ");
                } else
                    builder.append(args.get(i).toSymbol()).append(" ").append(symbol).append(" ");
            }

            if (args.get(args.size() - 1) instanceof MetaSentence) {
                MetaSentence m = (MetaSentence)args.get(args.size() - 1);
                Set<TruthAssignmentVar> s = m.getVars();
                s.removeIf(e -> vars.get(e));
                builder.append(m.toSymbol(false, s));
            } else
                builder.append(args.get(args.size() - 1).toSymbol()); //.append("]");

            return builder.toString();
        }
        return symbol;
    }

    public String toSymbol(boolean isTopLevel, Set<TruthAssignmentVar> unprintedVars) {
        if (isTopLevel)
            return toSymbol();

        StringBuilder builder = new StringBuilder();
        unprintedVars.forEach(v -> builder.append("∀").append(v.toSymbol()));

        builder.append("[");
        for (int i = 0; i < args.size() - 1; ++i) {
            if (args.get(i) instanceof MetaSentence)
                builder.append(((MetaSentence)args.get(i)).toSymbol(false, new HashSet<>())).append(" ").append(symbol).append(" ");
            else
                builder.append(args.get(i).toSymbol()).append(" ").append(symbol).append(" ");
        }

        if (args.get(args.size() - 1) instanceof MetaSentence)
            builder.append(((MetaSentence)args.get(args.size() - 1)).toSymbol(false, new HashSet<>())).append("]");
        else
            builder.append(args.get(args.size() - 1).toSymbol()).append("]");

        return builder.toString();
    }

    protected boolean displayQuantifier(TruthAssignmentVar v) {
        boolean firstMatch = false, secondMatch = false;

        for(Expression a : args) {
            if (a instanceof MetaSentence) {
                MetaSentence s = (MetaSentence)a;
                //System.out.println(s + " :- " + s.getVars() + " ?= " + v);
                if (s.getVars().contains(v)) {
                    //System.out.println();
                    if (!firstMatch)
                        firstMatch = true;
                    else {
                        secondMatch = true;
                        break;
                    }
                }
            } else {
                if (!firstMatch)
                    firstMatch = true;
                else {
                    secondMatch = true;
                    break;
                }
            }
        }

        //System.out.println("displayQuantifier: " + v + " in MetaSentence: " + symbol + " = " + firstMatch + " " + secondMatch + " " + (args.size() == 1));
        return firstMatch && (args.size() == 1 || secondMatch);
    }
}
