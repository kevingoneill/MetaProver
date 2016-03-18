package logicalreasoner.truthfunction;

import java.util.HashMap;

/**
 * The TruthValue class represents the assignment of a Sentence in
 * a given TruthAssignment. This can be a Set of boolean values, each with
 * an associated number which represents the inference which set this TruthValue.
 */
class TruthValue {
    private HashMap<Boolean, Integer> vals;
    private boolean isDecomposed;

    protected TruthValue() {
        vals = new HashMap<>();
        isDecomposed = false;
    }

    protected TruthValue(TruthValue tv) {
        vals = new HashMap<>(tv.vals);
        isDecomposed = false;
    }

    protected void setTrue(int stepNum) {
        if (!vals.keySet().contains(true))
            vals.put(true, stepNum);
    }

    protected void setFalse(int stepNum) {
        if (!vals.keySet().contains(false))
            vals.put(false, stepNum);
    }

    protected void set(Boolean b, int i) {
        if (!vals.keySet().contains(b))
            vals.put(b, i);
    }

    protected boolean isConsistent() {
        return vals.size() != 2;
    }

    protected void putAll(TruthValue truthValue) {
        truthValue.vals.forEach((k, v) -> {
            if (!vals.containsKey(k))
                vals.put(k, v);
        });
    }

    protected boolean isModelled() {
        return vals.keySet().contains(true);
    }

    protected void setDecomposed() { isDecomposed = true; }

    protected boolean isDecomposed() { return isDecomposed; }

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
