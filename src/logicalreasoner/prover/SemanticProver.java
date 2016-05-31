package logicalreasoner.prover;

import expression.sentence.Constant;
import expression.sentence.ForAll;
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
    inferenceCount = 0;
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

    branchQueue = new PriorityQueue<>((b1, b2) -> {
      if (b1.size() != b2.size())
        return b2.size() - b1.size();
      if (b1.getOrigin().size() != b2.getOrigin().size())
        return b2.getOrigin().size() - b1.getOrigin().size();

      int i = (int) b1.getOrigin().getConstants().stream().filter(constant -> !constant.toString().startsWith("#")).count(),
              j = (int) b2.getOrigin().getConstants().stream().filter(constant -> !constant.toString().startsWith("#")).count();
      return j - i;
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
        return b2.size() - b1.size();
      if (b1.getOrigin().size() != b2.getOrigin().size())
        return b2.getOrigin().size() - b1.getOrigin().size();
      return (int) (b1.getOrigin().getConstants().stream().filter(c -> !c.toString().startsWith("#")).count() -
              b2.getOrigin().getConstants().stream().filter(c -> !c.toString().startsWith("#")).count());
    });

    print = false;
  }

  /**
   * Reason over the TruthAssignment h by decomposing statements
   *
   * @param h the TruthAssignment to reason over
   * @return true if changes to h have been made as a result of this call, false otherwise
   */
  public boolean reason(TruthAssignment h, boolean overQuantifiers) {
    List<Inference> inferences = h.getSentencesUpwards().stream()
            .filter(s -> !h.isDecomposed(s) && (s.isQuantifier() == overQuantifiers))
            .map(s -> s.reason(h.getParentContaining(s), ++inferenceCount, h.getInferenceNum(s, h.models(s)))).filter(i -> i != null)
            .collect(Collectors.toList());

    inferences.forEach(i -> infer(i, h.getParentContaining(i.getOrigin())));
    return !inferences.isEmpty();
  }

  public boolean instantiateQuantifier(TruthAssignment h) {
    //System.out.println("instantiateQuantifier");

    PriorityQueue<Sentence> quantifierQueue = new PriorityQueue<>((e1, e2) -> {
      if (!h.models(e1)) {      // Always remove negations (false assignments) first
        if (!h.models(e2))
          return 0;
        return -1;
      }
      if (!h.models(e2))
        return 1;

      return Sentence.quantifierComparator.compare(e1, e2);
    });

    h.getSentencesUpwards().stream().filter(s -> s.isQuantifier() && !h.isDecomposed(s)).forEach(s -> {
      if (!quantifierQueue.contains(s))
        quantifierQueue.add(s);
    });

    Inference i = null;
    Sentence s;

    while (i == null) {
      s = quantifierQueue.poll();
      if (s == null)
        return false;

      //System.out.println("Instantiating: " + s);
      //printInferences();
      //printInferenceList();

      if (s instanceof ForAll && h.models(s) && h.getConstants().isEmpty())
        h.addConstant(Constant.getNewUniqueConstant());

      i = s.reason(h.getParentContaining(s), ++inferenceCount, h.getInferenceNum(s, h.models(s)));
    }

    infer(i, h.getParentContaining(i.getOrigin()));
    //System.out.println(i + "\n----------------------------------------------");
    //printInferences();

    return true;
  }

  private void infer(Inference i, TruthAssignment h) {
    if (i.getOrigin() instanceof ForAll && h.models(i.getOrigin())) {
      inferInstantiations((Decomposition) i, h);
      return;
    }
    h.setDecomposed(i.getOrigin());
    if (i instanceof Decomposition) {
      inferenceList.add(i);
      i.infer(h);
    } else if (i instanceof Branch) {
      branchQueue.offer((Branch) i);
    }
  }

  private void inferInstantiations(Decomposition d, TruthAssignment h) {
    if (!(d.getOrigin() instanceof ForAll))
      throw new RuntimeException("Can only instantiate Universal Quanitifers");
    inferenceList.add(d);
    d.infer(h.getParentContaining(d.getOrigin()));
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
      runPropositionally();

      if (!openBranches.isEmpty()) {
        if (!branchQueue.isEmpty())
          throw new RuntimeException("Branch queue is not empty before first-order reasoning!");

        // Instantiate a single quantifier (anyMatch terminates after first successful call)
        openBranches.stream().anyMatch(this::instantiateQuantifier);
        while (!branchQueue.isEmpty())
          addBranches();
      }
    }

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
      System.out.println(masterFunction.getConstants());
    }
  }

  public void runPropositionally() {
    closeBranches();
    //System.out.println("runPropositionally");
    while (!propositionalReasoningCompleted()) {  // Reason propositionally while possible
      boolean updated = true;
      // Always decompose all statements before branching
      while (!openBranches.isEmpty() && updated) {
        updated = openBranches.stream().map(b -> reason(b, false)).collect(Collectors.toList()).contains(true);
        closeBranches();
      }

      closeBranches();
      //printInferences();

      //Branch once on the largest branching statement then loop back around
      if (!openBranches.isEmpty() && !branchQueue.isEmpty())
        addBranches();
    }
    //printInferences();
    printInferenceList();
  }

  /**
   * Check if all TruthAssignments are consistent and fully decomposed.
   *
   * @return true if all open branches are fully decomposed
   */
  private boolean reasoningCompleted() {
    return openBranches.isEmpty() || (branchQueue.isEmpty() && openBranches.stream().allMatch(TruthAssignment::decomposedAll));
  }


  /**
   * Check if all TruthAssignments are consistent and  all propositions have been fully decomposed.
   *
   * @return true if all open branches are fully decomposed
   */
  private boolean propositionalReasoningCompleted() {
    return openBranches.isEmpty() || (branchQueue.isEmpty() && openBranches.stream().allMatch(TruthAssignment::decomposedAllPropositions));
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

    b.getParent().getLeaves().stream().filter(TruthAssignment::isConsistent).forEach(leaf -> {
      b.infer(leaf);
      leaf.getChildren().forEach(l -> openBranches.add(l));
      openBranches.remove(leaf);
    });

    closeBranches();    //Clean up any inconsistent branches
  }

  private void closeBranches() {
    openBranches.removeIf(b -> !b.isConsistent() || !b.areParentsConsistent());
  }

  private void getCounterExamples() {
    if (!masterFunction.isConsistent())
      return;

    masterFunction.getLeaves().stream().filter(TruthAssignment::isConsistent).forEach(t -> System.out.println(t.getCounterExample() + "\n"));
  }
}
