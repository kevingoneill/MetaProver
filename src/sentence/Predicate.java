package sentence;

import inference.Inference;
import truthfunction.TruthFunction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kevin on 3/4/16.
 */
public class Predicate extends Sentence{
    /**
     * Create a new Atom object with the given name
     * @param n the name of the Atom
     */
    public Predicate(String n, List<Sentence> vars) {
        super(new ArrayList<>(vars), n);
    }

    /**
     * Return the value of this in the given TruthFunction
     * @param h the TruthFunction used to evaluate this
     * @return null if h does not contain this,
     * true if h models this, or false if h models (not this)
     */
    public Boolean eval(TruthFunction h) {
        if (h.isMapped(this))
            return h.models(this);

        return null;
    }

    @Override
    public Inference reasonForwards(TruthFunction h) {
        return null;
    }

    @Override
    public Inference reasonBackwards(TruthFunction h) {
        return null;
    }
}
