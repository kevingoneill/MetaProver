package sentence;

import logicalreasoner.inference.Branch;
import logicalreasoner.inference.Inference;
import logicalreasoner.truthfunction.TruthAssignment;

import java.util.ArrayList;

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
    public Inference reason(TruthAssignment h, int inferenceNum) {
        if (h.isMapped(this)) {
            if (h.models(this)) {
                Branch b = new Branch(h, this, inferenceNum);
                TruthAssignment t = new TruthAssignment();
                t.setTrue(args.get(0), inferenceNum);
                t.setTrue(args.get(1), inferenceNum);
                b.addBranch(t);
                TruthAssignment t1 = new TruthAssignment();
                t1.setFalse(args.get(0), inferenceNum);
                t1.setFalse(args.get(1), inferenceNum);
                b.addBranch(t1);
                return b;
            } else {
                Branch b = new Branch(h, this, inferenceNum);
                TruthAssignment t = new TruthAssignment();
                t.setTrue(args.get(0), inferenceNum);
                t.setFalse(args.get(1), inferenceNum);
                b.addBranch(t);
                TruthAssignment t1 = new TruthAssignment();
                t1.setFalse(args.get(0), inferenceNum);
                t1.setTrue(args.get(1), inferenceNum);
                b.addBranch(t1);
                return b;
            }
        }

        return null;
    }

}
