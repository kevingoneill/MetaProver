package logicalreasoner.prover;

import expression.Sort;
import expression.sentence.Constant;
import expression.sentence.Exists;
import expression.sentence.ForAll;
import expression.sentence.Sentence;
import logicalreasoner.inference.Inference;
import logicalreasoner.inference.UniversalInstantiation;
import logicalreasoner.truthassignment.Pair;
import logicalreasoner.truthassignment.TruthAssignment;
import logicalreasoner.truthassignment.TruthValue;

import java.util.Collections;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The FOLProver class provides the same functionality of
 * its Prover superclass, but extends to implement
 * reasoning over first order logic.
 */
public class FOLProver extends Prover {

  /**
   * Initialize the reasoner with the premises and the negation of all goals
   *
   * @param premises the prior knowledge of the prover
   * @param goal the goal of the prover (to be negated)
   * @param print    Print log output if true
   */
  public FOLProver(Set<Sentence> premises, Sentence goal, boolean print) {
    super(premises, goal, print);
  }

  public FOLProver(TruthAssignment truthAssignment) {
    super(truthAssignment);
  }

  /**
   * Initialize the reasoner with the premises and the negation of all goals
   *
   * @param premises the prior knowledge of the prover
   * @param goal the goal of the prover (to be negated)
   * @param print    Print log output if true
   * @param runTime  The maximum runTime which this prover should run on
   */
  public FOLProver(Set<Sentence> premises, Sentence goal, boolean print, int runTime) {
    super(premises, goal, print, runTime);
  }

  public FOLProver(Set<Sentence> sentences, boolean b) {
    this(sentences, Collections.emptySet(), b);
  }

  public FOLProver(Set<Sentence> premises, Set<Sentence> goals, boolean print) {
    super(premises, goals, print);
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

    openBranches.stream().flatMap(TruthAssignment::flattenUndecomposedSerial)
            .filter(p -> p.sentence.isQuantifier()).forEach(quantifierQueue::add);
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
    Pair p;
    TruthAssignment h;
    Sentence s;

    while (i == null) {
      if (quantifierQueue.isEmpty()) {
        return null;
      }

      p = quantifierQueue.poll();
      h = p.truthAssignment;
      s = p.sentence;
      if (h.isDecomposed(s))
        continue;

      if (s == null)
        return null;
      if (h.models(s) && s instanceof ForAll) {
        TruthValue v = h.getTruthValue(s);
        Sort sort = ((ForAll) s).getVariable().getSort();

        if ((v.getUninstantiatedConstants().isEmpty() && v.getInstantiatedConstants().isEmpty()) ||
                h.getLeaves().anyMatch(l -> l.getConstants(sort).isEmpty())) {
          Constant c = Constant.getNewUniqueConstant(sort);
          h.getLeavesParallel().forEach(l -> {
            if (l.getConstants(sort).isEmpty())
              l.addConstant(c);
          });
        }
      }

      i = s.reason(h, inferenceCount++,
              h.getInferenceNum(s, h.models(s)));

      if (i == null) {
        System.out.println(s);
        System.out.println(h.getConstants().stream().map(c -> c.getSort() + " " + c.getName()).collect(Collectors.toList()));
        System.out.println(h.getTruthValue(s).getUninstantiatedConstants());
        System.out.println(h.getTruthValue(s).getInstantiatedConstants());
        System.out.println(h.isDecomposed(s));
        System.exit(1);
      }
    }

    return i;
  }

  public Stream<Pair> infer(Inference i) {
    if (i instanceof UniversalInstantiation) {
      inferenceList.add(i);
      return i.infer(i.getParent());
    } else {
      return super.infer(i);
    }
  }

  /**
   * Run the prover over the given premises & conclusion
   */
  public void run() {
    startTime = System.currentTimeMillis();
    printArgument();
    while (!reasoningCompleted()) {
      boolean updated = false;
      runPropositionally();

      if (maxRuntime != null && (System.currentTimeMillis() - startTime) >= maxRuntime)
        return;
      if (isInvalid() || openBranches.isEmpty())
        break;

      PriorityQueue<Pair> quantifierQueue = makeQuantifierQueue();
      Inference i;
      while (!(quantifierQueue.isEmpty())) {
        i = instantiateQuantifier(quantifierQueue);
        updated = updated || i != null;
        if (i != null) {
          infer(i).filter(p -> p.sentence.isQuantifier()).forEach(p -> {
            if (!quantifierQueue.contains(p))
              quantifierQueue.add(p);
          });
        }
      }

      while (updated && !branchQueue.isEmpty())
        addBranches();

      runPropositionally();


      //printInferences();
      //printInferenceList();
    }
    finishedProof = true;
    printResult();
  }

  private boolean isExistentialQuantifier(Pair p) {
    return p.sentence instanceof Exists;
  }
}
