package metareasoner.proof;

import expression.metasentence.MetaSentence;
import metareasoner.metainference.MetaInference;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The Proof class represents the state of a meta-logical proof,
 * with both forwards and backwards reasoning
 */
public class Proof {

    private ArrayList<Step> forwardsInferences, backwardsInferences;

    public Proof(ArrayList<MetaSentence> inferences, ArrayList<MetaSentence> interests) {
        forwardsInferences = new ArrayList<>();
        backwardsInferences = new ArrayList<>();
        inferences.forEach(i -> forwardsInferences.add(new Step(i, null)));
        interests.forEach(i -> backwardsInferences.add(new Step(i, null)));
    }

    public int hashCode() {
        return forwardsInferences.hashCode();
    }

    public boolean equals(Object o) {
        if (o instanceof Proof)
            return ((Proof)o).forwardsInferences.equals(forwardsInferences) && ((Proof)o).backwardsInferences.equals(backwardsInferences);
        return false;
    }

    public String toString() { return forwardsInferences.toString(); }

    public void printInferences() {
        forwardsInferences.forEach(i -> System.out.println(i.getMetaSentence().toSymbol()));
    }

    public void printInterests() {
        backwardsInferences.forEach(i -> System.out.println(i.getMetaSentence().toSymbol()));
    }

    public void addForwardsInference(MetaSentence s, MetaInference i) {
        System.out.println(s);
        ArrayList<Step> inferences = new ArrayList<>();
        forwardsInferences.forEach(inference -> {
            if (inference.getMetaSentence().equals(i.getOrigin())) {
                inference.setDecomposed();
                inferences.add(new Step(s, i));
            }
        });
        forwardsInferences.addAll(inferences);
    }

    /**
     * Add a backwards Step consisting of MetaSentence s and MetaInference i
     * @param s
     * @param i
     */
    public void addBackwardsInference(MetaSentence s, MetaInference i) {
        ArrayList<Step> inferences = new ArrayList<>();
        backwardsInferences.forEach(inference -> {
            if (inference.getMetaSentence().equals(i.getOrigin())) {
                inference.setDecomposed();
                inferences.add(new Step(s, i));
            }
        });
        backwardsInferences.addAll(inferences);
    }

    public List<MetaSentence> getNewForwardSentences() {
        return forwardsInferences.stream().filter(i -> !i.isDecomposed()).map(Step::getMetaSentence).collect(Collectors.toList());
    }

    public List<MetaSentence> getNewBackwardSentences() {
        return backwardsInferences.stream().filter(i -> !i.isDecomposed()).map(Step::getMetaSentence).collect(Collectors.toList());
    }

    public boolean isDecomposedForwards(MetaSentence s) {
        return forwardsInferences.stream().anyMatch(i -> i.getMetaSentence().equals(s) && i.isDecomposed());
    }

    public boolean isDecomposedBackwards(MetaSentence s) {
        return backwardsInferences.stream().anyMatch(i -> i.getMetaSentence().equals(s) && i.isDecomposed());
    }

    public boolean decomposedAll() {
        return forwardsInferences.stream().allMatch(Step::isDecomposed) &&
                backwardsInferences.stream().allMatch(Step::isDecomposed);
    }

    public boolean isComplete() {
        return forwardsInferences.stream().anyMatch(i -> backwardsInferences.contains(i));
    }
}
