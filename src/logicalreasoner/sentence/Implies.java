package logicalreasoner.sentence;

import logicalreasoner.inference.Branch;
import logicalreasoner.inference.Decomposition;
import logicalreasoner.inference.Inference;
import logicalreasoner.truthfunction.TruthAssignment;

import java.util.ArrayList;

/**
 * The Implies class represents logical implication,
 * (if X then Y)
 *
 * For example, (implies A B), (implies X Y)
 */
public class Implies extends Sentence {
    public Implies(Sentence ifExpr, Sentence thenExpr) {
        super(new ArrayList<Sentence>(){{add(ifExpr); add(thenExpr);}}, "implies");
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
        return !(antecedent && !consequent);
        //h.set(this, val);
        //return val;
    }

    @Override
    public Inference reason(TruthAssignment h) {
        if (h.isMapped(this)) {
            h.setDecomposed(this);
            if (h.models(this)) {
                return new Branch(h, this) {{
                    addBranch(new TruthAssignment() {{
                        setFalse(args.get(0));
                    }});
                    addBranch(new TruthAssignment() {{
                        setTrue(args.get(1));
                    }});
                }};
            } else {
                return new Decomposition(h, this) {{
                    setTrue(args.get(0));
                    setFalse(args.get(1));
                }};
            }
        }

        return null;
    }

}
