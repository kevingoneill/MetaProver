package logicalreasoner.prover;

import expression.sentence.Sentence;
import logicalreasoner.inference.Branch;
import logicalreasoner.inference.Decomposition;
import logicalreasoner.inference.Inference;
import logicalreasoner.truthassignment.TruthAssignment;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

  protected Set<Sentence> premises, interests;

  //Stores the initial/root TruthAssignment
  protected TruthAssignment masterFunction;

  //The leaves of the TruthAssignment tree
  protected List<TruthAssignment> openBranches;

  //All statements which can be branched upon (in descending order of size)
  protected PriorityQueue<Branch> branchQueue;
  protected int inferenceCount;

  //Keep an ordered list of inferences for proof printing
  protected List<Inference> inferenceList;

  protected boolean print;

  protected Comparator<Branch> branchComparator = (b1, b2) -> {


    int i = b1.getParent().getLeaves().filter(openBranches::contains).mapToInt(l ->
            (int) b1.getBranches().parallelStream().map(b -> {
              TruthAssignment t = new TruthAssignment(b);
              t.setParent(l);
              return t;
            }).filter(TruthAssignment::consistencyTest).count()).sum();
    int j = b2.getParent().getLeaves().filter(openBranches::contains).mapToInt(l ->
            (int) b2.getBranches().parallelStream().map(b -> {
              TruthAssignment t = new TruthAssignment(b);
              t.setParent(l);
              return t;
            }).filter(TruthAssignment::consistencyTest).count()).sum();
    //System.out.println("FINISHED");
    if (i != j)
      return i - j;

    if (b1.size() != b2.size())
      return b2.size() - b1.size();

    i = (int) b1.getOrigin().getConstants().stream().filter(constant -> !constant.toString().startsWith("#")).count();
    j = (int) b2.getOrigin().getConstants().stream().filter(constant -> !constant.toString().startsWith("#")).count();
    if (i != j)
      return j - i;

    //if (b1.getOrigin().size() != b2.getOrigin().size())
    return b2.getOrigin().size() - b1.getOrigin().size();

    //return 0;
    //System.out.println("WORKING");




  };

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

    Decomposition c = new Decomposition(masterFunction, null, 0, 0);
    int premiseCount = -1;
    for (Sentence s : premises) {
      Decomposition p = new Decomposition(masterFunction, null, premiseCount--, 0);
      p.setTrue(s);
      p.infer(masterFunction);
      inferenceList.add(p);
    }

    interests.forEach(c::setFalse);
    c.infer(masterFunction);
    inferenceList.add(c);

    openBranches = new ArrayList<>();
    openBranches.add(masterFunction);

    branchQueue = new PriorityQueue<>(branchComparator);
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

    branchQueue = new PriorityQueue<>(branchComparator);
    print = false;
  }

  /**
   * Reason over the TruthAssignment h by decomposing statements
   *
   * @param h the TruthAssignment to reason over
   * @return true if changes to h have been made as a result of this call, false otherwise
   */
  public Stream<Inference> reason(TruthAssignment h, boolean overQuantifiers) {
   /*
    return h.flatten()
            .map(s -> {
              if (s.isQuantifier() != overQuantifiers || h.isDecomposed(s))
                return null;
              TruthAssignment ta = h.getParentContaining(s);
              Inference i = s.reason(ta, inferenceCount, ta.getInferenceNum(s, ta.models(s)));
              if (i != null)
                ++inferenceCount;
              return i;
            }).filter(i -> i != null);
    */

    return h.flatten3()
            .map(p -> {
              Sentence s = p.sentence;
              if (s.isQuantifier() != overQuantifiers)
                return null;
              TruthAssignment ta = p.truthAssignment;
              Inference i = s.reason(ta, inferenceCount, ta.getInferenceNum(s, ta.models(s)));
              if (i != null)
                ++inferenceCount;
              return i;
            }).filter(i -> i != null);
  }

  protected void infer(Inference i) {
    //i.getParent().setDecomposed(i.getOrigin());
    if (i instanceof Decomposition) {
      inferenceList.add(i);
      i.infer(i.getParent());
    } else if (i instanceof Branch) {
      branchQueue.offer((Branch) i);
    }
  }

  /**
   * Run the prover over the given premises & conclusion
   */
  public void run() {
    printArgument();
    runPropositionally();
    printResult();
  }

  public void runPropositionally() {
    closeBranches();
    System.out.println("\nrunPropositionally\n");
    while (!propositionalReasoningCompleted()) {  // Reason propositionally while possible
      boolean updated = true;
      List<Inference> inferences;
      // Always decompose all statements before branching
      while (updated && !openBranches.isEmpty()) {
        inferences = openBranches.parallelStream().flatMap(b -> reason(b, false)).distinct().collect(Collectors.toList());
        inferences.forEach(this::infer);
        updated = !inferences.isEmpty();
        //closeBranches();
      }

      //closeBranches();
      printInferences();

      //Branch once on the largest branching statement then loop back around
      if (!openBranches.isEmpty() && !branchQueue.isEmpty())
        addBranches();

      if (isInvalid())
        break;
      closeBranches();
    }

    //printInferences();
    //printInferenceList();
    //printBranches();
  }

  /**
   * Check if all TruthAssignments are consistent and fully decomposed.
   *
   * @return true if all open branches are fully decomposed
   */
  protected boolean reasoningCompleted() {
    return openBranches.isEmpty() || (branchQueue.isEmpty() && openBranches.stream().allMatch(TruthAssignment::decomposedAll));
  }


  /**
   * Check if all TruthAssignments are consistent and  all propositions have been fully decomposed.
   *
   * @return true if all open branches are fully decomposed
   */
  protected boolean propositionalReasoningCompleted() {
    return openBranches.isEmpty() || (branchQueue.isEmpty() && openBranches.stream().allMatch(TruthAssignment::decomposedAllPropositions));
  }

  public boolean isConsistent() {
    return !openBranches.isEmpty() && masterFunction.isConsistent();
  }

  /**
   * Print the TruthAssignment tree generated by the argument
   */
  protected void printInferences() {
    if (print) {
      masterFunction.print();
      System.out.println();
    }
  }

  protected void printArgument() {
    if (print) {
      System.out.println("Premises: " + premises);
      System.out.println("Interests: " + interests);
    }
  }

  protected void printResult() {
    //If the tree has been completely decomposed
    //without inconsistencies, the argument is invalid
    if (print) {
      if (isConsistent()) {
        System.out.println("\nThe argument is NOT valid. Counterexamples: \n");
        getCounterExamples();
      } else {
        System.out.println("\nThe argument IS valid.\n");
      }

      printInferences();
      printInferenceList();
    }
  }

  /**
   * Print the Set of open Branches
   */
  public void printBranches() {
    if (print) {
      System.out.println("Branches: ");
      openBranches.forEach(TruthAssignment::print);
      //System.out.println("Branch Queue: " + branchQueue + "\n");
    }
  }

  /**
   * Print the list of inferences made to build the truth tree
   */
  protected void printInferenceList() {
    if (print) {
      System.out.println("Inference List: ");
      inferenceList.forEach(System.out::println);
      System.out.println();
    }
  }

  public TruthAssignment getTruthAssignment() {
    return masterFunction;
  }

  /**
   * Branch the Sentence on the top of the branchQueue
   * and update the openBranches Set to contain those children.
   */
  protected void addBranches() {
    Branch b = branchQueue.poll();
    //System.out.println("Branching on: " + b + "\n" + openBranches);
    if (openBranches.isEmpty())  //Make sure no unnecessary branching occurs
      return;
    inferenceList.add(b);

    b.getParent().getLeaves().filter(openBranches::contains).forEach(leaf -> {
      b.infer(leaf);
      if (!leaf.getChildren().isEmpty()) {
        openBranches.remove(leaf);
        openBranches.addAll(leaf.getChildren());
      }
    });
  }

  protected void closeBranches() {
    openBranches.removeIf(b -> !b.areParentsConsistent());
  }

  protected void getCounterExamples() {
    if (masterFunction.isConsistent()) {
      masterFunction.getLeaves().filter(TruthAssignment::isConsistent).forEach(t -> System.out.println(t.getCounterExample() + "\n"));
    }
  }

  protected boolean isInvalid() {
    return branchQueue.isEmpty() && openBranches.stream().anyMatch(TruthAssignment::isSatisfied);
  }
}
