package logicalreasoner.prover;

import expression.sentence.Constant;
import expression.sentence.ForAll;
import expression.sentence.Sentence;
import logicalreasoner.inference.Inference;
import logicalreasoner.inference.UniversalInstantiation;
import logicalreasoner.truthassignment.TruthAssignment;

import java.util.ArrayList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

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

  private PriorityQueue<Map.Entry<Sentence, TruthAssignment>> makeQuantifierQueue() {
    PriorityQueue<Map.Entry<Sentence, TruthAssignment>> quantifierQueue = new PriorityQueue<>((e1, e2) -> {
      if (!e1.getValue().models(e1.getKey())) {      // Always remove negations (false assignments) first
        if (!e2.getValue().models(e2.getKey()))
          return 0;
        return -1;
      }
      if (!e2.getValue().models(e2.getKey()))
        return 1;


      /*
      if (e1 instanceof ForAll && e2 instanceof ForAll) {
        ForAll f1 = (ForAll) e1,
                f2 = (ForAll) e2;
      }
      */

      return Sentence.quantifierComparator.compare(e1.getKey(), e2.getKey());
    });

    openBranches.forEach(h ->
            h.getUnfinishedQuantifiersUpwards().entrySet().forEach(e -> {
              if (!quantifierQueue.contains(e))
                quantifierQueue.add(e);
            }));
    return quantifierQueue;
  }

  private void printQueue(PriorityQueue<Map.Entry<Sentence, TruthAssignment>> queue) {
    System.out.print("[");
    PriorityQueue<Map.Entry<Sentence, TruthAssignment>> q = new PriorityQueue<>(queue);
    if (!q.isEmpty())
      System.out.print(q.poll().getKey());
    while (!q.isEmpty()) {
      System.out.print(", " + q.poll().getKey());
    }
    System.out.println("]");
  }

  private boolean instantiateQuantifier(PriorityQueue<Map.Entry<Sentence, TruthAssignment>> quantifierQueue) {
    System.out.println("instantiateQuantifier");
    System.out.println("################################################\n");
    printQueue(quantifierQueue);
    Inference i = null;
    Sentence s = null;
    TruthAssignment h = null;

    while (i == null) {
      Map.Entry<Sentence, TruthAssignment> e = quantifierQueue.poll();
      s = e.getKey();
      h = e.getValue();
      if (s == null)
        return false;
      if (s instanceof ForAll && h.models(s) && h.getConstants().isEmpty()) {
        h.addConstant(Constant.getNewUniqueConstant());
      }
      i = s.reason(h, ++inferenceCount, h.getInferenceNum(s, h.models(s)));
    }

    infer(i, h);
    System.out.println(i + "\n----------------------------------------------\n");
    if (h.models(s))
      System.out.println("Instantiating: " + s + "\n");

    return true;
  }

  protected void infer(Inference i, TruthAssignment h) {
    if (i instanceof UniversalInstantiation) {
      inferenceList.add(i);
      ArrayList<TruthAssignment> origins = h.getConstantOrigins(((UniversalInstantiation) i).getInstance());

      if (i.getInferenceNum() == 25) {
        i.getParent().print();
        System.out.println("ORIGINS: " + origins);
      }

      if (origins.isEmpty())
        i.infer(h);
      else
        origins.stream().filter(TruthAssignment::isConsistent).forEach(i::infer);
    } else
      super.infer(i, h);
  }

  /**
   * Run the prover over the given premises & conclusion
   */
  public void run() {
    printArgument();
    boolean updated = true;
    while (!reasoningCompleted()) {
      runPropositionally();
      if (isInvalid())
        break;

      printInferences();
      printInferenceList();

      if (updated && !openBranches.isEmpty()) {
        if (!branchQueue.isEmpty())
          throw new RuntimeException("Branch queue is not empty before first-order reasoning!");

        PriorityQueue<Map.Entry<Sentence, TruthAssignment>> quantifierQueue = makeQuantifierQueue();
        do {
          updated = instantiateQuantifier(quantifierQueue);
        }
        while (!(quantifierQueue.isEmpty() || quantifierQueue.peek().getValue().models(quantifierQueue.peek().getKey())));

        while (updated && !branchQueue.isEmpty())
          addBranches();

        if (isInvalid())
          break;
        closeBranches();

        //if (inferenceCount > 30)
        //  System.exit(0);
      }

    }
    printResult();
  }
}
