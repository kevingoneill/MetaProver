package expression.metasentence;

import expression.sentence.Sentence;
import logicalreasoner.inference.Inference;
import logicalreasoner.prover.SemanticProver;
import logicalreasoner.truthassignment.TruthAssignment;
import metareasoner.metainference.MetaInference;
import metareasoner.proof.Proof;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

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
        reduce();
    }

    public Inference reason(TruthAssignment h, int inferenceNum) {
        return null;
    }

    public Boolean isConsistent(TruthAssignment h) {
        return truthAssignment.isConsistent();
    }

    public void setTrue(Sentence s, int inferenceNum) {
        truthAssignment.setTrue(s, inferenceNum);
        reduce();
    }

    public void setFalse(Sentence s, int inferenceNum) {
        truthAssignment.setFalse(s, inferenceNum);
        reduce();
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

    @Override
    public String toString() {
        return name;
    }

    @Override
    public String toString(boolean isTopLevel) {
        return toString();
    }

    @Override
    public String toSymbol() {
        return name;
    }

    @Override
    public String toSymbol(boolean isTopLevel, Set<TruthAssignmentVar> unprintedVars) {
        return toSymbol();
    }

    public int hashCode() {
        return name.hashCode();
    }

    public boolean equals(Object o) {
        if (o instanceof TruthAssignmentVar) {
            TruthAssignment t = ((TruthAssignmentVar) o).truthAssignment;

            return truthAssignment.keySet().stream().allMatch(s -> truthAssignment.models(s) == t.models(s));
        }
        return false;
    }

    private void reduce() {
        SemanticProver.decompose(truthAssignment);
    }
}
