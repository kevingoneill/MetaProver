package logicalreasoner.prover;

import logicalreasoner.inference.Branch;
import logicalreasoner.inference.Decomposition;
import logicalreasoner.inference.Inference;
import logicalreasoner.sentence.Sentence;
import logicalreasoner.truthfunction.TruthAssignment;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * The SemanticProver class represents a logical reasoner
 * which determines the validity of arguments by
 * generating a TruthAssignment Tree (like a Truth Tree,
 * except that mappings from Sentences to their values are
 * stored in each node, not only the Sentences themselves).
 */
public class SemanticProver implements Runnable {

    //Stores the initial/root TruthAssignment
    TruthAssignment masterFunction;

    //The leaves of the TruthAssignment tree
    Set<TruthAssignment> openBranches;

    //All statements which can be branched upon (in descending order of size)
    PriorityQueue<Branch> branchQueue;


    /**
     * Initialize the reasoner with the premises and the negation of all interests
     * @param premises the prior knowledge of the prover
     * @param interests the interests of the prover (to be negated)
     */
    public SemanticProver(Set<Sentence> premises, Set<Sentence> interests) {
        masterFunction = new TruthAssignment();
        premises.forEach(masterFunction::setTrue);
        interests.forEach(masterFunction::setFalse);

        openBranches = new HashSet<TruthAssignment>() {{ add(masterFunction); }};

        branchQueue = new PriorityQueue<>((b1, b2) -> {
            if (b1.size() != b2.size())
                return b1.size() > b2.size() ? 1 : 0;
            return b1.getOrigin().size() < b2.getOrigin().size() ? 1 : 0;
        });
    }

    /**
     * Reason over the TruthAssignment h by decomposing statements
     * @param h the TruthAssignment to reason over
     * @return true if changes to h have been made as a result of this call, false otherwise
     */
    public boolean reason(TruthAssignment h) {
        TruthAssignment discoveries = new TruthAssignment();
        h.getSentencesUpwards().stream().filter(s -> !h.isDecomposed(s)).forEach(s -> {
            Inference i = s.reason(h);

            if (i instanceof Decomposition) {
                i.infer(discoveries);
            }
            else if (i instanceof Branch) {
                branchQueue.offer((Branch)i);
            }
        });

        //System.out.println("Discoveries: " + discoveries);

        if (discoveries.isEmpty() || h.containsAll(discoveries))
            return false;
        h.merge(discoveries);
        return true;
    }

    /**
     * Run the prover over the given premises & conclusion
     */
    public void run() {
        printInferences();

        while (!reasoningCompleted()) {
            boolean updated = true;

            // Always decompose all statements before branching
            while (updated) {
                updated = openBranches.stream().anyMatch(this::reason);
            }

            //Branch once on the largest branching statement then loop back around
            if (!branchQueue.isEmpty())
                addBranches();

            printInferences();
        }
        printInferences();

        //If the tree has been completely decomposed
        //without inconsistencies, the argument is invalid
        if (isConsistent()) {
            System.out.println("\nThe argument is NOT valid.\n");
        } else {
            System.out.println("\nThe argument IS valid.\n");
        }

        masterFunction.print();
        //System.out.println(masterFunction.getChildren());
    }

    /**
     * Check if the TruthAssignment is fully decomposed.
     * @return true if all statements are decomposed
     */
    public boolean reasoningCompleted(TruthAssignment h) {
        //System.out.println("DecomposedAll: " + h.decomposedAll());

        return h.decomposedAll() && branchQueue.isEmpty();
    }

    /**
     * Check if all TruthAssignments are consistent and fully decomposed.
     * @return true if all open branches are fully decomposed
     */
    public boolean reasoningCompleted() {
        return openBranches.stream().allMatch(this::reasoningCompleted);
    }

    public boolean isConsistent() {
        //System.out.println("Consistent: " + h.isConsistent());
        return openBranches.stream().allMatch(TruthAssignment::isConsistent);
    }

    /**
     * Print all inferences in the TruthAssignment
     */
    public void printInferences() {
        System.out.println("\nTree: ");
        masterFunction.print();
        System.out.println("Decomposed: ");
        masterFunction.printDecomposed();
    }

    /**
     * Print the Set of open Branches
     */
    public void printBranches() {
        System.out.println("Branches: ");
        openBranches.forEach(System.out::println);
        System.out.println();
    }

    /**
     * Branch the Sentence on the top of the branchQueue
     * and update the openBranches Set to contain those children.
     */
    public void addBranches() {
        Branch b = branchQueue.poll();
        System.out.println("Branching from: " + b.getOrigin());
        System.out.println("Parent: " + b.getParent());
        System.out.println("Leaves: " + b.getParent().getLeaves());

        b.getParent().setDecomposed(b.getOrigin());
        b.getParent().getLeaves().stream().filter(TruthAssignment::isConsistent).forEach(leaf -> {
            b.infer(leaf);
            openBranches.remove(leaf);
        });
        openBranches.addAll(b.getBranches());
    }
}
