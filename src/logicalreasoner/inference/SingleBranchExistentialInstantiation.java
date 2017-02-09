package logicalreasoner.inference;

/**
 * Created by kevin on 8/3/16.
 */
public class SingleBranchExistentialInstantiation extends Branch {
  public SingleBranchExistentialInstantiation(ExistentialInstantiation eI) {
    super(eI.parent, eI.origin, eI.inferenceNum, eI.justificationNum);
    addBranch(eI.additions);
  }
}
