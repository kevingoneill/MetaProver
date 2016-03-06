package logicalreasoner.inference;

import logicalreasoner.sentence.Sentence;
import logicalreasoner.truthfunction.TruthAssignment;

import java.util.HashSet;
import java.util.Set;

/**
 * A Branch is an inference which generates two or more
 * possible TruthAssignments given a parent TruthAssignment
 * and an origin Sentence.
 */
public class Branch extends Inference {

    Set<TruthAssignment> branches;


    public Branch(TruthAssignment p, Sentence s) {
        super(p, s);
        branches = new HashSet<>();
    }

    @Override
    public void infer(TruthAssignment h) {
        h.addChildren(branches);
    }

    public int size() {
        return branches.size();
    }

    public void addBranch(TruthAssignment h) {
        h.setParent(parent);
        branches.add(h);
    }

    public Set<TruthAssignment> getBranches() { return branches; }


}
