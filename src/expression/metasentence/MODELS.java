package expression.metasentence;

import expression.sentence.Sentence;
import logicalreasoner.inference.Branch;
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

    Boolean isModelled, isTopLevel;

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
        isTopLevel = printQuantifier;
    }

    public String toSymbol() {
        return vars.stream().map(v -> "FORALL " + v.toSymbol()).collect(Collectors.joining()) + (vars.isEmpty() ? "" : " ")
                + args.get(0).toSymbol() + " " + symbol + " " + args.get(1).toSymbol();
    }

    public Sentence getSentence() {
        return (Sentence)args.get(1);
    }

    public boolean isModelled() { return isModelled; }

    public MetaInference reasonForwards(Proof p, int inferenceNum) { return reason(p, inferenceNum); }

    public MetaInference reasonBackwards(Proof p, int inferenceNum) {
        return reason(p, inferenceNum);
    }

    public MetaInference reason(Proof p, int inferenceNum) {
        TruthAssignmentVar t = (TruthAssignmentVar)args.get(0);

        if (!t.getTruthAssignment().isConsistent()) {
            ArrayList<MetaSentence> inferences = new ArrayList<>();
            inferences.add(MetaConstant.CONTRADICTION);
            return new MetaInference(this, inferences, inferenceNum, true, getSentence().getSymbol());
        }

        if (!t.getInferences().isEmpty()) {
            Inference i = t.getInferences().pop();
            if (i.getOrigin().equals(getSentence())) {
                if (i instanceof Decomposition) {
                    Decomposition d = (Decomposition) i;
                    d.infer(t.getTruthAssignment());

                    //System.out.println("INFERRING " + d.getAdditions());

                    ArrayList<MetaSentence> a = new ArrayList<>();
                    d.getAdditions().keySet().forEach(s ->
                            a.add(new MODELS(t, s, t.models(s), inferenceNum, isTopLevel)));

                    return new MetaInference(this, a, inferenceNum, true, d.getOrigin().getSymbol());
                } else if (i instanceof Branch) {
                    Branch b = (Branch)i;

                    ArrayList<MetaSentence> a = new ArrayList<>();
                    b.getBranches().forEach(c -> {
                        if (c.keySet().size() == 1)
                            c.keySet().forEach(s -> a.add(new MODELS(t.addChild(c), s, c.models(s), inferenceNum, false)));
                    });
                    OR or = new OR(a, new HashSet<>(Collections.singleton(t)));
                    a.clear();
                    a.add(or);
                    return new MetaInference(this, a, inferenceNum, true, b.getOrigin().getSymbol());
                }
            }
        }

        return null;
    }
}
