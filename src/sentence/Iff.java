package sentence;

import inference.Inference;
import truthfunction.TruthFunction;

import java.util.ArrayList;

/**
 * Created by kevin on 3/3/16.
 */
public class Iff extends Sentence {
    public Iff(Sentence expr1, Sentence expr2) {
        super(new ArrayList<Sentence>(){{add(expr1); add(expr2);}}, "iff");
    }

    public Boolean eval(TruthFunction h) {
        //return pre-mapped value for this
        if (h.isMapped(this))
            return h.models(this);

        Boolean antecedent = h.models(args.get(0)),
                consequent = h.models(args.get(1));

        //Return null if any atoms are unmapped
        if (antecedent == null || consequent == null)
            return null;

        //Add mapping for new evaluation of this
        boolean val = (antecedent && consequent) || (!antecedent && !consequent);
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
}
