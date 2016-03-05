package inference;

import sentence.Sentence;
import truthfunction.TruthFunction;

/**
 * Created by kevin on 3/4/16.
 */
public class Decomposition extends Inference {
    TruthFunction additions;

    public Decomposition() {
        additions = new TruthFunction();
    }

    @Override
    public void infer(TruthFunction h) {
        h.merge(additions);
    }

    public void setTrue(Sentence s) { additions.setTrue(s); }

    public void setFalse(Sentence s) { additions.setFalse(s); }

    public void set(Sentence s, boolean b) { additions.set(s, b); }

    public TruthFunction getAdditions() {
        return additions;
    }
}
