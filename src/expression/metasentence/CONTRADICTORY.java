package expression.metasentence;

import expression.sentence.Sentence;
import metareasoner.metainference.MetaInference;
import metareasoner.proof.Proof;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * The CONTRADICTORY MetaSentence expresses the meta-logical
 * notion of two logical statements being contradictory to each
 * other.
 */
public class CONTRADICTORY extends MetaSentence {
    public CONTRADICTORY(Sentence s1, Sentence s2) {
        super(new ArrayList<>(Arrays.asList(s1, s2)), "CONTRADICTORY", "is contradictory to");
    }

    public MetaInference reasonForwards(Proof p, int inferenceNum) {
        return null;
    }

    public MetaInference reasonBackwards(Proof p, int inferenceNum) {
        return null;
    }
}
