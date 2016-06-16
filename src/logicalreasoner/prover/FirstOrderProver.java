package logicalreasoner.prover;

import expression.sentence.Constant;
import expression.sentence.ForAll;
import expression.sentence.Sentence;
import logicalreasoner.inference.Inference;
import logicalreasoner.inference.UniversalInstantiation;
import logicalreasoner.truthassignment.Pair;
import logicalreasoner.truthassignment.TruthAssignment;

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

  private PriorityQueue<Pair> makeQuantifierQueue() {
    PriorityQueue<Pair> quantifierQueue = new PriorityQueue<>((e1, e2) -> {
      if (!e1.truthAssignment.models(e1.sentence)) {      // Always remove negations (false assignments) first
        if (!e2.truthAssignment.models(e2.sentence))
          return 0;
        return -1;
      }
      if (!e2.truthAssignment.models(e2.sentence))
        return 1;

      return Sentence.quantifierComparator.compare(e1.sentence, e2.sentence);
    });

    openBranches.forEach(h ->
            h.getUnfinishedQuantifiersUpwards().forEachOrdered(e -> {
              if (!quantifierQueue.contains(e))
                quantifierQueue.add(e);
            }));
    return quantifierQueue;
  }

  private void printQueue(PriorityQueue<Pair> queue) {
    System.out.print("[");
    PriorityQueue<Pair> q = new PriorityQueue<>(queue);
    if (!q.isEmpty())
      System.out.print(q.poll().sentence);
    while (!q.isEmpty()) {
      System.out.print(", " + q.poll().sentence);
    }
    System.out.println("]");
  }

  private Inference instantiateQuantifier(PriorityQueue<Pair> quantifierQueue) {
    System.out.println("instantiateQuantifier");
    System.out.println("################################################\n");
    printQueue(quantifierQueue);
    Inference i = null;
    Sentence s = null;
    TruthAssignment h = null;

    while (i == null) {
      Pair p = quantifierQueue.poll();
      if (p == null) {
        printInferences();
        printInferenceList();
        //System.exit(1);
        return null;
      }
      s = p.sentence;
      h = p.truthAssignment;
      if (s == null)
        return null;
      if (s instanceof ForAll && h.models(s)) {
        Constant c = Constant.getNewUniqueConstant();
        h.getLeaves().forEach(l -> {
          if (l.getConstants().isEmpty())
            l.addConstant(c);
        });
      }
      TruthAssignment parent = h.getParentContaining(s);
      i = s.reason(parent, inferenceCount, parent.getInferenceNum(s, parent.models(s)));
      if (i != null)
        ++inferenceCount;
    }

    //infer(i);
    System.out.println(i + "\n----------------------------------------------\n");
    if (i.getParent().models(s))
      System.out.println("Instantiating: " + s + "\n");

    return i;
  }

  public void infer(Inference i) {
    if (i instanceof UniversalInstantiation) {
      i.infer(i.getParent());
      inferenceList.add(i);
    } else
      super.infer(i);
  }

  /**
   * Run the prover over the given premises & conclusion
   */
  public void run() {
    printArgument();
    while (!reasoningCompleted()) {
      boolean updated = true;
      runPropositionally();
      if (isInvalid() || openBranches.isEmpty())
        break;

      //printInferences();
      //printInferenceList();

        PriorityQueue<Pair> quantifierQueue = makeQuantifierQueue();
        Inference i;
        while (!(quantifierQueue.isEmpty())) {
          i = instantiateQuantifier(quantifierQueue);
          updated = updated && i != null;
          if (i != null) {
            infer(i);
            /*
            if (i instanceof Decomposition) {
              Decomposition d = (Decomposition) i;
              d.getAdditions().keySet().stream().filter(Sentence::isQuantifier).forEach(s -> {
                Pair p = new Pair(s, d.getParent());
                if (!quantifierQueue.contains(p))
                  quantifierQueue.add(p);
              });
            }
            */
            /*
            else if (i instanceof UniversalInstantiation) {
              UniversalInstantiation ui = (UniversalInstantiation) i;
              ui.getInstances().stream().filter(Sentence::isQuantifier).forEach(s -> {
                Pair p = new Pair(s, ui.getParent());
                if (!quantifierQueue.contains(p))
                  quantifierQueue.add(p);
              });
            }
            */

          }
          printInferences();
        }

        while (updated && !branchQueue.isEmpty())
          addBranches();

        printInferences();
        printInferenceList();

      //if (inferenceCount > 50)
      //  System.exit(1);
    }
    printResult();
  }
}
