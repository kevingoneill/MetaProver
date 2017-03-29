package logicalreasoner.prover;

import expression.sentence.Sentence;
import logicalreasoner.inference.*;
import logicalreasoner.truthassignment.Pair;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by kevin on 7/5/16.
 */
public class MATRProver extends FirstOrderProver {

  public MATRProver(Set<Sentence> premises, Sentence interest) {
    super(premises, interest, false);
  }

  public MATRProver(Set<Sentence> premises, Sentence interest, int runTime) {
    super(premises, interest, false, runTime);
  }

  public MATRProver(Set<Sentence> premises, Sentence interest, boolean print) {
    super(premises, interest, print);
  }

  public MATRProver(Set<Sentence> premises, Sentence interest, boolean print, int runTime) {
    super(premises, interest, print, runTime);
  }

  @Override
  public Stream<Pair> infer(Inference i) {
    if (i instanceof ExistentialInstantiation) {
      if (!branchQueue.isEmpty())
        throw new RuntimeException("Branch queue not empty?\n");
      Branch b = new SingleBranchExistentialInstantiation((ExistentialInstantiation) i);
      branchQueue.add(b);
      addBranches();
    }
    return super.infer(i);
  }

  @Override
  /**
   * Create inferences for every closed branch
   */
  public void closeBranches() {
    openBranches = openBranches.parallelStream().map(h -> {
      if (h.areParentsConsistent())
        return h;

      Closure c = h.closeBranch(inferenceCount);
      if (c == null)
        throw new RuntimeException();
      inferenceList.add(c);
      ++inferenceCount;
      return null;
    }).filter(h -> h != null).collect(Collectors.toList());
  }
}
