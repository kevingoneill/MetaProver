package logicalreasoner.prover;

import expression.sentence.Sentence;
import logicalreasoner.inference.Closure;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by kevin on 7/5/16.
 */
public class MATRProver extends SemanticProver {

  public MATRProver(Set<Sentence> premises, Sentence interest) {
    super(premises, interest, false);
  }

  public MATRProver(Set<Sentence> premises, Sentence interest, int runTime) {
    super(premises, interest, false, runTime);
  }

  @Override
  /**
   * Create inferences for every closed branch
   */
  protected void closeBranches() {
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
