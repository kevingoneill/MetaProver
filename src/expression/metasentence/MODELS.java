package expression.metasentence;

import expression.sentence.Sentence;
import metareasoner.metainference.MetaInference;
import metareasoner.proof.Proof;

import java.util.*;

/**
 * The MODELS class represents the meta-logical property of a
 * TruthAssignment setting some Sentence to some Boolean (null
 * if unknown/contingency)
 */
public class MODELS extends MetaSentence {

    Boolean isModelled;

    public MODELS(TruthAssignmentVar t, Sentence s, Boolean b, int inferenceNum) {
        super(new ArrayList<>(Arrays.asList(t, s)),
                "models", b == null ? "⊨?" : (b ? "⊨" : "⊭"),
                new HashSet<>(Collections.singletonList(t)));
        isModelled = b;
        if (b != null) {
            if (b)
                t.setTrue(s, inferenceNum);
            else
                t.setFalse(s, inferenceNum);
        }
    }

    public String toSymbol() {
        return args.get(0).toSymbol() + " " + symbol + " " + args.get(1).toSymbol();
    }

    @Override
    public String toSymbol(boolean isTopLevel, Set<TruthAssignmentVar> unprintedVars) {
        if (isTopLevel)
            return toSymbol();
        return args.get(0).toSymbol() + " " + symbol + " " + args.get(1).toSymbol();
    }

    public Sentence getSentence() {
        return (Sentence)args.get(1);
    }

    public MetaInference reasonForwards(Proof p, int inferenceNum) {
        return null;
    }

    public MetaInference reasonBackwards(Proof p, int inferenceNum) {
        return null;
    }
}
