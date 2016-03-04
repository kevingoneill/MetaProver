package sentence;

import truthfunction.TruthFunction;

import java.util.ArrayList;

/**
 * Created by kevin on 3/2/16.
 */
public class Not extends Sentence {

    public Not(Sentence e) {
        super(new ArrayList<Sentence>(){{add(e);}}, "not");
    }


    public Boolean eval(TruthFunction h) {
        if (h.isMapped(this))
            return h.models(this);

        Boolean val = args.get(0).eval(h);
        if (val == null)
            return null;
        return !val;
    }
}
