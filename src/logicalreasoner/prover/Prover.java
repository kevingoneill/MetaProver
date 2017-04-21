package logicalreasoner.prover;

import expression.sentence.Sentence;
import logicalreasoner.inference.Branch;
import logicalreasoner.inference.Decomposition;
import logicalreasoner.inference.Inference;
import logicalreasoner.truthassignment.Pair;
import logicalreasoner.truthassignment.TruthAssignment;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The Prover class represents a logical reasoner
 * which determines the validity of arguments by
 * generating a TruthAssignment Tree (like a Truth Tree,
 * except that mappings from Sentences to their values are
 * stored in each node, not only the Sentences themselves).
 */
public class Prover implements Runnable {

  public static void decompose(TruthAssignment t, List<Inference> inferences) {
    Prover prover = new Prover(t);
    prover.run();
    inferences.addAll(prover.inferenceList);
  }

  Long maxRuntime = null,
          startTime = null;

  protected Set<Sentence> premises, goals;

  //Stores the initial/root TruthAssignment
  protected TruthAssignment masterFunction;

  //The leaves of the TruthAssignment tree
  protected List<TruthAssignment> openBranches;

  //All statements which can be branched upon (in descending order of size)
  protected ArrayList<Branch> branchQueue;
  protected int inferenceCount;

  //Keep an ordered list of inferences for proof printing
  protected List<Inference> inferenceList;
  protected boolean print, addedBranches, finishedProof, decomposeAll;

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
      return j - i;

    i = b1.compareTo(b2);
    if (i != 0)
      return i;

    i = b1.getParent().getLeavesParallel().filter(openBranches::contains).mapToInt(l ->
            (int) b1.getBranches().stream().flatMap(b -> {
              b.setParent(null);
              return b.flattenSerial().flatMap(p -> p.sentence.getSubSentences());
            }).filter(l::isMapped).count()).sum();
    j = b2.getParent().getLeavesParallel().filter(openBranches::contains).mapToInt(l ->
            (int) b2.getBranches().stream().flatMap(b -> {
              b.setParent(null);
              return b.flattenSerial().flatMap(p -> p.sentence.getSubSentences());
            }).filter(l::isMapped).count()).sum();

    if (i != j)
      return j - i;

    return b2.getInferenceNum() - b1.getInferenceNum();
  };

  /**
   * Initialize the reasoner with the premises and the negation of all goals
   *
   * @param premises the prior knowledge of the prover
   * @param goals the goals of the prover (to be negated)
   * @param print    Print log output if true
   */
  public Prover(Set<Sentence> premises, Set<Sentence> goals, boolean print) {
    this.premises = new HashSet<>(premises);

    inferenceList = new ArrayList<>();
    inferenceCount = 1;
    masterFunction = new TruthAssignment();
    masterFunction.addConstants(Sentence.getAllConstants());
    Decomposition c = new Decomposition(masterFunction, null, 0, 0);
    int premiseCount = -1;

    for (Sentence s : this.premises) {
      Decomposition p = new Decomposition(masterFunction, null, premiseCount, premiseCount--);
      p.setTrue(s);
      p.infer(masterFunction);
      inferenceList.add(p);
    }

    if (goals.contains(null))
      goals.removeIf(Objects::isNull);
    goals.forEach(c::setFalse);
    c.infer(masterFunction);
    inferenceList.add(c);
    goals.forEach(masterFunction::addSupposition);
    this.goals = new HashSet<>(goals);

    openBranches = new CopyOnWriteArrayList<>();
    openBranches.add(masterFunction);

    branchQueue = new ArrayList<>();
    this.print = print;
    addedBranches = false;
    finishedProof = false;
    decomposeAll = false;
  }

  /**
   * Initialize the reasoner with the premises and the negation of all goals
   *
   * @param premises the prior knowledge of the prover
   * @param goal the goal of the prover (to be negated)
   * @param print    Print log output if true
   */
  public Prover(Set<Sentence> premises, Sentence goal, boolean print) {
    this(premises, Collections.singleton(goal), print);
  }

  public Prover(Set<Sentence> premises, boolean print) {
    this(premises, Collections.emptySet(), print);
  }

  public List<Inference> getInferenceList() {
    return new ArrayList<>(inferenceList);
  }

  public Prover(TruthAssignment truthAssignment) {
    premises = truthAssignment.keySet().stream().filter(truthAssignment::models).collect(Collectors.toSet());
    goals = truthAssignment.keySet().stream().filter(s -> !truthAssignment.models(s)).collect(Collectors.toSet());

    masterFunction = truthAssignment;
    inferenceList = new CopyOnWriteArrayList<>();
    inferenceCount = 1;

    openBranches = new CopyOnWriteArrayList<>();
    openBranches.add(masterFunction);

    branchQueue = new ArrayList<>();
    print = false;
    addedBranches = false;
    finishedProof = false;
    decomposeAll = true;
  }

  public Prover(Set<Sentence> premises, Sentence goal, boolean print, int runTime) {
    this(premises, goal, print);
    maxRuntime = (long) runTime * 1000;
    finishedProof = false;
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

  public Stream<Pair> infer(Inference i) {
    if (i == null)
      return Stream.empty();
    if (i instanceof Decomposition) {
      inferenceList.add(i);
      return i.infer(i.getParent());
    } else if (i instanceof Branch) {
      branchQueue.add((Branch) i);
      addedBranches = true;
    }
    return Stream.empty();
  }

  /**
   * Run the prover over the given premises & conclusion
   */
  public void run() {
    startTime = System.currentTimeMillis();

    printArgument();
    runPropositionally();
    finishedProof = true;
    printResult();
  }

  public void runPropositionally() {
    while (!propositionalReasoningCompleted()) {  // Reason propositionally while possible

      if (maxRuntime != null && (System.currentTimeMillis() - startTime) >= maxRuntime)
        return;

      boolean updated = true;
      int i = inferenceList.size();
      // Always decompose all statements before branching
      while (updated && !openBranches.isEmpty()) {
        openBranches.parallelStream().flatMap(b -> reason(b, false)).collect(Collectors.toList()).forEach(this::infer);

        updated = i != inferenceList.size();
        i = inferenceList.size();
      }

      closeBranches();

      //Branch once on the largest branching statement then loop back around
      if (!openBranches.isEmpty() && !branchQueue.isEmpty())
        addBranches();

      closeBranches();

      if (!decomposeAll && isInvalid())
        break;

      //System.out.println("# of Inferences:\t" + inferenceList.size() + "\t\t# of Open Branches:\t" + openBranches.size() + "\t\tBranch Queue Size:\t" + branchQueue.size());
      //printInferences();
    }

    //printInferenceList();
    //printBranches();
  }

  /**
   * Check if all TruthAssignments are consistent and fully decomposed.
   *
   * @return true if all open branches are fully decomposed
   */
  public boolean reasoningCompleted() {
    return openBranches.isEmpty() || (branchQueue.isEmpty() && openBranches.parallelStream().allMatch(TruthAssignment::decomposedAll));
  }

  public boolean finishedProof() {
    return finishedProof;
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
  public void printInferences() {
    if (print) {
      masterFunction.print();
      System.out.println();
    }
  }

  public void printArgument() {
    if (print) {
      System.out.println("Premises: " + premises);
      System.out.println("goals: " + goals);
    }
  }

  public void printResult() {
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
  public void printInferenceList() {
    if (print) {
      //System.out.println("Inference List: ");
      inferenceList.forEach(System.out::println);
      //System.out.println();
    }
  }

  public TruthAssignment getTruthAssignment() {
    return masterFunction;
  }

  /**
   * Branch the Sentence on the top of the branchQueue
   * and update the openBranches Set to contain those children.
   */
  public void addBranches() {
    if (addedBranches) {
      Collections.sort(branchQueue, branchComparator);
      addedBranches = false;
    }

    Branch b = branchQueue.get(branchQueue.size() - 1);
    branchQueue.remove(branchQueue.size() - 1);
    //System.out.println("Branching on: " + b + "\n" + openBranches);
    if (openBranches.isEmpty())  //Make sure no unnecessary branching occurs
      return;
    inferenceList.add(b);

    b.getParent().getLeaves().filter(openBranches::contains).flatMap(b::infer).forEach(p -> {
    });
    b.getInferredOver().forEach(leaf -> {
      openBranches.addAll(leaf.getChildren());
      if (!leaf.getChildren().isEmpty())
        openBranches.remove(leaf);
    });
  }

  public void closeBranches() {
    openBranches = openBranches.parallelStream().filter(TruthAssignment::areParentsConsistent).collect(Collectors.toList());
  }

  protected void getCounterExamples() {
    if (masterFunction.isConsistent()) {
      masterFunction.getLeavesParallel().filter(h -> h.isConsistent() && h.decomposedAll()).forEach(t -> System.out.println(t.getCounterExample() + "\n"));
    }
  }

  public boolean isInvalid() {
    return branchQueue.isEmpty() && openBranches.parallelStream().anyMatch(TruthAssignment::isSatisfied);
  }

  public int getInferenceCount() {
    return inferenceCount;
  }

  public void incrementInferenceCount() {
    ++inferenceCount;
  }

  public ArrayList<Branch> getBranchQueue() {
    return branchQueue;
  }
}
