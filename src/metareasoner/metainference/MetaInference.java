package metareasoner.metainference;

import expression.metasentence.MetaSentence;
import metareasoner.proof.Proof;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Created by kevin on 3/23/16.
 */
public class MetaInference {

    private MetaSentence origin;
    private ArrayList<MetaSentence> inferences;
    private int uid;

    public MetaInference(MetaSentence s, ArrayList<MetaSentence> inferences, int i) {
        origin = s;
        this.inferences = inferences;
        uid = i;
    }

    public int getInferenceNum() { return  uid; }

    public MetaSentence getOrigin() { return origin; }

    public void infer(Proof p, boolean isForwardsInference) {
        if (isForwardsInference)
            inferences.forEach(i -> p.addForwardsInference(i, this));
        else
            inferences.forEach(i -> p.addBackwardsInference(i, this));
    }

    public int hashCode() {
        return origin.hashCode();
    }

    public boolean equals(Object o) {
        if (o instanceof MetaInference) {
            MetaInference i = (MetaInference)o;
            return i.origin.equals(origin);
        }
        return false;
    }

    public String toString() {
        return "Inference " + uid + " over origin: " + origin.toSymbol() + " inferences: { "
                + inferences.stream().map(s -> s.toSymbol() + " ").collect(Collectors.joining()) + "}";
    }
}
