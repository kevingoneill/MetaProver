package expression.metasentence;

import logicalreasoner.truthassignment.TruthAssignment;
import metareasoner.metainference.MetaInference;
import metareasoner.proof.Proof;

import java.util.*;

/**
 * The IFF class represents the metalogical IFF statement
 */
public class IFF extends MetaSentence {
    public IFF(MetaSentence s1, MetaSentence s2, HashSet<TruthAssignmentVar> v) {
        super(new ArrayList<>(Arrays.asList(s1, s2)), "IFF", "IFF", v);
    }

    public MetaInference reasonForwards(Proof p, int inferenceNum) {
        return reason(p, inferenceNum);
    }

    public MetaInference reasonBackwards(Proof p, int inferenceNum) {
        return null;
    }

    public MetaInference reason(Proof p, int inferenceNum) {
        if (args.get(0) instanceof MODELS && args.get(1) instanceof MODELS) {
            MODELS arg1 = (MODELS)args.get(0),
                    arg2 = (MODELS)args.get(1);

            TruthAssignmentVar t1 = new TruthAssignmentVar(new TruthAssignment()),
                    t2 = new TruthAssignmentVar(new TruthAssignment());

            MODELS m1 = new MODELS(t1, arg1.getSentence(), true, inferenceNum, false),
                    m2 = new MODELS(t1, arg2.getSentence(), false, inferenceNum, false),
                    m3 = new MODELS(t2, arg1.getSentence(), false, inferenceNum, false),
                    m4 = new MODELS(t2, arg2.getSentence(), true, inferenceNum, false);

            MetaSentence s = new AND(new NOT(new AND(m1, m2, new HashSet<>()), new HashSet<>(Collections.singletonList(t1))),
                    new NOT(new AND(m4, m3, new HashSet<>()), new HashSet<>(Collections.singletonList(t2))),
                    new HashSet<>());

            ArrayList<MetaSentence> a = new ArrayList<>();
            a.add(s);
            return new MetaInference(this, a, inferenceNum);
        }

        return null;
    }
}
