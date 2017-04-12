package expression.metasentence;

import metareasoner.metainference.MetaInference;
import metareasoner.proof.Proof;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

/**
 * The NOT class represents the Meta-logical NOT expression,
 * taking a single MetaSentence as an argument
 */
public class NOT extends MetaSentence {
  public NOT(MetaSentence s, HashSet<TruthAssignmentVar> vars) {
    super(new ArrayList<>(Collections.singletonList(s)), "NOT", "NOT", vars);
  }

  public MetaInference reason(Proof p, int inferenceNum) {
    return null;
  }

  public String toString() {
    StringBuilder builder = new StringBuilder();

    vars.forEach(v -> builder.append("∀").append(v.toSExpression()));

    if (!vars.isEmpty())
      builder.append(" ");

    builder.append("[").append(name).append(" ").append(args.get(0).toString()).append("]");

    return builder.toString();
  }


  public String toSExpression() {
    StringBuilder builder = new StringBuilder();

    vars.forEach(v -> builder.append("∀").append(v.toSExpression()));

    if (!vars.isEmpty())
      builder.append(" ");

    builder.append(symbol).append("[").append(args.get(0).toSExpression()).append("]");

    return builder.toString();
  }

  @Override
  public MetaSentence toplevelCopy(HashSet<TruthAssignmentVar> vars) {
    HashSet<TruthAssignmentVar> allVars = new HashSet<>(vars);
    allVars.addAll(this.vars);
    return new NOT((MetaSentence) args.get(0), allVars);
  }

}
