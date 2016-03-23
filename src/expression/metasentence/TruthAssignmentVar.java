package expression.metasentence;

import logicalreasoner.inference.Inference;
import logicalreasoner.truthassignment.TruthAssignment;
import expression.sentence.Sentence;

import java.util.ArrayList;

/**
 *  TruthAssignmentVar is a wrapper class for TruthAssignment which
 *  allows the meta-reasoner to make judgements about particular
 *  (but arbitrary as a whole) TruthAssignments.
 */
public class TruthAssignmentVar extends MetaSentence {
    TruthAssignment truthAssignment;

    public TruthAssignmentVar(TruthAssignment t) {
        super(new ArrayList<>(), t.getName(), t.getName());
    }

    public Inference reason(TruthAssignment h, int inferenceNum) {
        return null;
    }

    public Boolean eval(TruthAssignment h) {
        return truthAssignment.isConsistent();
    }

    public boolean models(Sentence s) {
        return truthAssignment.models(s);
    }
}
