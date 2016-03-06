package logicalreasoner.sentence;

import logicalreasoner.inference.Branch;
import logicalreasoner.inference.Inference;
import logicalreasoner.truthfunction.TruthAssignment;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * The Iff class represents the logical iff
 * (if and only if) operator
 */
public class Iff extends Sentence {
    public Iff(Sentence expr1, Sentence expr2) {
        super(new ArrayList<Sentence>(){{add(expr1); add(expr2);}}, "iff");
    }

    public Boolean eval(TruthAssignment h) {
        //return pre-mapped value for this
        //if (h.isMapped(this))
        //    return h.models(this);

        Boolean antecedent = h.models(args.get(0)),
                consequent = h.models(args.get(1));

        //Return null if any atoms are unmapped
        if (antecedent == null || consequent == null)
            return null;

        //Add mapping for new evaluation of this
        //boolean val =
        return (antecedent && consequent) || (!antecedent && !consequent);
        //h.set(this, val);
        //return val;
    }

    @Override
    public Inference reason(TruthAssignment h) {
        if (h.isMapped(this)) {
            h.setDecomposed(this);
            if (h.models(this)) {
                return new Branch(h, this) {{
                    addBranch(new TruthAssignment(new HashMap<Sentence, Boolean>() {{
                        put(args.get(0), true); put(args.get(1), true);
                    }}));
                    addBranch(new TruthAssignment(new HashMap<Sentence, Boolean>() {{
                        put(args.get(0), false); put(args.get(1), false);
                    }}));
                }};
            } else {
                return new Branch(h, this) {{
                    addBranch(new TruthAssignment(new HashMap<Sentence, Boolean>() {{
                        put(args.get(0), true); put(args.get(1), false);
                    }}));
                    addBranch(new TruthAssignment(new HashMap<Sentence, Boolean>() {{
                        put(args.get(0), false); put(args.get(1), true);
                    }}));
                }};
            }
        }

        return null;
    }

}
