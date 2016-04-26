package expression.metasentence;

import metareasoner.metainference.MetaInference;
import metareasoner.proof.Proof;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

/**
 * The OR class represents the meta-logical disjunction
 * of MetaSentences
 */
public class OR extends MetaSentence {
    public OR(ArrayList<MetaSentence> a, HashSet<TruthAssignmentVar> v) {
        super(new ArrayList<>(a), "OR", "OR", v);
    }

    public OR(MetaSentence s1, MetaSentence s2, HashSet<TruthAssignmentVar> v) {
        super(new ArrayList<>(Arrays.asList(s1, s2)), "OR", "OR", v);
    }

    public MetaInference reasonForwards(Proof p, int inferenceNum) { return reason(p, inferenceNum, true); }

    public MetaInference reasonBackwards(Proof p, int inferenceNum) { return reason(p, inferenceNum, false); }

    public MetaInference reason(Proof p, int inferenceNum, boolean isForwards) {
        ArrayList<MetaInference> inferences = new ArrayList<>();
        ArrayList<MetaSentence> removedArgs = new ArrayList<>();
        ArrayList<MetaSentence> newArgs = new ArrayList<>();

        args.forEach(a -> {
            if (!a.equals(MetaConstant.CONTRADICTION)) {
                MetaInference i = null;
                if (isForwards)
                    i = ((MetaSentence) a).reasonForwards(p, inferenceNum);
                else
                    i = ((MetaSentence) a).reasonBackwards(p, inferenceNum);
                if (i != null)
                    inferences.add(i);
                else
                    newArgs.add((MetaSentence) a);
            } else
                removedArgs.add((MetaSentence) a);
        });

        for (int i = 0; i < inferences.size(); ++i) {
            if (inferences.get(i) != null)
                newArgs.addAll(inferences.get(i).getInferences());
        }

        OR or = new OR(newArgs, vars);

        if (!or.equals(this)) {
            if (newArgs.size() >= args.size()) {
                ArrayList<MetaSentence> a = new ArrayList<>();
                a.add(or);
                return new MetaInference(this, a, inferenceNum, true, inferences.get(0).getSymbol());
            } else if (newArgs.size() > 1) {
                ArrayList<MetaSentence> a = new ArrayList<>();
                a.add(or);
                return new MetaInference(this, a, inferenceNum, false, symbol);
            }
            ArrayList<MetaSentence> a = new ArrayList<>();
            a.add(newArgs.get(0));
            return new MetaInference(this, a, inferenceNum, false, symbol);
        }

        return null;
    }

    public boolean equals(Object o) {
        if (o instanceof OR) {
            OR or = (OR)o;
            return args.stream().allMatch(or.args::contains) && or.args.stream().allMatch(args::contains);
        }
        return false;
    }
}