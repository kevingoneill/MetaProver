package logicalreasoner.inference;

import logicalreasoner.sentence.Sentence;
import logicalreasoner.truthfunction.TruthAssignment;

/**
 * The inference stores changes to be made to a parent TruthAssignment.
 */
public abstract class Inference {
    TruthAssignment parent;
    Sentence origin;

    public Inference(TruthAssignment p, Sentence o) {
        parent = p;
        origin = o;
    }

    public TruthAssignment getParent() { return parent; }

    public Sentence getOrigin() { return origin; }

    public abstract void infer(TruthAssignment h);
}
