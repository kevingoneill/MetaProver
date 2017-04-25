package metareasoner.proof;

import expression.metasentence.MetaSentence;
import expression.metasentence.TruthAssignmentVar;
import metareasoner.metainference.MetaInference;

import java.io.PrintStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The Proof class represents the state of a meta-logical proof,
 * with both forwards and backwards reasoning
 */
public class Proof {

  private ArrayList<MetaSentence> premises;
  private MetaSentence ultimateGoal;
  private ArrayList<Step> forwardsInferences, backwardsInferences;
  private HashMap<String, TruthAssignmentVar> truthAssignments;

  public Proof(ArrayList<MetaSentence> inferences, MetaSentence goal) {
    forwardsInferences = new ArrayList<>();
    backwardsInferences = new ArrayList<>();
    inferences.forEach(i -> forwardsInferences.add(new Step(i, null, null, true)));
    backwardsInferences.add(new Step(goal, null, null, false));
    premises = inferences;
    ultimateGoal = goal;
    truthAssignments = new HashMap<>();
  }

  public int hashCode() {
    return forwardsInferences.hashCode();
  }

  public boolean equals(Object o) {
    if (o instanceof Proof)
      return ((Proof) o).forwardsInferences.equals(forwardsInferences) && ((Proof) o).backwardsInferences.equals(backwardsInferences);
    return false;
  }

  public String toString() {
    return forwardsInferences.toString();
  }

  public HashMap<String, TruthAssignmentVar> getTruthAssignments() {
    return truthAssignments;
  }

  public void addTruthAssignment(TruthAssignmentVar v) {
    truthAssignments.put(v.getName(), v);
  }

  public void printInferences() {
    forwardsInferences.forEach(i -> System.out.println(i.getMetaSentence().toSExpression() + "\t" + i.getJustification()));
  }

  public void printgoals() {
    backwardsInferences.forEach(i -> System.out.println(i.getMetaSentence().toSExpression() + "\t" + i.getJustification()));
  }

  public void addForwardsInference(MetaSentence s, MetaInference i) {
    Step step = find(forwardsInferences, i.getOrigin());
    if (step != null) {
      step.setDecomposed();
      forwardsInferences.add(new Step(s, i, step, true));
    }
  }

  /**
   * Add a backwards Step consisting of MetaSentence s and MetaInference i
   *
   * @param s
   * @param i
   */
  public void addBackwardsInference(MetaSentence s, MetaInference i) {
    Step step = find(backwardsInferences, i.getOrigin());
    if (step != null) {
      step.setDecomposed();
      step.setJustification(i);
      backwardsInferences.add(new Step(s, null, step, false));
    }
  }

  private int indexOf(ArrayList<Step> a, MetaSentence s) {
    for (int i = 0; i < a.size(); ++i)
      if (a.get(i).getMetaSentence().equals(s))
        return i;
    return -1;
  }

  private Step find(ArrayList<Step> a, MetaSentence s) {
    for (int i = 0; i < a.size(); ++i)
      if (a.get(i).getMetaSentence().equals(s))
        return a.get(i);
    return null;
  }

  public List<MetaSentence> getNewForwardSentences() {
    return forwardsInferences.stream().filter(i -> !i.isDecomposed()).map(Step::getMetaSentence).collect(Collectors.toList());
  }

  public List<MetaSentence> getNewBackwardSentences() {
    return backwardsInferences.stream().filter(i -> !i.isDecomposed()).map(Step::getMetaSentence).collect(Collectors.toList());
  }

  public boolean isDecomposedForwards(MetaSentence s) {
    return forwardsInferences.stream().anyMatch(i -> i.getMetaSentence().equals(s) && i.isDecomposed());
  }

  public boolean isDecomposedBackwards(MetaSentence s) {
    return backwardsInferences.stream().anyMatch(i -> i.getMetaSentence().equals(s) && i.isDecomposed());
  }

  public boolean decomposedAll() {
    return forwardsInferences.stream().allMatch(Step::isDecomposed) &&
            backwardsInferences.stream().allMatch(Step::isDecomposed);
  }

  public boolean isComplete() {
    Step s = find(backwardsInferences, ultimateGoal);
    return s.getLeaves().stream().allMatch(l ->
            forwardsInferences.stream().anyMatch(f ->
                    f.getMetaSentence().equals(l.getMetaSentence())));
  }

  public void printProof(PrintStream stepStream, PrintStream justificationStream) {
    if (!isComplete()) {
      System.out.println("NO PROOF FOUND");
      forwardsInferences.forEach(f -> f.getMetaSentence().getVars().forEach(this::addTruthAssignment));
      backwardsInferences.forEach(b -> b.getMetaSentence().getVars().forEach(this::addTruthAssignment));
      return;
    }

    compressProof();
    ArrayList<Step> proof = generateProof();

    // Get the maximum metasentence length
    Integer max = proof.stream().mapToInt(s -> s.getMetaSentence().toSExpression().length()).max().orElse(10) + 10;

    for (int i = 0; i < proof.size(); ++i) {
      Step s = proof.get(i);
      MetaInference justification = s.getJustification();

      if (stepStream == justificationStream)
        System.out.printf("%2d. %-" + max + "s %s ", i, s.getMetaSentence().toSExpression(), s.getReason());
      else {
        stepStream.println(i + ".  " + s.getMetaSentence().toSExpression());
        justificationStream.print(s.getReason() + " ");
      }

      if (s.isForwards() && justification != null)
        justificationStream.println(indexOf(proof, s.getJustification().getOrigin()));
      else if (s.isForwards())
        justificationStream.println();
      else {
        if (s.getChildren().size() > 0) {
          justificationStream.print(indexOf(proof, s.getChildren().get(0).getMetaSentence()));
          for (int j = 1; j < s.getChildren().size(); ++j)
            justificationStream.print(", " + indexOf(proof, s.getChildren().get(j).getMetaSentence()));
          justificationStream.println();
        }
      }
    }

    proof.forEach(step -> step.getMetaSentence().getVars().forEach(this::addTruthAssignment));

    /*
    System.out.println("\n\n\n\nTruth Assignments used in the above proof: ");
    truthAssignments.forEach((n, v) -> {
      v.getTruthAssignment().print();
      //System.out.println();
      v.getInferences().forEach(System.out::println);
      System.out.println();
    });
    */
  }

  /**
   * Make the proof as compact as possible by merging equivalent
   * forwards & backwards steps
   */
  public void compressProof() {
    backwardsInferences.forEach(b -> {
      forwardsInferences.stream().filter(f -> f.getMetaSentence().equals(b.getMetaSentence())).forEach(f -> {
        b.setJustification(f.getJustification());
      });
    });
  }

  public ArrayList<Step> generateProof() {
    ArrayList<Step> a = getProofBackwards(find(backwardsInferences, ultimateGoal));

    /*
    a.forEach(s -> {
      if (s.getJustification() != null && s.isForwards())
        System.out.println(s + "\t" + s.getJustification().getOrigin());
      else if (s.getJustification() != null)
        System.out.println(s + "\t" + s.getChildren().stream().map(Step::getMetaSentence).collect(Collectors.toList()));
      else
        System.out.println(s);
    });
    System.out.println();
    */

    // remove unused steps
    boolean changed = true;
    while (changed) {
      changed = a.removeIf(s -> (a.indexOf(s) != a.size() - 1) && a.stream().noneMatch(s2 ->
              s2.getJustification() != null
                      && ((s2.isForwards() && s2.getJustification().getOrigin().equals(s.getMetaSentence()))
                      || (!s2.isForwards() && (s2.getChildren().isEmpty() || s2.getChildren().stream().anyMatch(c -> c.getMetaSentence().equals(s.getMetaSentence())))))
      ));
    }

    return a;
  }

  private ArrayList<Step> getProofBackwards(Step s) {
    ArrayList<Step> a = new ArrayList<>();

    if (s.getChildren().isEmpty()) {
      Step unifier = find(forwardsInferences, s.getMetaSentence());
      if (unifier != null) {
        getProofForwards(unifier).forEach(step -> {
          if (!a.contains(step))
            a.add(step);
        });
      }
    } else {
      for (Step child : s.getChildren()) {
        getProofBackwards(child).forEach(step -> {
          if (!a.contains(step))
            a.add(step);
        });
      }
      a.add(s);
    }

    return a;
  }

  private ArrayList<Step> getProofForwards(Step s) {
    ArrayList<Step> a = new ArrayList<>();
    if (s.getParent() != null)
      getProofForwards(s.getParent()).forEach(step -> {
        if (!a.contains(step))
          a.add(step);
      });
    if (!a.contains(s))
      a.add(s);
    return a;
  }
}
