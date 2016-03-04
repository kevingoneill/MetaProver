package sentence;

import truthfunction.TruthFunction;

import java.util.ArrayList;

/**
 * Created by kevin on 3/2/16.
 */
public class Implies extends Sentence {
    public Implies(Sentence ifExpr, Sentence thenExpr) {
        super(new ArrayList<Sentence>(){{add(ifExpr); add(thenExpr);}}, "implies");
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
        boolean val = !(antecedent && !consequent);
        h.set(this, val);
        return val;
    }
}
