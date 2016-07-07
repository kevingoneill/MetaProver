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
import java.util.stream.Stream;

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

  /**
   * Initialize the reasoner with the premises and the negation of all interests
   *
   * @param premises the prior knowledge of the prover
   * @param interest the interest of the prover (to be negated)
   * @param print    Print log output if true
   * @param runTime  The maximum runTime which this prover should run on
   */
  public FirstOrderProver(Set<Sentence> premises, Sentence interest, boolean print, int runTime) {
    super(premises, interest, print, runTime);
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
            h.flattenUndecomposedParallel().filter(p -> p.sentence.isQuantifier()).forEachOrdered(e -> {
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
    //System.out.println("instantiateQuantifier");
    //System.out.println("################################################\n");
    //printQueue(quantifierQueue);
    Inference i = null;
    Pair p = null;

    while (i == null) {
      if (quantifierQueue.isEmpty()) {
        //printInferences();
        //printInferenceList();
        //System.exit(1);

        return null;
      }

      p = quantifierQueue.poll();

      if (p.sentence == null)
        return null;
      if (p.truthAssignment.models(p.sentence) && p.sentence instanceof ForAll) {
        Constant c = Constant.getNewUniqueConstant();
        p.truthAssignment.getLeavesParallel().forEach(l -> {
          if (l.getConstants().isEmpty())
            l.addConstant(c);
        });
      }

      i = p.sentence.reason(p.truthAssignment, inferenceCount,
              p.truthAssignment.getInferenceNum(p.sentence, p.truthAssignment.models(p.sentence)));

      if (i == null) {
        System.out.println(p.sentence);
        System.out.println(p.truthAssignment.getConstants());
        System.out.println(p.truthAssignment.getTruthValue(p.sentence).getUninstantiatedConstants());
        System.out.println(p.truthAssignment.getTruthValue(p.sentence).getInstantiatedConstants());
        System.exit(1);
      }

      if (i != null) {
        ++inferenceCount;
        //System.out.println(i + "\n----------------------------------------------\n");
        //if (p.truthAssignment.models(p.sentence))
        //  System.out.println("Instantiating: " + p.sentence + "\n");
      }
    }

    return i;
  }

  public Stream<Pair> infer(Inference i) {
    if (i instanceof UniversalInstantiation) {
      inferenceList.add(i);
      return i.infer(i.getParent());
    } else
      return super.infer(i);
  }

  /**
   * Run the prover over the given premises & conclusion
   */
  public void run() {
    startTime = System.currentTimeMillis();
    printArgument();
    while (!reasoningCompleted()) {
      boolean updated = true;
      runPropositionally();

      if (maxRuntime != null && (System.currentTimeMillis() - startTime) >= maxRuntime)
        return;

      if (isInvalid() || openBranches.isEmpty()) {
        break;
      }

      //printInferences();
      //printInferenceList();

      PriorityQueue<Pair> quantifierQueue = makeQuantifierQueue();
      Inference i;
      while (!(quantifierQueue.isEmpty())) {
        i = instantiateQuantifier(quantifierQueue);
        updated = updated && i != null;
        if (i != null) {
          infer(i).filter(p -> p.sentence.isQuantifier()).forEach(p -> {
            if (!quantifierQueue.contains(p))
              quantifierQueue.add(p);
          });

          //printInferences();
          //printInferenceList();
        }
      }

      while (updated && !branchQueue.isEmpty())
        addBranches();

      //printInferences();
      //printInferenceList();
    }
    finishedProof = true;
    printResult();
  }
}
