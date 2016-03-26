package expression.metasentence;

import expression.Expression;
import expression.sentence.Sentence;
import logicalreasoner.truthassignment.TruthAssignment;
import metareasoner.metainference.MetaInference;
import metareasoner.proof.Proof;

import java.util.*;

/**
 * SUBSUMES is the meta-logical equivalent of implies
 */
public class SUBSUMES extends MetaSentence {

    public SUBSUMES(Expression e1, Expression e2) {
        super(new ArrayList<>(Arrays.asList(e1, e2)), "SUBSUMES", "⟹", new HashSet<>());
    }

    public MetaInference reasonForwards(Proof p, int inferenceNum) {
        return reason(p, inferenceNum);
    }

    public MetaInference reasonBackwards(Proof p, int inferenceNum) {
        return reason(p, inferenceNum);
    }

    public MetaInference reason(Proof p, int inferenceNum) {
        TruthAssignmentVar t = new TruthAssignmentVar(new TruthAssignment());
        MODELS m1 = new MODELS(t, (Sentence)args.get(0), true, inferenceNum, false),
                m2 = new MODELS(t, (Sentence)args.get(1), false, inferenceNum, false);
        MetaSentence s = new NOT(new AND(m1, m2, new HashSet<>()), new HashSet<>(Collections.singletonList(t)));
        ArrayList<MetaSentence> a = new ArrayList<>();
        a.add(s);
        return new MetaInference(this, a, inferenceNum);
    }
}
