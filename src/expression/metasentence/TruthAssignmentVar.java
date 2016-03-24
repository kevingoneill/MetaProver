package expression.metasentence;

import expression.sentence.Sentence;
import logicalreasoner.inference.Inference;
import logicalreasoner.truthassignment.TruthAssignment;
import metareasoner.metainference.MetaInference;
import metareasoner.proof.Proof;

import java.util.ArrayList;
import java.util.HashSet;

/**
 *  TruthAssignmentVar is a wrapper class for TruthAssignment which
 *  allows the meta-reasoner to make judgements about particular
 *  (but arbitrary as a whole) TruthAssignments.
 */
public class TruthAssignmentVar extends MetaSentence {
    TruthAssignment truthAssignment;

    public TruthAssignmentVar(TruthAssignment t) {
        super(new ArrayList<>(), t.getName(), t.getName(), new HashSet<>());
        truthAssignment = t;
    }

    public Inference reason(TruthAssignment h, int inferenceNum) {
        return null;
    }

    public Boolean isConsistent(TruthAssignment h) {
        return truthAssignment.isConsistent();
    }

    public void setTrue(Sentence s, int inferenceNum) {
        truthAssignment.setTrue(s, inferenceNum);
    }

    public void setFalse(Sentence s, int inferenceNum) {
        truthAssignment.setFalse(s, inferenceNum);
    }

    public boolean models(Sentence s) {
        return truthAssignment.models(s);
    }

    public MetaInference reasonForwards(Proof p, int inferenceNum) {
        return null;
    }

    public MetaInference reasonBackwards(Proof p, int inferenceNum) {
        return null;
    }

    public TruthAssignment getTruthAssignment() {
        return truthAssignment;
    }

    public String toString() {
        return name;
    }

    public String toString(boolean isTopLevel) {
        return toString();
    }

    public String toSymbol() {
        return name;
    }

    public String toSymbol(boolean isTopLevel) {
        return toSymbol();
    }

    public int hashCode() {
        return truthAssignment.hashCode();
    }

    public boolean equals(Object o) {
        if (o instanceof TruthAssignmentVar) {
            return ((TruthAssignmentVar)o).truthAssignment.equals(truthAssignment);
        }
        return false;
    }
}
