package truthfunction;

import sentence.Not;
import sentence.Sentence;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Created by kevin on 3/2/16.
 */
public class TruthFunction {
    private Map<Sentence, Boolean> map;     // The explicit Sentence -> Boolean mapping

    private Map<Sentence, Boolean> decomposed;


    TruthFunction parent;

    Set<TruthFunction> children;

    /**
     * Create a new, empty TruthFunction
     */
    public TruthFunction() {
        map = new HashMap<>();
        decomposed = new HashMap<>();
        parent = null;
        children = new HashSet<>();
    }

    /**
     * Create a copy of another TruthFunction, th
     * @param tf the TruthFunction to copy
     */
    public TruthFunction(TruthFunction tf) {
        this.map = new HashMap<>(tf.map);
        this.decomposed = new HashMap<>(tf.decomposed);
        this.parent = tf.parent;
        children = new HashSet<>(tf.children);
    }

    /**
     * Create a TruthFunction with the given mapping
     * @param tf the mapping
     */
    public TruthFunction(HashMap<Sentence, Boolean> tf) {
        this.map = new HashMap<>(tf);
        decomposed = new HashMap<>();
        parent = null;
        children = new HashSet<>();
    }


    public int size() { return map.size(); }

    public boolean isEmpty() { return map.isEmpty(); }

    public int hashCode() { return map.hashCode(); }

    public boolean equals(Object o) {
        if (o instanceof TruthFunction) {
            return map.equals(((TruthFunction)o).map);
        }
        return false;
    }

    public String toString() {
        return map.toString();
    }

    /**
     * Add a new mapping to the TruthFunction
     * @param s the Sentence to be set to true
     */
    public void setTrue(Sentence s) {
        map.put(s, true);
    }

    /**
     * Add a new mapping to the TruthFunction
     * @param s the Sentence to be set to false
     */
    public void setFalse(Sentence s) {
        map.put(s, false);
    }

    /**
     * Add a new mapping to the TruthFunction
     * @param s the Sentence to be set to b
     * @param b the new truth value of s
     */
    public void set(Sentence s, boolean b) { map.put(s, b); }

    /**
     * Return true if this |= s
     * @param s the sentence to be tested for
     * @return true if this |= s, false if this |= (not s),
     * null if not found
     */
    public Boolean models(Sentence s) {
        return map.get(s);
    }

    /**
     * Return true if this has a mapping for s
     * @param s the sentence to search for
     * @return true if s is mapped to some value,
     *  false otherwise
     */
    public boolean isMapped(Sentence s) {
        return map.containsKey(s);
    }

    /**
     * @return all Sentences mapped in this
     */
    public Set<Sentence> keySet() { return map.keySet(); }

    /**
     * Get a Stream of all true Sentences in this
     * @return a Stream of all true Sentences in this
     */
    public Stream<Sentence> getTrueSentences() {
        return map.keySet().stream().filter(this::models);
    }

    /**
     * Get a Stream of all false Sentences in this
     * @return a Stream of all false Sentences in this
     */
    public Stream<Sentence> getFalseSentences() {
        return map.keySet().stream().filter(sentence -> !models(sentence));
    }

    public void merge(TruthFunction h) {
        if (h != null)
            map.putAll(h.map);
    }

    public boolean isConsistent() {
        return map.keySet().stream().allMatch(s -> !map.keySet().contains(new Not(s)));
    }

    public boolean containsAll(TruthFunction h) {
        return h.keySet().stream().allMatch(s -> isMapped(s) && h.models(s).equals(models(s)));
    }

    public void setDecomposed(Sentence s) {
        decomposed.put(s, true);
    }

    public Boolean isDecomposed(Sentence s) {
        return decomposed.get(s) != null && decomposed.get(s);
    }
}
