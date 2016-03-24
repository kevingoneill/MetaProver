package expression.metasentence;

import expression.Expression;
import expression.sentence.Sentence;
import logicalreasoner.truthassignment.TruthAssignment;
import metareasoner.metainference.MetaInference;
import metareasoner.proof.Proof;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by kevin on 3/22/16.
 */
public class EQUIVALENT extends MetaSentence {
    public EQUIVALENT(Expression e1, Expression e2) {
        super(new ArrayList<>(Arrays.asList(e1, e2)),  "EQUIVALENT","⟺");
    }

    public MetaInference reasonForwards(Proof p, int inferenceNum) {
        return reason(p, inferenceNum);
    }

    public MetaInference reasonBackwards(Proof p, int inferenceNum) {
        return reason(p, inferenceNum);
    }

    public MetaInference reason(Proof p, int inferenceNum) {
        TruthAssignmentVar t1 = new TruthAssignmentVar(new TruthAssignment()),
                t2 = new TruthAssignmentVar(new TruthAssignment());
        MODELS m1 = new MODELS(t1, (Sentence)args.get(0), true, inferenceNum),
                m2 = new MODELS(t1, (Sentence)args.get(1), false, inferenceNum),
                m3 = new MODELS(t2, (Sentence)args.get(0), false, inferenceNum),
                m4 = new MODELS(t2, (Sentence)args.get(1), true, inferenceNum);

        MetaSentence s = new AND(new NOT(new AND(m1, m2)), new NOT(new AND(m3, m4)));
        ArrayList<MetaSentence> a = new ArrayList<>();
        a.add(s);
        return new MetaInference(this, a, inferenceNum);
    }
}
