package logicalreasoner.truthassignment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import expression.sentence.Atom;
import expression.sentence.Constant;
import expression.sentence.Predicate;
import expression.sentence.Sentence;
import gui.truthtreevisualization.TreeBranch;
import gui.truthtreevisualization.TruthTree;

/**
 * The TruthAssignment class represents a function
 * mapping of logical Sentences to their values in some
 * logically plausible universe; the TruthAssignment is
 * a logical model of the world
 */
public class TruthAssignment {
    private static int truthAssignmentCount = 0;

    private int UID;
    private Map<Sentence, TruthValue> map;     // The explicit Sentence -> Boolean mapping

    private TruthAssignment parent;
    private List<TruthAssignment> children;

    /**
     * Create a new, empty TruthAssignment
     */
    public TruthAssignment() {
        UID = truthAssignmentCount++;
        map = new HashMap<>();
        parent = null;
        children = new ArrayList<>();
    }

    /**
     * Create a new, empty TruthAssignment with the
     * provided ID. Useful for temporary TruthAssignments,
     * so that the static counter doesn't increase as quickly
     */
    public TruthAssignment(int id) {
        UID = id;
        map = new HashMap<>();
        parent = null;
        children = new ArrayList<>();
    }

    /**
     * Create a copy of another TruthAssignment, th
     * @param ta the TruthAssignment to copy
     */
    public TruthAssignment(TruthAssignment ta) {
        UID = ta.UID;
        this.map = new HashMap<>();
        ta.map.forEach((k, v) -> map.put(k, new TruthValue(v)));
        this.parent = ta.parent;
        children = new ArrayList<>();
        ta.children.forEach(c -> children.add(c));
    }

    public String getName() { return "h" + UID; }

    public int hashCode() { return map.hashCode(); }

    public boolean equals(Object o) {
        if (o instanceof TruthAssignment) {
            TruthAssignment h = (TruthAssignment)o;

            return map.equals(h.map) && parent == h.parent && children.equals(h.children);
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
        System.out.println(prefix + (isTail ? "└── " : "├── ") + toString() + " isConsistent: " +(isConsistent() ? "✓" : "✗") + " decomposedAll: " + (decomposedAll() ? "✓" : "✗"));
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
    
    public TruthTree makeTruthTree() {
    	TreeBranch root = makeBranch(map.keySet());
    	children.forEach(child -> {
    		root.addChild(child.makeTruthTree().getRoot());
    	});
    	
    	TruthTree tree = new TruthTree(root);
    	return tree;
    }
    
    private TreeBranch makeBranch(Set<Sentence> sens) {
    	TreeBranch newBranch = new TreeBranch();
    	sens.forEach(s -> {
    		String prefix = "";
    		if (map.get(s).isConsistent() && map.get(s).containsFalse()) {
    			prefix = "¬";
    		}
    		newBranch.addStatement(prefix + s.toString());
    	});
    	
    	return newBranch;
    }

    public TruthAssignment getParent() { return parent; }

    public void setParent(TruthAssignment h) { parent = h; }


    /**
     * Add a new mapping to the TruthAssignment
     * @param s the Sentence to be set to true
     */
    public void setTrue(Sentence s, int inferenceNum) {
        if (map.keySet().contains(s))
            map.get(s).setTrue(inferenceNum);
        else {
            TruthValue t = new TruthValue();
            t.set(true, inferenceNum);
            map.put(s, t);
        }

        if (s instanceof Atom || s instanceof Predicate)
            setDecomposed(s);
    }

    /**
     * Add a new mapping to the TruthAssignment
     * @param s the Sentence to be set to false
     */
    public void setFalse(Sentence s, int inferenceNum) {
        if (map.keySet().contains(s))
            map.get(s).setFalse(inferenceNum);
        else {
            TruthValue t = new TruthValue();
            t.set(false, inferenceNum);
            map.put(s, t);
        }

        if (s instanceof Atom || s instanceof Predicate)
            setDecomposed(s);
    }

    /**
     * Add a new mapping to the TruthAssignment
     * @param s the Sentence to be set to b
     * @param b the new truth value of s
     */
    public void set(Sentence s, boolean b, int inferenceNum) {
        if (map.keySet().contains(s))
            map.get(s).set(b, inferenceNum);
        else {
            TruthValue t = new TruthValue();
            t.set(b, inferenceNum);
            map.put(s, t);
        }

        if (s instanceof Atom || s instanceof Predicate)
            setDecomposed(s);
    }

    /**
     * Return true if this |= s (or a parent of this |= s)
     * @param s the expression.sentence to be tested for
     * @return true if this |= s, false if this |= (not s),
     * null if not found
     */
    public Boolean models(Sentence s) {
        if (map.containsKey(s))
            return map.get(s).isModelled();
        else if (parent != null)
            return parent.models(s);
        return false;
    }

    /**
     * Return true if this or a parent of this has a mapping for s
     * @param s the expression.sentence to search for
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
     * Recursively collect the mappings of this
     * TruthAssignment and its parents
     *
     * @return a set of all inferences under this TruthAssignment
     */
    public Set<Sentence> getSentencesUpwards() {
        if (parent == null)
            return map.keySet();

        Set<Sentence> s = new HashSet<>();
        s.addAll(map.keySet());
        s.addAll(parent.getSentencesUpwards());
        return s;
    }

    /**
     * Get a Stream of all true Sentences in this
     * @return a Stream of all true Sentences in this
     */
    public Stream<Sentence> getTrueSentences() {
        return getSentencesUpwards().stream().filter(this::models);
    }

    /**
     * Get a Stream of all false Sentences in this
     * @return a Stream of all false Sentences in this
     */
    public Stream<Sentence> getFalseSentences() {
        return getSentencesUpwards().stream().filter(sentence -> !models(sentence));
    }

    /**
     * Add all of the mappings of h into this TruthAssignment
     * @param h the mappings to add to this
     */
    public void merge(TruthAssignment h) {
        h.map.forEach((k, v) -> {
            if (!map.containsKey(k))
                map.put(k, new TruthValue(v));
            else
                map.get(k).putAll(v);
        });
    }

    /**
     * Make sure the mappings of Sentences is the same as their evaluation
     * under this TruthAssignment
     * @return true if all mappings are consistent, false otherwise
     */
    public boolean isConsistent() {
        return map.keySet().stream().allMatch(s -> {
            if (s.equals(Constant.TRUE) && !map.get(s).isModelled())
                return false;
            if (s.equals(Constant.FALSE) && map.get(s).isModelled())
                return false;
            if (!map.get(s).isConsistent())
                return false;
            return !(parent != null && parent.isMapped(s)) || parent.models(s).equals(models(s));
        }) && (children.isEmpty() || children.stream().anyMatch(TruthAssignment::isConsistent));
    }

    /**
     * Mark this expression.sentence as having been decomposed under this TruthAssignment
     * @param s the expression.sentence which has been decomposed
     */
    public void setDecomposed(Sentence s) {
        if (map.containsKey(s))
            map.get(s).setDecomposed();
        else if (parent != null)
            parent.setDecomposed(s);
    }

    /**
     * Check if this Sentence has already been reasoned upon.
     * @param s the Sentence to check
     * @return true if s has been decomposed in this or a child of this,
     *  false otherwise
     */
    public Boolean isDecomposed(Sentence s) {
        if (map.containsKey(s))
            return map.get(s).isDecomposed();
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
    public void addChildren(List<TruthAssignment> h) {
        h.forEach(c -> {
            TruthAssignment c1 = new TruthAssignment(c);
            children.add(c1);
            c1.setParent(this);
        });
    }

    /**
     * Get all descendents of this which have noe children
     * @return the set of leaf TruthAssignments under this
     */
    public Set<TruthAssignment> getLeaves() {
        if (children.isEmpty()) {
            HashSet<TruthAssignment> h = new HashSet<>();
            if (isConsistent())
                h.add(this);
            return h;
        }

        return children.stream().flatMap(s -> s.getLeaves().stream()).filter(TruthAssignment::isConsistent).collect(Collectors.toSet());
    }

    /**
     * Get the toplevel TruthAssignment in this tree
     * @return
     */
    public TruthAssignment getRoot() {
        if (parent == null)
            return this;

        return parent.getRoot();
    }
}
