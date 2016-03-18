package sentence;

import logicalreasoner.inference.Branch;
import logicalreasoner.inference.Decomposition;
import logicalreasoner.inference.Inference;
import logicalreasoner.truthfunction.TruthAssignment;

import java.util.ArrayList;
import java.util.List;

/**
 * The Or class represents the generalized logical disjunction
 *
 * For example, (or A B), (or X Y Z)
 */
public class Or extends Sentence {

    public Or(List<Sentence> a) {
        super(new ArrayList<>(a), "or");
    }

    public Boolean eval(TruthAssignment h) {
        //return pre-mapped value for this
        //if (h.isMapped(this))
        //    return h.models(this);

        //if any atoms are unmapped, return null
        if (args.contains(null))
            return null;

        //Create a new mapping in h for this with newly computed value
        //boolean val =
        return args.stream().anyMatch(arg -> arg.eval(h));
        //h.set(this, val);
        //return val;
    }

    @Override
    public Inference reason(TruthAssignment h, int inferenceNum) {
        if (h.isMapped(this)) {
            if (h.models(this)) {
                Branch b = new Branch(h, this, inferenceNum);
                args.forEach(arg -> {
                    TruthAssignment t = new TruthAssignment();
                    t.setTrue(arg, inferenceNum);
                    b.addBranch(t);
                });

                return b;
            } else {
                Decomposition d = new Decomposition(h, this, inferenceNum);
                args.forEach(d::setFalse);
                return d;
            }
        }

        return null;
    }

    public int hashCode() { return toString().hashCode(); }
}
