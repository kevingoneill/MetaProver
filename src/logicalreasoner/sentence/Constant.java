package logicalreasoner.sentence;

import logicalreasoner.inference.Inference;
import logicalreasoner.truthfunction.TruthAssignment;

/**
 * The Constant represents logical tautology and contradiction
 */
public class Constant extends Atom {
    public static final Constant TRUE = new Constant(true),
            FALSE = new Constant(false);

    private boolean value;

    private Constant(boolean b) {
        super(Boolean.toString(b));
        value = b;
    }

    public Boolean eval(TruthAssignment h) {
        return value;
    }

    @Override
    public Inference reason(TruthAssignment h) {
        h.setDecomposed(this);
        return null;
    }
}
