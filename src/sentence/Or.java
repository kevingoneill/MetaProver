package sentence;

import inference.Inference;
import truthfunction.TruthFunction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kevin on 3/2/16.
 */
public class Or extends Sentence {

    public Or(List<Sentence> a) {
        super(new ArrayList<>(a), "or");
    }

    public Boolean eval(TruthFunction h) {
        //return pre-mapped value for this
        if (h.isMapped(this))
            return h.models(this);

        //if any atoms are unmapped, return null
        if (args.contains(null))
            return null;

        //Create a new mapping in h for this with newly computed value
        boolean val = args.stream().anyMatch(arg -> arg.eval(h));
        h.set(this, val);
        return val;
    }

    @Override
    public Inference reasonForwards(TruthFunction h) {
        return null;
    }

    @Override
    public Inference reasonBackwards(TruthFunction h) {
        return null;
    }

    public int hashCode() { return toString().hashCode(); }
}
