package expression.metasentence;

import metareasoner.metainference.MetaInference;
import metareasoner.proof.Proof;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

  @Override
  public MetaSentence toplevelCopy(HashSet<TruthAssignmentVar> vars) {
    HashSet<TruthAssignmentVar> allVars = new HashSet<>(vars);
    allVars.addAll(this.vars);
    ArrayList<MetaSentence> newArgs = new ArrayList<>();
    args.forEach(a -> newArgs.add((MetaSentence) a));
    return new AND(newArgs, allVars);
  }

  public MetaInference reason(Proof p, int inferenceNum) {
    ArrayList<MetaSentence> inferences = new ArrayList<>();
    args.forEach(a -> inferences.add(((MetaSentence) a).toplevelCopy(vars)));
    return new MetaInference(this, inferences, inferenceNum, false, symbol);
  }

  public MetaInference reasonContained(Proof p, int inferenceNum) {
    List<MetaInference> metaInferences;
    metaInferences = args.stream().map(a -> ((MetaSentence) a).reason(p, inferenceNum)).collect(Collectors.toList());

    ArrayList<MetaSentence> newArgs = new ArrayList<>();
    IntStream.range(0, metaInferences.size()).forEach(i -> {
      if (metaInferences.get(i) == null)
        newArgs.add((MetaSentence) args.get(i));
      else
        newArgs.addAll(metaInferences.get(i).getInferences());
    });

    metaInferences.removeIf(Objects::isNull);
    AND and = new AND(newArgs, vars);
    if (metaInferences.stream().anyMatch(Objects::nonNull) && !equals(and)) {
      ArrayList<MetaSentence> a = new ArrayList<>();
      a.add(and);
      return new MetaInference(this, a, inferenceNum, true, metaInferences.get(0).getSymbol());
    }

    return null;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this)
      return true;
    if (o instanceof AND) {
      AND and = (AND) o;
      return args.stream().allMatch(and.args::contains) && and.args.stream().allMatch(args::contains);
    }
    return false;
  }
}
