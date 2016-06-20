package logicalreasoner.inference;

import expression.sentence.Sentence;
import logicalreasoner.truthassignment.Pair;
import logicalreasoner.truthassignment.TruthAssignment;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A Branch is an inference which generates two or more
 * possible TruthAssignments given a parent TruthAssignment
 * and an origin Sentence.
 */
public class Branch extends Inference {

  protected List<TruthAssignment> branches;

  public Branch(TruthAssignment p, Sentence s, int i, int j) {
    super(p, s, i, j);
    branches = new ArrayList<>();
  }

  @Override
  public Stream<Pair> infer(TruthAssignment h) {
    return h.addChildren(branches);
  }

  public int size() {
    return branches.size();
  }

  public void addBranch(TruthAssignment h) {
    branches.add(h);
  }

  public List<TruthAssignment> getBranches() {
    return branches;
  }

  public boolean equals(Object o) {
    if (o instanceof Branch) {
      Branch b = (Branch) o;
      if (!super.equals(b) || branches.size() != b.branches.size())
        return false;

      for (int i = 0; i < branches.size(); ++i) {
        if (!branches.get(i).equals(b.branches.get(i)))
          return false;
      }
      return true;
    }
    return false;
  }

  public String toString() {
    return "Branch " + inferenceNum + "- from: " + origin + "=" + parent.models(origin) + " [" + justificationNum + "] to branches: " +
            branches.stream().map(b -> "{" +
                    b.keySet().stream().map(s ->
                            s.toString() + "=" + b.models(s)).collect(Collectors.joining())
                    + "} ").collect(Collectors.joining());
  }

}
