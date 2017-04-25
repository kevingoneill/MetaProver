package metareasoner;

import expression.metasentence.MetaSentence;
import expression.metasentence.TruthAssignmentVar;
import metareasoner.metainference.MetaInference;
import metareasoner.proof.Proof;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The MetaProver class uses meta-reasoning to provide meta-proofs
 * for valid arguments
 */
public class MetaProver implements Runnable {

  private Proof proof;
  private ArrayList<MetaSentence> premises;
  private MetaSentence goal;
  int inferenceCount;

  private PrintStream stepStream = System.out,
          justificationStream = System.out;

  public MetaProver(ArrayList<MetaSentence> p, MetaSentence i) {
    premises = p;
    goal = i;
    proof = new Proof(p, i);
    inferenceCount = 1;
  }

  /**
   * Create a MetaProver which prints output to stepStream and justificationStream
   * @param p the premises
   * @param i the goal
   * @param stepStream the PrintStream to display inferences on
   * @param justificationStream the PrintStream to display justifications on
   */
  public MetaProver(ArrayList<MetaSentence> p, MetaSentence i, PrintStream stepStream, PrintStream justificationStream) {
    this(p, i);
    this.stepStream = stepStream;
    this.justificationStream = justificationStream;
  }

  public boolean reasonForwards(Proof p) {
    List<MetaInference> inferences = p.getNewForwardSentences().stream()
            .map(s -> s.reason(p, inferenceCount++)).filter(Objects::nonNull)
            .collect(Collectors.toList());

    inferences.forEach(i -> i.infer(p, true));

    return !inferences.isEmpty();
  }

  public boolean reasonBackwards(Proof p) {
    List<MetaInference> inferences = p.getNewBackwardSentences().stream()
            .map(s -> s.reason(p, inferenceCount++)).filter(Objects::nonNull)
            .collect(Collectors.toList());

    inferences.forEach(i -> i.infer(p, false));
    return !inferences.isEmpty();
  }

  public void run() {
    /*
    System.out.println("Premises: ");
    proof.printInferences();

    System.out.println("Goals: ");
    proof.printgoals();
    */

    while (!reasoningCompleted()) {
      if (!(reasonForwards(proof) || reasonBackwards(proof)))
        break;

      /*
      System.out.println("\nInferences: ");
      proof.printInferences();
      System.out.println("\ngoals: ");
      proof.printgoals();
      System.out.println("\n\n");
      */
    }

    proof.printProof(stepStream, justificationStream);
    //proof.getTruthAssignments().values().forEach(t -> t.getTruthAssignment().print());
  }

  public boolean proofFound() {
    return proof.isComplete();
  }

  public Map<String, TruthAssignmentVar> getTruthAssignments() {
	return proof.getTruthAssignments();
  }

  /**
   * Check if all TruthAssignments are consistent and fully decomposed.
   *
   * @return true if all open branches are fully decomposed
   */
  private boolean reasoningCompleted() {
    //System.out.println("Complete: " + proof.isComplete());
    //System.out.println("Decomposed: " + proof.decomposedAll());
    return proof.isComplete() || proof.decomposedAll();
  }
}
