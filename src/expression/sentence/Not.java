package expression.sentence;

import logicalreasoner.inference.Decomposition;
import logicalreasoner.inference.Inference;
import logicalreasoner.truthassignment.TruthAssignment;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * The Not class represents a logical negation
 *
 * (not A)
 */
public class Not extends Sentence {

    public Not(Sentence e) {
        super(new ArrayList<>(Arrays.asList(e)), "not", "¬");
    }

    public String toSymbol() {
        if (args.get(0) instanceof Atom || args.get(0) instanceof Predicate)
            return symbol + args.get(0).toSymbol();
        return symbol + "(" + args.get(0).toSymbol() + ")";
    }

    public Boolean eval(TruthAssignment h) {
        //if (h.isMapped(this))
        //    return h.models(this);

        Boolean val = args.get(0).eval(h);
        if (val == null)
            return null;
        return !val;
    }

    @Override
    public Inference reason(TruthAssignment h, int inferenceNum) {
        if (h.isMapped(this)) {
            if (h.models(this)) {
                Decomposition d = new Decomposition(h, this, inferenceNum);
                d.setFalse(args.get(0));
                return d;
            } else {
                Decomposition d = new Decomposition(h, this, inferenceNum);
                d.setTrue(args.get(0));
                return d;
            }
        }
        return null;
    }

}
