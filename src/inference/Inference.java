package inference;

import truthfunction.TruthFunction;

/**
 * Created by kevin on 3/4/16.
 */
public abstract class Inference {
    public Inference() {

    }

    public abstract void infer(TruthFunction h);
}
