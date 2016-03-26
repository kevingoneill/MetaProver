package expression.metasentence;

import expression.sentence.Sentence;
import logicalreasoner.inference.Decomposition;
import logicalreasoner.inference.Inference;
import metareasoner.metainference.MetaInference;
import metareasoner.proof.Proof;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The MODELS class represents the meta-logical property of a
 * TruthAssignment setting some Sentence to some Boolean (null
 * if unknown/contingency)
 */
public class MODELS extends MetaSentence {

    Boolean isModelled;

    public MODELS(TruthAssignmentVar t, Sentence s, Boolean b, int inferenceNum, boolean printQuantifier) {
        super(new ArrayList<>(Arrays.asList(t, s)),
                "models", b == null ? "⊨?" : (b ? "⊨" : "⊭"),
                (printQuantifier ? new HashSet<>(Collections.singletonList(t)) : new HashSet<>()));
        isModelled = b;
        if (b != null) {
            if (b)
                t.setTrue(s, inferenceNum);
            else
                t.setFalse(s, inferenceNum);
        }
    }

    public String toSymbol() {
        return vars.stream().map(v -> "∀" + v.toSymbol()).collect(Collectors.joining()) + (vars.isEmpty() ? "" : " ")
                + args.get(0).toSymbol() + " " + symbol + " " + args.get(1).toSymbol();
    }

    public Sentence getSentence() {
        return (Sentence)args.get(1);
    }

    public MetaInference reasonForwards(Proof p, int inferenceNum) { return reason(p, inferenceNum); }

    public MetaInference reasonBackwards(Proof p, int inferenceNum) {
        return reason(p, inferenceNum);
    }

    public MetaInference reason(Proof p, int inferenceNum) {
        TruthAssignmentVar t = (TruthAssignmentVar)args.get(0);
        if (!t.getInferences().isEmpty()) {
            Inference i = t.getInferences().pop();
            if (i.getOrigin().equals(getSentence()) && i instanceof Decomposition) {
                Decomposition d = (Decomposition)i;
                TruthAssignmentVar var = new TruthAssignmentVar(d.getAdditions());
                System.out.println("INFERRING " + d.getAdditions());
                ArrayList<MetaSentence> a = new ArrayList<>();
                d.getAdditions().keySet().forEach(s ->
                    a.add(new MODELS(var, s, var.models(s), inferenceNum, true)));

                return new MetaInference(this, a, inferenceNum);
            }
        }

        return null;
    }
}
