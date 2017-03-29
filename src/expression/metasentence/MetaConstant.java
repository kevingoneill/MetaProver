package expression.metasentence;

import metareasoner.metainference.MetaInference;
import metareasoner.proof.Proof;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by kevin on 3/22/16.
 */
public class MetaConstant extends MetaSentence {
  public static final MetaConstant TAUTOLOGY = new MetaConstant(true, "Tautology"),
          CONTRADICTION = new MetaConstant(false, "Contradiction"),
          CONTINGENCY = new MetaConstant(null, "Contingency");

  private Boolean value;

  private MetaConstant(Boolean b, String name) {
    super(new ArrayList<>(), name, name, new HashSet<>());
    value = b;
  }

  public Boolean getValue() {
    return value;
  }

  public MetaInference reason(Proof p, int inferenceNum) {
    return null;
  }

  @Override
  public MetaSentence toplevelCopy(HashSet<TruthAssignmentVar> vars) {
    return this;
  }

}
