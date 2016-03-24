package metareasoner;

import expression.metasentence.MetaSentence;
import metareasoner.metainference.MetaInference;
import metareasoner.proof.Proof;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 *  The MetaProver class uses meta-reasoning to provide meta-proofs
 *  for valid arguments
 */
public class MetaProver implements Runnable {

    private Proof proof;
    private ArrayList<MetaSentence> premises;
    private MetaSentence interest;
    int inferenceCount;

    public MetaProver(ArrayList<MetaSentence> p, MetaSentence i) {
        premises = p;
        interest = i;
        proof = new Proof(p, i);
        inferenceCount = 1;
    }

    public boolean reasonForwards(Proof p) {
        List<MetaInference> inferences = p.getNewForwardSentences().stream()
                .map(s -> s.reasonForwards(p, inferenceCount++)).filter(i -> i != null)
                .collect(Collectors.toList());

        inferences.forEach(i -> i.infer(p, true));

        return !inferences.isEmpty();
    }

    /**
     * Reason over the TruthAssignment h by decomposing statements
     * @param h the TruthAssignment to reason over
     * @return true if changes to h have been made as a result of this call, false otherwise
     */
    public boolean reasonBackwards(Proof p) {
        List<MetaInference> inferences = p.getNewBackwardSentences().stream()
                .map(s -> s.reasonBackwards(p, inferenceCount++)).filter(i -> i != null)
                .collect(Collectors.toList());

        inferences.forEach(i -> i.infer(p, false));
        return !inferences.isEmpty();
    }

    public void run() {
        System.out.println("Inferences: ");
        proof.printInferences();

        System.out.println("Interests: ");
        proof.printInterests();


        while(!reasoningCompleted()) {
            if (!reasonForwards(proof) && !reasonBackwards(proof))
                break;

            System.out.println("\nInferences: ");
            proof.printInferences();

            System.out.println("\nInterests: ");
            proof.printInterests();
        }


    }


    /**
     * Check if all TruthAssignments are consistent and fully decomposed.
     * @return true if all open branches are fully decomposed
     */
    private boolean reasoningCompleted() {
        System.out.println("Complete: " + proof.isComplete());
        System.out.println("Decomposed: " + proof.decomposedAll());
        return proof.isComplete() || proof.decomposedAll();
    }
}
