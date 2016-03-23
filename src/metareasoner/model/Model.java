package metareasoner.model;

import expression.metasentence.MetaSentence;
import logicalreasoner.truthassignment.TruthValue;

import java.util.HashMap;

/**
 * Created by kevin on 3/22/16.
 */
public class Model {
    private HashMap<MetaSentence, TruthValue> map;

    public Model() {
        map = new HashMap<>();
    }

    public int hashCode() {
        return map.hashCode();
    }

    public boolean equals(Object o) {
        if (o instanceof Model)
            return ((Model)o).map.equals(map);
        return false;
    }

    public String toString() { return map.toString(); }

    /**
     * Add a new mapping to the TruthAssignment
     * @param s the Sentence to be set to true
     */
    public void setTrue(MetaSentence s, int inferenceNum) {
        if (map.keySet().contains(s))
            map.get(s).setTrue(inferenceNum);
        else {
            TruthValue t = new TruthValue();
            t.set(true, inferenceNum);
            map.put(s, t);
        }
    }

    /**
     * Add a new mapping to the TruthAssignment
     * @param s the Sentence to be set to false
     */
    public void setFalse(MetaSentence s, int inferenceNum) {
        if (map.keySet().contains(s))
            map.get(s).setFalse(inferenceNum);
        else {
            TruthValue t = new TruthValue();
            t.set(false, inferenceNum);
            map.put(s, t);
        }
    }

    /**
     * Add a new mapping to the TruthAssignment
     * @param s the Sentence to be set to b
     * @param b the new truth value of s
     */
    public void set(MetaSentence s, boolean b, int inferenceNum) {
        if (map.keySet().contains(s))
            map.get(s).set(b, inferenceNum);
        else {
            TruthValue t = new TruthValue();
            t.set(b, inferenceNum);
            map.put(s, t);
        }
    }
}
