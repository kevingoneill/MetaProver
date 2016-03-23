package logicalreasoner.truthassignment;

import java.util.HashMap;

/**
 * The TruthValue class represents the assignment of a Sentence in
 * a given TruthAssignment. This can be a Set of boolean values, each with
 * an associated number which represents the inference which set this TruthValue.
 */
public class TruthValue {
    private HashMap<Boolean, Integer> vals;
    private boolean isDecomposed;

    public TruthValue() {
        vals = new HashMap<>();
        isDecomposed = false;
    }

    public TruthValue(TruthValue tv) {
        vals = new HashMap<>(tv.vals);
        isDecomposed = false;
    }

    public void setTrue(int stepNum) {
        if (!vals.keySet().contains(true))
            vals.put(true, stepNum);
    }

    public void setFalse(int stepNum) {
        if (!vals.keySet().contains(false))
            vals.put(false, stepNum);
    }

    public void set(Boolean b, int i) {
        if (!vals.keySet().contains(b))
            vals.put(b, i);
    }

    public boolean isConsistent() {
        return vals.size() != 2;
    }

    public void putAll(TruthValue truthValue) {
        truthValue.vals.forEach((k, v) -> {
            if (!vals.containsKey(k))
                vals.put(k, v);
        });
    }

    public boolean isModelled() {
        return vals.keySet().contains(true);
    }

    public void setDecomposed() { isDecomposed = true; }

    public boolean isDecomposed() { return isDecomposed; }

    public String toString() {
        return vals.keySet().toString() + " decomposed: " + isDecomposed;
    }

    public int hashCode() {
        return vals.hashCode();
    }

    public boolean equals(Object o) {
        if (o instanceof TruthValue) {
            TruthValue tv = (TruthValue)o;
            return vals.equals(tv.vals) && isDecomposed == tv.isDecomposed;
        }
        return false;
    }


}
