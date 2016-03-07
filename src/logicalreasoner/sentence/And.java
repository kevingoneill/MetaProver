package logicalreasoner.sentence;

import logicalreasoner.inference.Branch;
import logicalreasoner.inference.Decomposition;
import logicalreasoner.inference.Inference;
import logicalreasoner.truthfunction.TruthAssignment;

import java.util.*;

/**
 * The And class represents the generalized logical conjunction
 *
 * For example, (and A B), (and X Y Z)
 */
public class And extends Sentence {
    public And(List<Sentence> a) {
       super(new ArrayList<>(a), "and");
    }

    public Boolean eval(TruthAssignment h) {
        //return pre-mapped value for this
        if (h.isMapped(this))
            return h.models(this);

        //if any atoms are unmapped, return null
        if (args.contains(null))
            return null;

        //Create a new mapping in h for this with newly computed value
        //boolean val =
        return args.stream().allMatch(arg -> arg.eval(h));
        //h.set(this, val);
        //return val;
    }

    @Override
    public Inference reason(TruthAssignment h) {
        if (h.isMapped(this)) {
            h.setDecomposed(this);
            if (h.models(this)) {
                return new Decomposition(h, this) {{
                    args.forEach(this::setTrue);
                }};
            } else {
                return new Branch(h, this) {{
                    args.forEach(arg ->
                        addBranch(new TruthAssignment() {{
                            setFalse(arg);
                        }}));
                }};
            }
        }

        return null;
    }
}
