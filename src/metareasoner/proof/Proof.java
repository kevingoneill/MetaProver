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

    private ArrayList<MetaSentence> premises;
    private MetaSentence ultimateInterest;
    private ArrayList<Step> forwardsInferences, backwardsInferences;

    public Proof(ArrayList<MetaSentence> inferences, MetaSentence interest) {
        forwardsInferences = new ArrayList<>();
        backwardsInferences = new ArrayList<>();
        inferences.forEach(i -> forwardsInferences.add(new Step(i, null, null, true)));
        backwardsInferences.add(new Step(interest, null, null, false));
        premises = inferences;
        ultimateInterest = interest;
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
        forwardsInferences.forEach(i -> System.out.println(i.getMetaSentence().toSymbol() + "\t" + i.getJustification()));
    }

    public void printInterests() {
        backwardsInferences.forEach(i -> System.out.println(i.getMetaSentence().toSymbol() + "\t" + i.getJustification()));
    }

    public void addForwardsInference(MetaSentence s, MetaInference i) {
        Step step = find(forwardsInferences, i.getOrigin());
        if (step != null) {
            step.setDecomposed();
            forwardsInferences.add(new Step(s, i, step, true));
        }
    }

    /**
     * Add a backwards Step consisting of MetaSentence s and MetaInference i
     * @param s
     * @param i
     */
    public void addBackwardsInference(MetaSentence s, MetaInference i) {
        Step step = find(backwardsInferences, i.getOrigin());
        if (step != null) {
            step.setDecomposed();
            step.setJustification(i);
            backwardsInferences.add(new Step(s, null, step, false));
        }
    }

    private int indexOf(ArrayList<Step> a, MetaSentence s) {
        for (int i = 0; i < a.size(); ++i)
            if (a.get(i).getMetaSentence().equals(s))
                return i;
        return -1;
    }

    private Step find(ArrayList<Step> a, MetaSentence s) {
        for (int i = 0; i < a.size(); ++i)
            if (a.get(i).getMetaSentence().equals(s))
                return a.get(i);
        return null;
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
        Step s = find(backwardsInferences, ultimateInterest);
        return s.getLeaves().stream().allMatch(l ->
                forwardsInferences.stream().anyMatch(f ->
                    f.getMetaSentence().equals(l.getMetaSentence())));
    }

    public void printProof() {
        if (!isComplete()) {
            System.out.println("NO PROOF FOUND");
            return;
        }

        ArrayList<Step> proof = generateProof();
        for (int i = 0; i < proof.size(); ++i) {
            Step s = proof.get(i);
            MetaInference justification = s.getJustification();

            System.out.print(i + ".  " + s.getMetaSentence().toSymbol() + "\t\t\t" + s.getReason() + " ");

            if (s.isForwards() && justification != null)
                    System.out.println(indexOf(proof, s.getJustification().getOrigin()));
             else {
                if (s.getChildren().size() > 0) {
                    System.out.print(indexOf(proof, s.getChildren().get(0).getMetaSentence()));
                    for (int j = 1; j < s.getChildren().size(); ++j)
                        System.out.print(", " + indexOf(proof, s.getChildren().get(j).getMetaSentence()));
                    System.out.println();
                }
            }
        }
    }

    public ArrayList<Step> generateProof() {
        return getProofBackwards(find(backwardsInferences, ultimateInterest));
    }

    private ArrayList<Step> getProofBackwards(Step s) {
        ArrayList<Step> a = new ArrayList<>();

        if (s.getChildren().isEmpty()) {
            Step unifier = find(forwardsInferences, s.getMetaSentence());
            if (unifier != null) {
                getProofForwards(unifier).forEach(step -> {
                    if (!a.contains(step))
                        a.add(step);
                });
            }
        } else {
            for (Step child : s.getChildren()) {
                getProofBackwards(child).forEach(step -> {
                    if (!a.contains(step))
                        a.add(step);
                });
            }
            a.add(s);
        }

        return a;
    }

    private ArrayList<Step> getProofForwards(Step s) {
        ArrayList<Step> a = new ArrayList<>();
        if (s.getParent() != null)
            getProofForwards(s.getParent()).forEach(step -> {
                if (!a.contains(step))
                    a.add(step);
            });
        if (!a.contains(s))
           a.add(s);
        return a;
    }
}
