package sentence;

import truthfunction.TruthFunction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kevin on 3/2/16.
 */
public class And extends Sentence {
    List<Sentence> args;

    public And(List<Sentence> a) {
       super(new ArrayList<>(a), "and");
    }

    public Boolean eval(TruthFunction h) {
        //return pre-mapped value for this
        if (h.isMapped(this))
            return h.models(this);

        //if any atoms are unmapped, return null
        if (args.contains(null))
            return null;

        //Create a new mapping in h for this with newly computed value
        boolean val = args.stream().allMatch(arg -> arg.eval(h));
        h.set(this, val);
        return val;
    }


}
