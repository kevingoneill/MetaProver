package sentence;

import truthfunction.TruthFunction;

import java.util.ArrayList;

/**
 * Created by kevin on 3/2/16.
 */
public class Atom extends Sentence {

    /**
     * Create a new Atom object with the given name
     * @param n the name of the Atom
     */
    public Atom(String n) {
        super(new ArrayList<>(), n);
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

    public String toString() { return name; }
    public int hashCode() { return name.hashCode(); }
}
