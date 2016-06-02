package logicalreasoner.prover;

import expression.sentence.Constant;
import expression.sentence.ForAll;
import expression.sentence.Sentence;
import logicalreasoner.inference.Inference;
import logicalreasoner.inference.UniversalInstantiation;
import logicalreasoner.truthassignment.TruthAssignment;

import java.util.PriorityQueue;
import java.util.Set;
import java.util.stream.IntStream;

/**
 * The FirstOrderProver class provides the same functionality of
 * its SemanticProver superclass, but extends to implement
 * reasoning over first order logic.
 */
public class FirstOrderProver extends SemanticProver {

  /**
   * Initialize the reasoner with the premises and the negation of all interests
   *
   * @param premises the prior knowledge of the prover
   * @param interest the interest of the prover (to be negated)
   * @param print    Print log output if true
   */
  public FirstOrderProver(Set<Sentence> premises, Sentence interest, boolean print) {
    super(premises, interest, print);
  }

  public FirstOrderProver(TruthAssignment truthAssignment) {
    super(truthAssignment);
  }

  public boolean instantiateQuantifier(TruthAssignment h) {
    System.out.println("instantiateQuantifier");
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

    System.out.println("################################################\n" + quantifierQueue);

    Inference i = null;
    Sentence s = null;

    while (i == null) {
      s = quantifierQueue.poll();
      if (s == null)
        return false;
      if (s instanceof ForAll && h.models(s) && h.getConstants().isEmpty()) {
        h.addConstant(Constant.getNewUniqueConstant());
      }
      i = s.reason(h.getParentContaining(s), ++inferenceCount, h.getInferenceNum(s, h.models(s)));
    }

    infer(i, h.getParentContaining(i.getOrigin()));
    System.out.println(i + "\n----------------------------------------------\nInstantiating: " + s);
    printInferenceList();
    return true;
  }

  protected void infer(Inference i, TruthAssignment h) {
    if (i instanceof UniversalInstantiation) {
      inferenceList.add(i);
      i.infer(h.getParentContaining(i.getOrigin()));
    } else
      super.infer(i, h);
  }

  /**
   * Run the prover over the given premises & conclusion
   */
  public void run() {
    if (print) {
      System.out.println("Premises: " + premises);
      System.out.println("Interests: " + interests);
    }
    boolean updated = true;
    while (!reasoningCompleted()) {
      runPropositionally();

      if (isInvalid())
        break;

      if (updated && !openBranches.isEmpty()) {
        if (!branchQueue.isEmpty())
          throw new RuntimeException("Branch queue is not empty before first-order reasoning!");

        // Instantiate a single quantifier (anyMatch terminates after first successful call)
        //updated = openBranches.stream().anyMatch(this::instantiateQuantifier);
        updated = IntStream.range(0, openBranches.size() - 1).map(i -> openBranches.size() - 2 - i).anyMatch(i -> instantiateQuantifier(openBranches.get(i)));

        while (updated && !branchQueue.isEmpty())
          addBranches();

        if (isInvalid())
          break;
      }

      if (inferenceCount >= 9)
        System.exit(1);
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
    }
  }
}
