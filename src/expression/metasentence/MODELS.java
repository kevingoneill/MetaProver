package expression.metasentence;

import expression.sentence.Sentence;
import logicalreasoner.truthassignment.TruthAssignment;
import metareasoner.metainference.MetaInference;
import metareasoner.proof.Proof;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * The MODELS class represents the meta-logical property of a
 * TruthAssignment setting some Sentence to some Boolean (null
 * if unknown/contingency)
 */
public class MODELS extends MetaSentence {

    Boolean isModelled;

    public MODELS(TruthAssignmentVar t, Sentence s, Boolean b, int inferenceNum) {
        super(new ArrayList<>(Arrays.asList(t, s)), "models", b == null ? "⊨?" : (b ? "⊨" : "⊭"));
        isModelled = b;
        if (b != null) {
            if (b)
                t.setTrue(s, inferenceNum);
            else
                t.setFalse(s, inferenceNum);
        }
        System.out.println(t.getTruthAssignment());
    }

    public String toSymbol() {
        return "∀" + args.get(0).toSymbol() + "[" + args.get(0).toSymbol() + " " + symbol + " " + args.get(1).toSymbol() + "]";
    }

    public MetaInference reasonForwards(Proof p, int inferenceNum) {
        return null;
    }

    public MetaInference reasonBackwards(Proof p, int inferenceNum) {
        return null;
    }
}
