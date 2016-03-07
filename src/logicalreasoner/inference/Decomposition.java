package logicalreasoner.inference;

import logicalreasoner.sentence.Sentence;
import logicalreasoner.truthfunction.TruthAssignment;

import java.util.List;

/**
 * A Decomposition is the direct inference of one or more
 * Sentences from another Sentence.
 */
public class Decomposition extends Inference {
    TruthAssignment additions;

    public Decomposition(TruthAssignment h, Sentence o) {
        super(h, o);
        additions = new TruthAssignment();
    }

    @Override
    public List<TruthAssignment> infer(TruthAssignment h) {
        h.merge(additions);
        return null;
    }

    public void setTrue(Sentence s) { additions.setTrue(s); }

    public void setFalse(Sentence s) { additions.setFalse(s); }

    public TruthAssignment getAdditions() {
        return additions;
    }
}
