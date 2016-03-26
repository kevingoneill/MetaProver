package expression.metasentence;

import metareasoner.metainference.MetaInference;
import metareasoner.proof.Proof;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * The OR class represents the meta-logical disjunction
 * of MetaSentences
 */
public class OR extends MetaSentence {
    public OR(ArrayList<MetaSentence> a, HashSet<TruthAssignmentVar> v) {
        super(new ArrayList<>(a), "OR", "OR", v);
    }

    public MetaInference reasonForwards(Proof p, int inferenceNum) {
        return null;
    }

    public MetaInference reasonBackwards(Proof p, int inferenceNum) {
        return null;
    }

}
