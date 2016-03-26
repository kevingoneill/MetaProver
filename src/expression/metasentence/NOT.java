package expression.metasentence;

import metareasoner.metainference.MetaInference;
import metareasoner.proof.Proof;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by kevin on 3/24/16.
 */
public class NOT extends MetaSentence {
    public NOT(MetaSentence s) {
        super(new ArrayList<>(Collections.singletonList(s)), "NOT", "NOT", s.getVars());
    }

    public MetaInference reasonForwards(Proof p, int inferenceNum) {
        return null;
    }

    public MetaInference reasonBackwards(Proof p, int inferenceNum) { return null; }

    public String toString() {
        StringBuilder builder = new StringBuilder();

        vars.forEach((v, b) -> {
            if (b)
                builder.append("∀").append(v.toSymbol());
        });
        if (!vars.isEmpty())
            builder.append(" ");

        builder.append("[").append(name).append(" ").append(((MetaSentence)args.get(0)).toString(false)).append("]");

        return builder.toString();
    }


    public String toSymbol() {
        StringBuilder builder = new StringBuilder();

        vars.forEach((v, b) -> {
            if (b)
                builder.append("∀").append(v.toSymbol());
        });
        if (!vars.isEmpty())
            builder.append(" ");

        builder.append("[").append(symbol).append(" ").append(((MetaSentence)args.get(0)).toSymbol(false, new HashSet<>())).append("]");

        return builder.toString();
    }

    @Override
    public String toSymbol(boolean isTopLevel, Set<TruthAssignmentVar> unprintedVars) {
        if (isTopLevel)
            return toSymbol();
        StringBuilder builder = new StringBuilder();
        unprintedVars.forEach(v -> builder.append("∀").append(v.toSymbol()));

        return builder.toString() + "[" + symbol + " " + ((MetaSentence)args.get(0)).toSymbol(false, new HashSet<>()) + "]";
    }
}
