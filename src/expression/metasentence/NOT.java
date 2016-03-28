package expression.metasentence;

import metareasoner.metainference.MetaInference;
import metareasoner.proof.Proof;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

/**
 * The NOT class represents the Meta-logical NOT expression,
 * taking a single MetaSentence as an argument
 */
public class NOT extends MetaSentence {
    public NOT(MetaSentence s, HashSet<TruthAssignmentVar> vars) {
        super(new ArrayList<>(Collections.singletonList(s)), "NOT", "NOT", vars);
    }

    public MetaInference reasonForwards(Proof p, int inferenceNum) {
        return null;
    }

    public MetaInference reasonBackwards(Proof p, int inferenceNum) { return null; }

    public String toString() {
        StringBuilder builder = new StringBuilder();

        vars.forEach(v -> builder.append("∀").append(v.toSymbol()));

        if (!vars.isEmpty())
            builder.append(" ");

        builder.append("[").append(name).append(" ").append(args.get(0).toString()).append("]");

        return builder.toString();
    }


    public String toSymbol() {
        StringBuilder builder = new StringBuilder();

        vars.forEach(v -> builder.append("∀").append(v.toSymbol()));

        if (!vars.isEmpty())
            builder.append(" ");

        builder.append(symbol).append("[").append(args.get(0).toSymbol()).append("]");

        return builder.toString();
    }

}
