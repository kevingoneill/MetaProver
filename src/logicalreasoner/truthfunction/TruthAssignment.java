package logicalreasoner.truthfunction;

import logicalreasoner.sentence.Atom;
import logicalreasoner.sentence.Not;
import logicalreasoner.sentence.Sentence;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The TruthAssignment class represents a function
 * mapping of logical Sentences to their values in some
 * logically plausible universe; the TruthAssignment is
 * a logical model of the world
 */
public class TruthAssignment {
    private Map<Sentence, Set<Boolean>> map;     // The explicit Sentence -> Boolean mapping

    private Map<Sentence, Boolean> decomposed;

    private TruthAssignment parent;

    private List<TruthAssignment> children;

    /**
     * Create a new, empty TruthAssignment
     */
    public TruthAssignment() {
        map = new HashMap<>();
        decomposed = new HashMap<>();
        parent = null;
        children = new ArrayList<>();
    }

    /**
     * Create a copy of another TruthAssignment, th
     * @param ta the TruthAssignment to copy
     */
    public TruthAssignment(TruthAssignment ta) {
        this.map = new HashMap<>(ta.map);
        this.decomposed = new HashMap<>(ta.decomposed);
        this.parent = ta.parent;
        children = new ArrayList<>(ta.children);
    }

    /**
     * Create a TruthAssignment with the given mapping
     * @param ta the mapping
     */
    public TruthAssignment(HashMap<Sentence, Set<Boolean>> ta) {
        this.map = new HashMap<>(ta);
        decomposed = new HashMap<>();
        parent = null;
        children = new ArrayList<>();
    }


    public int size() { return map.size(); }

    public boolean isEmpty() { return map.isEmpty(); }

    public int hashCode() { return map.hashCode(); }

    public boolean equals(Object o) {
        if (o instanceof TruthAssignment) {
            TruthAssignment h = (TruthAssignment)o;

            if (parent == null)
                return map.equals(h.map) && h.parent == null && children.equals(h.children);
            return map.equals(h.map) && parent.equals(h.parent) && children.equals(h.children);
        }
        return false;
    }

    public String toString() {
        return map.toString();
    }

    public void print() {
        print("", true);
    }

    private void print(String prefix, boolean isTail) {
        System.out.println(prefix + (isTail ? "└── " : "├── ") + toString() + (isConsistent() ? " ✓" : " ✗"));
        Iterator<TruthAssignment> itr = children.iterator();
        int i = 0;

        while (i < children.size() - 1 && itr.hasNext()) {
            itr.next().print(prefix + (isTail ? "    " : "│   "), false);
            ++i;
        }
        if (itr.hasNext()) {
            itr.next().print(prefix + (isTail ?"    " : "│   "), true);
        }
    }

    public void printDecomposed() {
        printDecomposed("", true);
    }

    private void printDecomposed(String prefix, boolean isTail) {
        System.out.println(prefix + (isTail ? "└── " : "├── ") + decomposed.toString() + (decomposedAll() ? " ✓" : " ✗"));
        Iterator<TruthAssignment> itr = children.iterator();
        int i = 0;
        while (i < children.size() - 1 && itr.hasNext()) {
            itr.next().printDecomposed(prefix + (isTail ? "    " : "│   "), false);
            ++i;
        }
        if (itr.hasNext()) {
            itr.next().printDecomposed(prefix + (isTail ?"    " : "│   "), true);
        }
    }

    public TruthAssignment getParent() { return parent; }

    public void setParent(TruthAssignment h) { parent = h; }


    /**
     * Add a new mapping to the TruthAssignment
     * @param s the Sentence to be set to true
     */
    public void setTrue(Sentence s) {
        if (map.keySet().contains(s))
            map.get(s).add(true);
        else
            map.put(s, new HashSet<Boolean>() {{ add(true); }});

        if (s instanceof Atom)
            setDecomposed(s);
    }

    /**
     * Add a new mapping to the TruthAssignment
     * @param s the Sentence to be set to false
     */
    public void setFalse(Sentence s) {
        if (map.keySet().contains(s))
            map.get(s).add(false);
        else
            map.put(s, new HashSet<Boolean>() {{ add(false); }});

        if (s instanceof Atom)
            setDecomposed(s);
    }

    /**
     * Add a new mapping to the TruthAssignment
     * @param s the Sentence to be set to b
     * @param b the new truth value of s
     */
    public void set(Sentence s, boolean b) {
        if (map.keySet().contains(s))
            map.get(s).add(b);
        else
            map.put(s, new HashSet<Boolean>() {{ add(b); }});

        if (s instanceof Atom)
            setDecomposed(s);
    }

    /**
     * Return true if this |= s (or a parent of this |= s)
     * @param s the sentence to be tested for
     * @return true if this |= s, false if this |= (not s),
     * null if not found
     */
    public Boolean models(Sentence s) {
        if (map.containsKey(s))
            return map.get(s).contains(true);
        if (parent != null)
            return parent.models(s);
        return false;
    }

    /**
     * Return true if this or a parent of this has a mapping for s
     * @param s the sentence to search for
     * @return true if s is mapped to some value,
     *  false otherwise
     */
    public boolean isMapped(Sentence s) {
        return map.containsKey(s) || parent != null && parent.isMapped(s);
    }

    /**
     * @return all Sentences mapped in this TruthAssignment only
     */
    public Set<Sentence> keySet() { return map.keySet(); }

    /**
     * Recursively collect the mappings of this TruthAssignment and
     * all of its children
     *
     * @return a set of all inferences under this TruthAssignment
     */
    public Set<Sentence> getSentencesDownwards() {
        if (children.isEmpty())
            return map.keySet();

        Set<Sentence> set = new HashSet<>(map.keySet());
        set.addAll(children.stream().flatMap(s -> s.getSentencesDownwards().stream())
                .collect(Collectors.toSet()));
        return set;
    }

    /**
     * Recursively collect the mappings of this
     * TruthAssignment and its parents
     *
     * @return a set of all inferences under this TruthAssignment
     */
    public Set<Sentence> getSentencesUpwards() {
        if (parent == null)
            return map.keySet();

        return new HashSet<Sentence>() {{
            addAll(map.keySet());
            addAll(parent.getSentencesUpwards());
        }};
    }

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

    /**
     * Add all of the mappings of h into this TruthAssignment
     * @param h the mappings to add to this
     */
    public void merge(TruthAssignment h) {
        h.keySet().forEach(s -> {
            if (!map.keySet().contains(s))
                map.put(s, h.map.get(s));
            else {
                map.get(s).addAll(h.map.get(s));
            }
        });
        decomposed.putAll(h.decomposed);
    }

    /**
     * Make sure the mappings of Sentences is the same as their evaluation
     * under this TruthAssignment
     * @return true if all mappings are consistent, false otherwise
     */
    public boolean isConsistent() {
        return map.keySet().stream().allMatch(s -> {
            if (map.get(s).size() > 1)
                return false;
            if (parent != null && parent.isMapped(s)) {
                return parent.models(s).equals(models(s));
            }
            Sentence neg = new Not(s);
            return !isMapped(neg) || models(neg) != models(s);
        }) && children.stream().allMatch(TruthAssignment::isConsistent);
    }

    /**
     * Check if s is mapped to b in this or a parent of this
     * @param s the Sentence to search for
     * @param b the mapping of the Sentence to search for
     * @return true if this or a parent of this contains this mapping,
     *  false otherwise.
     */
    public boolean contains(Sentence s, Boolean b) {
        return isMapped(s) && models(s).equals(b);
    }

    /**
     * Check if the mappings in h are redundant under this
     * @param h the possible subset of this
     * @return true if this contains every mapping in h
     */
    public boolean containsAll(TruthAssignment h) {
        return h.keySet().stream().allMatch(s -> contains(s, h.models(s)));
    }

    /**
     * Mark this sentence as having been decomposed under this TruthAssignment
     * @param s the sentence which has been decomposed
     */
    public void setDecomposed(Sentence s) {
        decomposed.put(s, true);
    }

    /**
     * Check if this Sentence has already been reasoned upon.
     * @param s the Sentence to check
     * @return true if s has been decomposed in this or a child of this,
     *  false otherwise
     */
    public Boolean isDecomposed(Sentence s) {
        if (decomposed.get(s) != null)
            return decomposed.get(s);
        if (parent != null)
            return parent.isDecomposed(s);
        return false;
    }

    /**
     * Check if all mappings in this TruthAssignment have been decomposed
     * @return true if all Sentences in this and its parents have been decomposed
     */
    public boolean decomposedAll() {
        return map.keySet().stream().allMatch(this::isDecomposed) && (parent == null || parent.decomposedAll());
    }

    /**
     * Get the child TruthAssignments of this TruthAssignment
     * @return a set of all children
     */
    public List<TruthAssignment> getChildren () { return children; }

    /**
     * Recursively add children to all leaf nodes
     * @param h the children of the leaves of this to add
     */
    public List<TruthAssignment> addChildren(List<TruthAssignment> h) {
        if (children.isEmpty()) {
            h.forEach(c -> {
                TruthAssignment c1 = new TruthAssignment(c);
                children.add(c1);
                c1.setParent(this);
            });
            return children;
        }
        else
            return children.stream().flatMap(c -> addChildren(h).stream()).collect(Collectors.toList());
    }

    /**
     * Recursively find the TruthAssignment equal to or a
     * parent of this which explicitly has a mapping for s
     * @param s the sentence to search for
     * @return the TruthAssignment containing s
     */
    public TruthAssignment getEnclosingTruthAssignment(Sentence s) {
        if (map.containsKey(s))
            return this;
        if (parent != null)
            return parent.getEnclosingTruthAssignment(s);
        return null;
    }

    public Set<TruthAssignment> getLeaves() {
        if (children.isEmpty()) {
            HashSet<TruthAssignment> h = new HashSet<>();
            if (isConsistent())
                h.add(this);
            return h;
        }

        return new HashSet<TruthAssignment>() {{
            addAll(children.stream().flatMap(s -> s.getLeaves().stream()).filter(TruthAssignment::isConsistent)
                    .collect(Collectors.toSet()));
        }};
    }
}
