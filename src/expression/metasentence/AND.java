package expression.metasentence;

import metareasoner.metainference.MetaInference;
import metareasoner.proof.Proof;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

/**
 * The AND class represents the meta-logical AND,
 * a high order conjunction of MetaSentences
 */
public class AND extends MetaSentence {
  public AND(ArrayList<MetaSentence> a, HashSet<TruthAssignmentVar> v) {
    super(new ArrayList<>(a), "AND", "AND", v);
  }

  public AND(MetaSentence s1, MetaSentence s2, HashSet<TruthAssignmentVar> v) {
    super(new ArrayList<>(Arrays.asList(s1, s2)), "AND", "AND", v);
  }

  public MetaInference reasonForwards(Proof p, int inferenceNum) {
    return infer(p, inferenceNum);
  }

  public MetaInference reasonBackwards(Proof p, int inferenceNum) {
    return infer(p, inferenceNum);
  }

  private MetaInference infer(Proof p, int inferenceNum) {
    ArrayList<MetaSentence> inferences = new ArrayList<>();
    args.forEach(a -> inferences.add((MetaSentence) a));
    return new MetaInference(this, inferences, inferenceNum, false, symbol);
  }
}
