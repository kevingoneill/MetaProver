package expression.sentence;

import logicalreasoner.inference.Branch;
import logicalreasoner.inference.Decomposition;
import logicalreasoner.inference.Inference;
import logicalreasoner.truthassignment.TruthAssignment;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * The Implies class represents logical implication,
 * (if X then Y)
 *
 * For example, (implies A B), (implies X Y)
 */
public class Implies extends Sentence {
    public Implies(Sentence ifExpr, Sentence thenExpr) {
        super(new ArrayList<>(Arrays.asList(ifExpr, thenExpr)), "implies", "⟶");
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
    public Inference reason(TruthAssignment h, int inferenceNum) {
        if (h.isMapped(this)) {
            if (h.models(this)) {
                Branch b = new Branch(h, this, inferenceNum);
                TruthAssignment t = new TruthAssignment();
                t.setFalse(args.get(0), inferenceNum);
                b.addBranch(t);
                TruthAssignment t1 = new TruthAssignment();
                t1.setTrue(args.get(1), inferenceNum);
                b.addBranch(t1);
                return b;

            } else {
                Decomposition d = new Decomposition(h, this, inferenceNum);
                d.setTrue(args.get(0));
                d.setFalse(args.get(1));
                return d;
            }
        }

        return null;
    }

}
