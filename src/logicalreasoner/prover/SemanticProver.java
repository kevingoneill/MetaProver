package logicalreasoner.prover;

import expression.sentence.Sentence;
import logicalreasoner.inference.Branch;
import logicalreasoner.inference.Decomposition;
import logicalreasoner.inference.Inference;
import logicalreasoner.truthassignment.Pair;
import logicalreasoner.truthassignment.TruthAssignment;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
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
  protected PriorityBlockingQueue<Branch> branchQueue;
  protected int inferenceCount;

  //Keep an ordered list of inferences for proof printing
  protected List<Inference> inferenceList;

  protected boolean print;

  protected final Lock branchLock = new ReentrantLock();

  protected Comparator<Branch> branchComparator = (b1, b2) -> {
    int i, j;
    i = b1.getParent().getLeavesParallel().filter(openBranches::contains).mapToInt(l ->
            (int) b1.getBranches().stream().filter(b -> {
              b.setParent(l);
              return b.consistencyTest();
            }).count()).sum();

    j = b2.getParent().getLeavesParallel().filter(openBranches::contains).mapToInt(l ->
            (int) b2.getBranches().stream().filter(b -> {
              b.setParent(l);
              return b.consistencyTest();
            }).count()).sum();
    if (i != j)
      return i - j;
    return b1.compareTo(b2);
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

    inferenceList = new CopyOnWriteArrayList<>();
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

    branchQueue = new PriorityBlockingQueue<>(100, branchComparator);
    this.print = print;
  }

  public List<Inference> getInferenceList() {
    return new ArrayList<>(inferenceList);
  }

  public SemanticProver(TruthAssignment truthAssignment) {
    premises = null;
    interests = null;

    masterFunction = truthAssignment;
    inferenceList = new CopyOnWriteArrayList<>();
    inferenceCount = 1;

    openBranches = new ArrayList<>();
    openBranches.add(masterFunction);

    branchQueue = new PriorityBlockingQueue<>(100, branchComparator);
    print = false;
  }

  /**
   * Reason over the TruthAssignment h by decomposing statements
   *
   * @param h the TruthAssignment to reason over
   * @return true if changes to h have been made as a result of this call, false otherwise
   */
  public Stream<Inference> reason(TruthAssignment h, boolean overQuantifiers) {
    return h.flattenUndecomposedSerial()
            .map(p -> {
              if (p.sentence.isQuantifier() != overQuantifiers)
                return null;
              Inference i = p.sentence.reason(p.truthAssignment, inferenceCount,
                      p.truthAssignment.getInferenceNum(p.sentence, p.truthAssignment.models(p.sentence)));
              if (i != null)
                ++inferenceCount;
              return i;
            });
  }

  protected Stream<Pair> infer(Inference i) {
    if (i == null)
      return Stream.empty();
    if (i instanceof Decomposition) {
      inferenceList.add(i);
      return i.infer(i.getParent());
    } else if (i instanceof Branch) {
      branchQueue.offer((Branch) i);
    }
    return Stream.empty();
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
    //closeBranches();
    System.out.println("runPropositionally");
    while (!propositionalReasoningCompleted()) {  // Reason propositionally while possible
      boolean updated = true;
      int i = inferenceList.size();
      // Always decompose all statements before branching
      while (updated && !openBranches.isEmpty()) {
        openBranches.parallelStream().flatMap(b -> reason(b, false)).collect(Collectors.toCollection(HashSet::new)).forEach(this::infer);
        updated = i != inferenceList.size();
        i = inferenceList.size();
      }

      closeBranches();

      //Branch once on the largest branching statement then loop back around
      if (!openBranches.isEmpty() && !branchQueue.isEmpty())
        addBranches();

      closeBranches();

      if (isInvalid())
        break;

      System.out.println("# of Inferences:\t" + inferenceList.size() + "\t# of Open Branches:\t" + openBranches.size());
    }

    //printInferenceList();
    //printBranches();
  }

  /**
   * Check if all TruthAssignments are consistent and fully decomposed.
   *
   * @return true if all open branches are fully decomposed
   */
  protected boolean reasoningCompleted() {
    return openBranches.isEmpty() || (branchQueue.isEmpty() && openBranches.parallelStream().allMatch(TruthAssignment::decomposedAll));
  }


  /**
   * Check if all TruthAssignments are consistent and  all propositions have been fully decomposed.
   *
   * @return true if all open branches are fully decomposed
   */
  protected boolean propositionalReasoningCompleted() {
    return openBranches.isEmpty() || (branchQueue.isEmpty() && openBranches.parallelStream().allMatch(TruthAssignment::decomposedAllPropositions));
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

    //b.getParent().getLeaves().filter(openBranches::contains).forEach(b::infer);
    //openBranches = openBranches.parallelStream().flatMap(TruthAssignment::getLeaves).filter(TruthAssignment::areParentsConsistent).collect(Collectors.toList());

    b.getParent().getLeaves().filter(openBranches::contains).forEach(leaf -> {
      b.infer(leaf);
      leaf.getChildren().forEach(l -> openBranches.add(l));
      if (!leaf.getChildren().isEmpty())
        openBranches.remove(leaf);
    });

  }

  protected void closeBranches() {
    //openBranches.removeIf(b -> !b.areParentsConsistent());
    openBranches = openBranches.parallelStream().filter(TruthAssignment::areParentsConsistent).collect(Collectors.toList());
  }

  protected void getCounterExamples() {
    if (masterFunction.isConsistent()) {
      masterFunction.getLeavesParallel().filter(h -> h.isConsistent() && h.decomposedAll()).forEach(t -> System.out.println(t.getCounterExample() + "\n"));
    }
  }

  protected boolean isInvalid() {
    return branchQueue.isEmpty() && openBranches.parallelStream().anyMatch(TruthAssignment::isSatisfied);
  }
}
