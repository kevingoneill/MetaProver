package sentence;

import inference.Decomposition;
import inference.Inference;
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

    public Inference reasonForwards(TruthFunction h) {
        if (h.isMapped(this) && (!h.isMapped(args.get(0)) || h.models(args.get(0)).equals(h.models(this)))) {
            h.setDecomposed(this);
            if (h.models(this))
                return new Decomposition() {{
                    setFalse(args.get(0));
                }};
            else
                return new Decomposition() {{
                    setTrue(args.get(0));
                }};
        }
        return null;
    }

    public Inference reasonBackwards(TruthFunction h) {
        return reasonForwards(h);
    }
}
