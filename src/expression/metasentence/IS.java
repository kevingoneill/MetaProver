package expression.metasentence;

import expression.sentence.Sentence;
import metareasoner.metainference.MetaInference;
import metareasoner.proof.Proof;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by kevin on 3/23/16.
 */
public class IS extends MetaSentence {

    public IS(Sentence s, MetaConstant c) {
        super(new ArrayList<>(Arrays.asList(s, c)), "IS", "is a");
    }

    public MetaInference reasonForwards(Proof p, int inferenceNum) {
        return null;
    }

    public MetaInference reasonBackwards(Proof p, int inferenceNum) {
        return null;
    }
}
