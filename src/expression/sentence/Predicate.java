package expression.sentence;

import logicalreasoner.inference.Inference;
import logicalreasoner.truthassignment.TruthAssignment;

import java.util.ArrayList;

/**
 * The Predicate class represents a logical predicate over
 * some constants (assume no objects are variables)
 *
 * ie, (P x), (W x y z), and (H a) are all predicates
 */
public class Predicate extends Sentence {
    /**
     * Create a new Atom object with the given name
     * @param n the name of the Atom
     */
    public Predicate(String n, ArrayList<Sentence> vars) {
        super(vars, n, n);
    }

    public String toSymbol() {
        StringBuilder builder = new StringBuilder();
        builder.append(name).append("(");
        args.forEach(arg -> builder.append(" ").append(arg));
        builder.append(")");
        return builder.toString();
    }

    /**
     * Return the value of this in the given TruthAssignment
     * @param h the TruthAssignment used to evaluate this
     * @return null if h does not contain this,
     * true if h models this, or false if h models (not this)
     */
    public Boolean eval(TruthAssignment h) {
        if (h.isMapped(this))
            return h.models(this);

        return null;
    }

    @Override
    public Inference reason(TruthAssignment h, int inferenceNum) {
        if (!h.isDecomposed(this))
            h.setDecomposed(this);
        return null;
    }
}
