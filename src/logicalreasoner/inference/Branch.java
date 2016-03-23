package logicalreasoner.inference;

import logicalreasoner.truthassignment.TruthAssignment;
import expression.sentence.Sentence;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A Branch is an inference which generates two or more
 * possible TruthAssignments given a parent TruthAssignment
 * and an origin Sentence.
 */
public class Branch extends Inference {

    private List<TruthAssignment> branches;

    public Branch(TruthAssignment p, Sentence s, int i) {
        super(p, s, i);
        branches = new ArrayList<>();
    }

    @Override
    public void infer(TruthAssignment h) {
        h.addChildren(new ArrayList<>(branches));
    }

    public int size() {
        return branches.size();
    }

    public void addBranch(TruthAssignment h) {
        branches.add(h);
    }

    public boolean equals(Object o) {
        if (o instanceof Branch) {
            Branch i = (Branch) o;
            return super.equals(i) && branches.equals(i.branches);
        }
        return false;
    }

    public String toString() {
        return "Branch " + inferenceNum + "- from: " + origin + "=" + parent.models(origin) + " to branches: " +
                branches.stream().map(b -> "{" +
                        b.keySet().stream().map(s ->
                                s.toString() + "=" + b.models(s)).collect(Collectors.joining())
                        + "} ").collect(Collectors.joining());
    }

}
