package expression.metasentence;

import metareasoner.metainference.MetaInference;
import metareasoner.proof.Proof;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * The AND class represents the meta-logical AND,
 * a high order conjunction of MetaSentences
 */
public class AND extends MetaSentence {
    public AND(ArrayList<MetaSentence> a) {
        super(new ArrayList<>(a), "AND", "AND");
    }

    public AND(MetaSentence s1, MetaSentence s2) {
        super(new ArrayList<>(Arrays.asList(s1, s2)), "AND", "AND");
    }

    public MetaInference reasonForwards(Proof p, int inferenceNum) {
        return infer(p, inferenceNum);
    }

    public MetaInference reasonBackwards(Proof p, int inferenceNum) {
        return infer(p, inferenceNum);
    }

    private MetaInference infer(Proof p, int inferenceNum) {
        ArrayList<MetaSentence> inferences = new ArrayList<>();
        args.forEach(a -> inferences.add((MetaSentence)a));
        return new MetaInference(this, inferences, inferenceNum);
    }
}
