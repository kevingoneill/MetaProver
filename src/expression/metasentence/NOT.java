package expression.metasentence;

import metareasoner.metainference.MetaInference;
import metareasoner.proof.Proof;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by kevin on 3/24/16.
 */
public class NOT extends MetaSentence {
    public NOT(MetaSentence s) {
        super(new ArrayList<>(Collections.singletonList(s)), "NOT", "NOT");
    }

    public MetaInference reasonForwards(Proof p, int inferenceNum) {
        return null;
    }

    public MetaInference reasonBackwards(Proof p, int inferenceNum) { return null; }

    public String toString() {
        return "[" + name + " " + args.get(0).toString() + "]";
    }

    public String toSymbol() {
        return "[" + symbol + " " + args.get(0).toSymbol() + "]";
    }
}
