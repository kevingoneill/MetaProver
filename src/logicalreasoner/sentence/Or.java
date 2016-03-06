package logicalreasoner.sentence;

import logicalreasoner.inference.Branch;
import logicalreasoner.inference.Decomposition;
import logicalreasoner.inference.Inference;
import logicalreasoner.truthfunction.TruthAssignment;

import java.util.ArrayList;
import java.util.HashMap;
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
    public Inference reason(TruthAssignment h) {
        if (h.isMapped(this)) {
            h.setDecomposed(this);
            if (h.models(this)) {
                return new Branch(h, this) {{
                    args.forEach(arg ->
                            addBranch(new TruthAssignment(new HashMap<Sentence, Boolean>() {{
                                put(arg, true);
                            }})));
                }};
            } else {
                return new Decomposition(h, this) {{
                    args.forEach(this::setFalse);
                }};
            }
        }

        return null;
    }

    public int hashCode() { return toString().hashCode(); }
}
