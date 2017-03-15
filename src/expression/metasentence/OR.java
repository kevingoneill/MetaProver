package expression.metasentence;

import metareasoner.metainference.MetaInference;
import metareasoner.proof.Proof;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

/**
 * The OR class represents the meta-logical disjunction
 * of MetaSentences
 */
public class OR extends MetaSentence {
  public OR(ArrayList<MetaSentence> a, HashSet<TruthAssignmentVar> v) {
    super(new ArrayList<>(a), "OR", "OR", v);
  }

  public OR(MetaSentence s1, MetaSentence s2, HashSet<TruthAssignmentVar> v) {
    super(new ArrayList<>(Arrays.asList(s1, s2)), "OR", "OR", v);
  }

  public MetaInference reasonForwards(Proof p, int inferenceNum) {
    return reason(p, inferenceNum, true);
  }

  public MetaInference reasonBackwards(Proof p, int inferenceNum) {
    return reason(p, inferenceNum, false);
  }

  public MetaInference reason(Proof p, int inferenceNum, boolean isForwards) {
    ArrayList<MetaInference> inferences = new ArrayList<>();
    ArrayList<MetaSentence> removedArgs = new ArrayList<>();
    ArrayList<MetaSentence> newArgs = new ArrayList<>();

    args.forEach(a -> {
      if (!a.equals(MetaConstant.CONTRADICTION)) {
        MetaInference i = null;
        if (isForwards)
          i = ((MetaSentence) a).reasonForwards(p, inferenceNum);
        else
          i = ((MetaSentence) a).reasonBackwards(p, inferenceNum);
        if (i != null)
          inferences.add(i);
        else
          newArgs.add((MetaSentence) a);
      } else
        removedArgs.add((MetaSentence) a);
    });

    for (int i = 0; i < inferences.size(); ++i) {
      if (inferences.get(i) != null) {
        if (inferences.get(i).getInferences().size() <= 1)
          newArgs.addAll(inferences.get(i).getInferences());
        else
          newArgs.add(new AND(inferences.get(i).getInferences(), new HashSet<>()));
      }
    }

    OR or = new OR(newArgs, vars);

    if (!or.equals(this)) {
      if (newArgs.size() >= args.size()) {
        ArrayList<MetaSentence> a = new ArrayList<>();
        a.add(or);
        System.out.println("INFERRING: " + or + " from " + this);
        return new MetaInference(this, a, inferenceNum, true, inferences.get(0).getSymbol());
      } else if (newArgs.size() > 1) {
        ArrayList<MetaSentence> a = new ArrayList<>();
        a.add(or);
        System.out.println("INFERRING: " + or + " from " + this);
        return new MetaInference(this, a, inferenceNum, false, symbol);
      }

      ArrayList<MetaSentence> a = new ArrayList<>();
      a.add(newArgs.get(0));
      return new MetaInference(this, a, inferenceNum, false, symbol);
    }

    //*
    // If all disjuncts are ANDs, distributeOR over them
    if (args.stream().allMatch(a -> a instanceof AND)) {
      ArrayList<MetaSentence> ands = new ArrayList<>();
      args.forEach(a -> ands.add((MetaSentence) a));
      AND and = new AND(distributeOR(ands), vars);

      ArrayList<MetaSentence> a = new ArrayList<>();
      a.add(and);
      return new MetaInference(this, a, inferenceNum, false, symbol);
    }
    //*/

    return null;
  }

  /**
   * Distribute an OR of the ANDs in ands into an AND of ORs in the returned list
   *
   * @param disjuncts a disjunctive list of ANDs
   * @return a conjunctive list of ORs equivalent to ands
   */
  private ArrayList<MetaSentence> distributeOR(ArrayList<MetaSentence> disjuncts) {
    ArrayList<MetaSentence> conjuncts = new ArrayList<>();
    Optional<MetaSentence> a = disjuncts.stream().filter(d -> d instanceof AND).findFirst();

    if (a.isPresent()) {

      MetaSentence and = a.get();
      disjuncts.remove(and);

      and.args.forEach(disjunct -> {
        ArrayList<MetaSentence> orArgs = new ArrayList<>();
        orArgs.add((MetaSentence) disjunct);
        orArgs.addAll(disjuncts);

        conjuncts.addAll(distributeOR(orArgs));
      });
    } else
      // If no disjuncts are conjunctions, then we are done
      conjuncts.add(new OR(disjuncts, new HashSet<>()));

    return conjuncts;
  }

  public boolean equals(Object o) {
    if (o == this)
      return true;
    if (o instanceof OR) {
      OR or = (OR) o;
      return args.stream().allMatch(or.args::contains) && or.args.stream().allMatch(args::contains);
    }
    return false;
  }

  @Override
  public MetaSentence toplevelCopy(HashSet<TruthAssignmentVar> vars) {
    HashSet<TruthAssignmentVar> allVars = new HashSet<>(vars);
    allVars.addAll(this.vars);
    ArrayList<MetaSentence> newArgs = new ArrayList<>();
    args.forEach(a -> newArgs.add((MetaSentence) a));
    return new OR(newArgs, allVars);
  }
}
