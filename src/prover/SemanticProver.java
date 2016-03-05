package prover;

import inference.Decomposition;
import inference.Inference;
import sentence.Sentence;
import truthfunction.TruthFunction;

import java.util.Set;

/**
 * Created by kevin on 3/4/16.
 */
public class SemanticProver implements Runnable {

    TruthFunction masterFunction, interestFunction;

    public SemanticProver(Set<Sentence> premises, Set<Sentence> interests) {
        masterFunction = new TruthFunction();
        premises.forEach(masterFunction::setTrue);

        interestFunction = new TruthFunction();
        interests.forEach(interestFunction::setTrue);
    }

    public boolean reasonForwards() {
        TruthFunction discoveries = new TruthFunction();
        masterFunction.keySet().parallelStream().filter(s -> !masterFunction.isDecomposed(s)).forEach(s -> {
            Inference i = s.reasonForwards(masterFunction);

            if (i instanceof Decomposition)
                i.infer(discoveries);
        });

        System.out.println("Discoveries: " + discoveries);

        if (discoveries.isEmpty() || masterFunction.containsAll(discoveries))
            return false;

        masterFunction.merge(discoveries);
        return true;
    }

    public boolean reasonBackwards() {
        TruthFunction discoveries = new TruthFunction();
        masterFunction.keySet().parallelStream().forEach(s -> {
            Inference i = s.reasonBackwards(interestFunction);

            if (i instanceof Decomposition)
                i.infer(discoveries);
        });



        if (discoveries.isEmpty() || interestFunction.containsAll(discoveries))
            return false;
        interestFunction.merge(discoveries);
        return true;
    }

    public void run() {
        printInferences();
        boolean updated = true;

        while (updated && !interestsMet()) {
            updated = updated && (reasonForwards()); // || reasonBackwards());

            printInferences();

        }
    }

    public boolean interestsMet() {
        if (!masterFunction.isConsistent())
            return false;

        return interestFunction.keySet().parallelStream().allMatch(s -> masterFunction.isMapped(s) && masterFunction.models(s));
    }

    public void printInferences() {
        System.out.println("\nForwardsInferences: ");
        masterFunction.keySet().forEach(s -> System.out.println(s + ":\t" + masterFunction.models(s)));

        System.out.println("\nBackwards Inferences: ");
        interestFunction.keySet().forEach(s -> System.out.println(s + ":\t" + interestFunction.models(s)));
    }
}
