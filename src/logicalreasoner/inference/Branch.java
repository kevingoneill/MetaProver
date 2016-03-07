package logicalreasoner.inference;

import logicalreasoner.sentence.Sentence;
import logicalreasoner.truthfunction.TruthAssignment;

import java.util.ArrayList;
import java.util.List;

/**
 * A Branch is an inference which generates two or more
 * possible TruthAssignments given a parent TruthAssignment
 * and an origin Sentence.
 */
public class Branch extends Inference {

    List<TruthAssignment> branches;


    public Branch(TruthAssignment p, Sentence s) {
        super(p, s);
        branches = new ArrayList<>();
    }

    //@Override
    public List<TruthAssignment> infer(TruthAssignment h) {
        //branches.forEach(b -> b.setParent(h));
        h.setDecomposed(origin);
        return h.addChildren(branches);
    }

    public int size() {
        return branches.size();
    }

    public void addBranch(TruthAssignment h) {
        branches.add(h);
    }

    public List<TruthAssignment> getBranches() { return branches; }


}
