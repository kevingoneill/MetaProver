package logicalreasoner.inference;

import expression.sentence.Sentence;
import logicalreasoner.truthassignment.Pair;
import logicalreasoner.truthassignment.TruthAssignment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A Branch is an inference which generates two or more
 * possible TruthAssignments given a parent TruthAssignment
 * and an origin Sentence.
 */
public class Branch extends Inference implements Comparable<Branch> {

  protected List<TruthAssignment> branches;
  private List<TruthAssignment> inferredOver;

  public Branch(TruthAssignment p, Sentence s, int i, int j) {
    super(p, s, i, j);
    branches = new ArrayList<>();
    inferredOver = Collections.synchronizedList(new ArrayList<>());
  }

  public List<TruthAssignment> getInferredOver() {
    return inferredOver;
  }

  @Override
  public Stream<Pair> infer(TruthAssignment h) {
    inferredOver.add(h);
    return h.addChildren(branches);
  }

  public int size() {
    return branches.size();
  }

  public void addBranch(TruthAssignment h) {
    branches.add(h);
    h.keySet().forEach(h::addSupposition);
  }

  public void addBranchWithoutSuppositions(TruthAssignment h) {
    branches.add(h);
  }

  public List<TruthAssignment> getBranches() {
    return branches;
  }

  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o instanceof Branch) {
      Branch b = (Branch) o;
      return super.equals(b) && branches.equals(b.branches);
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

  @Override
  public int compareTo(Branch o) {
    if (this == o)
      return 0;

    int i, j;

    if (this.size() != o.size())
      return o.size() - this.size();

    i = this.getOrigin().quantifierCount();
    j = o.getOrigin().quantifierCount();
    if (i != j)
      return i - j;

    i = branches.parallelStream().mapToInt(b -> b.getConstants().size()).sum();
    j = o.branches.parallelStream().mapToInt(b -> b.getConstants().size()).sum();
    if (i != j)
      return i - j;

    i = this.getOrigin().atomCount();
    j = o.getOrigin().atomCount();
    if (i != j)
      return i - j;

    i = this.getOrigin().size();
    j = o.getOrigin().size();
    if (i != j)
      return i - j;

    return 0;
  }
}
