package logicalreasoner.inference;

import expression.sentence.Constant;
import expression.sentence.Exists;
import expression.sentence.Sentence;
import logicalreasoner.truthassignment.Pair;
import logicalreasoner.truthassignment.TruthAssignment;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * An existential instantiation is the substitution of an
 * existentially quantified variable, which can be replaced by
 * any number of branching constants. The number of branches in
 * the instantiation depends on the number of active constants in
 * that branch.
 */
public class BranchingExistentialInstantiation extends Branch {
  private List<Sentence> constants;

  public BranchingExistentialInstantiation(TruthAssignment h, Exists o, int i, int j, Set<Sentence> s) {
    super(h, o, i, j);
    constants = new ArrayList<>(s);
    constants.add(Constant.getNewUniqueConstant());
    constants.forEach(constant -> {
      TruthAssignment t = new TruthAssignment();
      t.setTrue(o.instantiate(constant, o.getVariable()), i);
      addBranch(t);
    });
  }

  @Override
  public Stream<Pair> infer(TruthAssignment h) {
    Set<Sentence> s = h.getConstants();
    List<TruthAssignment> l = new ArrayList<>();
    for (int i = 0; i < constants.size(); ++i) {
      if (s.contains(constants.get(i)))
        l.add(branches.get(i));
    }
    l.add(branches.get(branches.size() - 1)); // add branch for new constant
    if (l.size() == 1)
      return h.merge(new TruthAssignment(l.get(0)));
    else
      return h.addChildren(l);
  }

  public List<Sentence> getConstants() {
    return constants;
  }

  @Override
  public int size() {
    return constants.size();
  }

  public String toString() {
    return "BranchingExistentialInstantiation " + inferenceNum + "- from: " + origin + "=" + parent.models(origin) + " [" + justificationNum + "] over constants: " +
            constants.stream().map(s -> s.toString() + " ").collect(Collectors.joining());
  }
}
