package metareasoner.proof;

import expression.metasentence.MetaSentence;
import metareasoner.metainference.MetaInference;

/**
 * The Step class represents the state of a proof step
 * for a given MetaSentence in the proof
 */
public class Step {
    private MetaSentence metaSentence;
    private MetaInference justification;
    private boolean isDecomposed;

    protected Step(MetaSentence s, MetaInference i) {
        metaSentence = s;
        justification = i;
        isDecomposed = false;
    }

    public MetaSentence getMetaSentence() { return metaSentence; }

    public MetaInference getJustification() {
        return justification;
    }

    public void setDecomposed() {
        isDecomposed = true;
    }

    public boolean isDecomposed() {
        return isDecomposed;
    }

    public int hashCode() {
        if (justification != null)
            return justification.hashCode();
        return 0;
    }

    public boolean equals(Object o) {
        if (o instanceof Step) {
            Step s = (Step)o;
            if (justification == null)
                return isDecomposed == s.isDecomposed && metaSentence.equals(s.metaSentence) && s.justification == null;
            return isDecomposed == s.isDecomposed && metaSentence.equals(s.metaSentence) && s.justification.equals(justification);
        }
        return false;
    }
}
