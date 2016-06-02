package logicalreasoner.inference;

import expression.sentence.Constant;
import expression.sentence.Exists;
import expression.sentence.Sentence;
import logicalreasoner.truthassignment.TruthAssignment;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * An existential instantiation is the substitution of an
 * existentially quantified variable, which can be replaced by
 * any number of branching constants. The number of branches in
 * the instantiation depends on the number of active constants in
 * that branch.
 */
public class ExistentialInstantiation extends Branch {
  private List<Constant> constants;
  private Constant newConstant;

  public ExistentialInstantiation(TruthAssignment h, Exists o, int i, int j, Set<Constant> s) {
    super(h, o, i, j);
    constants = new ArrayList<>(s);
    constants.forEach(constant -> {
      TruthAssignment t = new TruthAssignment();
      t.setTrue(o.instantiate(constant, o.getVariable()), i);
      addBranch(t);
    });
    newConstant = Constant.getNewUniqueConstant();
    TruthAssignment t = new TruthAssignment();
    t.setTrue(o.instantiate(newConstant, o.getVariable()), i);
    addBranch(t);
  }

  @Override
  public void infer(TruthAssignment h) {
    Set<Constant> s = h.getConstants();
    List<TruthAssignment> l = new ArrayList<>();
    for (int i = 0; i < constants.size(); ++i) {
      if (s.contains(constants.get(i)))
        l.add(branches.get(i));
    }
    l.add(branches.get(branches.size() - 1)); // add branch for new constant
    if (l.size() < 2)
      throw new RuntimeException("Tried to create a single branch!");
    h.addChildren(l);
  }

  public List<Constant> getConstants() {
    return constants;
  }

  @Override
  public int size() {
    return constants.size();
  }

  public String toString() {
    return "ExistentialInstantiation " + inferenceNum + "- from: " + origin + "=" + parent.models(origin) + " [" + justificationNum + "] over constants: " +
            branches.stream().map(b -> b.getConstants().stream().map(Sentence::toString).collect(Collectors.joining()) + " ").collect(Collectors.joining());
  }
}
