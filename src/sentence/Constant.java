package sentence;

import inference.Inference;
import truthfunction.TruthFunction;

/**
 * Created by kevin on 3/3/16.
 */
public class Constant extends Atom {
    public static final Constant TRUE = new Constant(true),
            FALSE = new Constant(false);

    private boolean value;

    private Constant(boolean b) {
        super(Boolean.toString(b));
        value = b;
    }

    public Boolean eval(TruthFunction h) {
        return value;
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
