package expression.metasentence;

import expression.sentence.Sentence;
import logicalreasoner.truthassignment.TruthAssignment;
import metareasoner.metainference.MetaInference;
import metareasoner.proof.Proof;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

/**
 * Created by kevin on 3/22/16.
 */
public class EQUIVALENT extends MetaSentence {
    public EQUIVALENT(Sentence e1, Sentence e2) {
        super(new ArrayList<>(Arrays.asList(e1, e2)),  "EQUIVALENT","⟺", new HashSet<>());
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

        MetaSentence s = new AND(new NOT(new AND(m1, m2, new HashSet<>(Collections.singletonList(t1)))),
                new NOT(new AND(m3, m4, new HashSet<>(Collections.singletonList(t2)))),
                new HashSet<>(Arrays.asList(t1, t2)));

        ArrayList<MetaSentence> a = new ArrayList<>();
        a.add(s);
        return new MetaInference(this, a, inferenceNum);
    }
}
