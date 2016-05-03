package logicalreasoner.prover;

import expression.sentence.Sentence;
import logicalreasoner.inference.Branch;
import logicalreasoner.inference.Decomposition;
import logicalreasoner.inference.Inference;
import logicalreasoner.truthassignment.TruthAssignment;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The SemanticProver class represents a logical reasoner
 * which determines the validity of arguments by
 * generating a TruthAssignment Tree (like a Truth Tree,
 * except that mappings from Sentences to their values are
 * stored in each node, not only the Sentences themselves).
 */
public class SemanticProver implements Runnable {

  public static void decompose(TruthAssignment t, List<Inference> inferences) {
    SemanticProver prover = new SemanticProver(t);
    prover.run();
    inferences.addAll(prover.inferenceList);
  }

  private Set<Sentence> premises, interests;

  //Stores the initial/root TruthAssignment
  private TruthAssignment masterFunction;

  //The leaves of the TruthAssignment tree
  private List<TruthAssignment> openBranches;

  //All statements which can be branched upon (in descending order of size)
  private PriorityQueue<Branch> branchQueue;
  private int inferenceCount;

  //Keep an ordered list of inferences for proof printing
  private List<Inference> inferenceList;

  private boolean print;

  /**
   * Initialize the reasoner with the premises and the negation of all interests
   *
   * @param premises the prior knowledge of the prover
   * @param interest the interest of the prover (to be negated)
   * @param print    Print log output if true
   */
  public SemanticProver(Set<Sentence> premises, Sentence interest, boolean print) {
    this.premises = premises;
    if (premises == null)
      this.premises = new HashSet<>();
    interests = new HashSet<>();

    if (interest != null)
      interests.add(interest);

    inferenceList = new ArrayList<>();
    inferenceCount = 1;
    masterFunction = new TruthAssignment();
    premises.forEach(s -> masterFunction.setTrue(s, 0));
    interests.forEach(s -> masterFunction.setFalse(s, 0));

    openBranches = new ArrayList<>();
    openBranches.add(masterFunction);

    branchQueue = new PriorityQueue<>((b1, b2) -> {
      if (b1.size() != b2.size())
        return b1.size() > b2.size() ? 1 : 0;
      return b1.getOrigin().size() > b2.getOrigin().size() ? 1 : 0;
    });

    this.print = print;
  }

  public List<Inference> getInferenceList() {
    return new ArrayList<>(inferenceList);
  }

  public SemanticProver(TruthAssignment truthAssignment) {
    premises = null;
    interests = null;

    masterFunction = truthAssignment;
    inferenceList = new ArrayList<>();
    inferenceCount = 1;

    openBranches = new ArrayList<>();
    openBranches.add(masterFunction);

    branchQueue = new PriorityQueue<>((b1, b2) -> {
      if (b1.size() != b2.size())
        return b1.size() > b2.size() ? 1 : 0;
      return b1.getOrigin().size() > b2.getOrigin().size() ? 1 : 0;
    });

    print = false;
  }

  /**
   * Reason over the TruthAssignment h by decomposing statements
   *
   * @param h the TruthAssignment to reason over
   * @return true if changes to h have been made as a result of this call, false otherwise
   */
  public boolean reason(TruthAssignment h) {
    List<Inference> inferences = h.getSentencesUpwards().stream()
            .filter(s -> !h.isDecomposed(s))
            .map(s -> s.reason(h, inferenceCount++)).filter(i -> i != null)
            .collect(Collectors.toList());

    inferences.forEach(i -> {
      h.setDecomposed(i.getOrigin());
      if (i instanceof Decomposition) {
        inferenceList.add(i);
        i.infer(h);
      } else if (i instanceof Branch)
        branchQueue.offer((Branch) i);
    });

    return !inferences.isEmpty();
  }

  /**
   * Run the prover over the given premises & conclusion
   */
  public void run() {
    if (print) {
      System.out.println("Premises: " + premises);
      System.out.println("Interests: " + interests);
    }

    while (!reasoningCompleted()) {
      boolean updated = true;

      // Always decompose all statements before branching
      while (!openBranches.isEmpty() && updated) {
        //closeBranches();
        updated = openBranches.stream().map(this::reason).collect(Collectors.toList()).contains(true);
      }

      closeBranches();

      //Branch once on the largest branching statement then loop back around
      if (!openBranches.isEmpty() && !branchQueue.isEmpty())
        addBranches();
    }

    //If the tree has been completely decomposed
    //without inconsistencies, the argument is invalid
    if (print) {
      if (isConsistent()) {
        System.out.println("\nThe argument is NOT valid.\n");
      } else {
        System.out.println("\nThe argument IS valid.\n");
      }

      printInferences();
      printInferenceList();
//            printTruthTree();
    }
  }

  /**
   * Check if all TruthAssignments are consistent and fully decomposed.
   *
   * @return true if all open branches are fully decomposed
   */
  private boolean reasoningCompleted() {
    return openBranches.isEmpty() || (branchQueue.isEmpty() && openBranches.stream().allMatch(TruthAssignment::decomposedAll));
  }

  public boolean isConsistent() {
    return !openBranches.isEmpty() && openBranches.stream().allMatch(TruthAssignment::isConsistent);
  }

  /**
   * Print the TruthAssignment tree generated by the argument
   */
  private void printInferences() {
    if (print) {
      masterFunction.print();
      System.out.println();
    }
  }

  /**
   * Print the Set of open Branches
   */
  public void printBranches() {
    if (print) {
      System.out.println("Branches: ");
      openBranches.forEach(System.out::println);
      System.out.println("Branch Queue: " + branchQueue + "\n");
    }
  }

  /**
   * Print the list of inferences made to build the truth tree
   */
  private void printInferenceList() {
    if (print) {
      System.out.println("Inference List: ");
      inferenceList.forEach(System.out::println);
      System.out.println();
    }
  }

//    private void printTruthTree() {
//    	if (print) {
//    		System.out.println("Truth tree: ");
//    		masterFunction.makeTruthTree().print();
//    		System.out.println();
//    	}
//    }

  public TruthAssignment getTruthAssignment() {
    return masterFunction;
  }

  /**
   * Branch the Sentence on the top of the branchQueue
   * and update the openBranches Set to contain those children.
   */
  private void addBranches() {
    Branch b = branchQueue.poll();
    //System.out.println("Branching on: " + b);

    inferenceList.add(b);

    if (openBranches.isEmpty())  //Make sure no unnecessary branching occurs
      return;

    b.getParent().getLeaves().forEach(leaf -> {
      b.infer(leaf);
      leaf.getChildren().forEach(l -> openBranches.add(l));
      openBranches.remove(leaf);
    });

    closeBranches();    //Clean up any inconsistent branches
  }

  private void closeBranches() {
    openBranches.removeIf(b -> !b.isConsistent());
  }
}
