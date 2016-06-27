package logicalreasoner.truthassignment;

import expression.sentence.BooleanSentence;
import expression.sentence.ForAll;
import expression.sentence.Sentence;
import gui.truthtreevisualization.TreeBranch;
import gui.truthtreevisualization.TruthTree;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * The TruthAssignment class represents a function
 * mapping of logical Sentences to their values in some
 * logically plausible universe; the TruthAssignment is
 * a logical model of the world
 */
public class TruthAssignment {
  private static long truthAssignmentCount = 0;

  private long UID;
  private Map<Sentence, TruthValue> map;     // The explicit Sentence -> Boolean mapping
  private Map<Sentence, TruthAssignment> inheritedMappings;
  private TruthAssignment parent;
  private List<TruthAssignment> children;
  private Set<TruthAssignment> leaves;
  private Set<Sentence> constants;

  /**
   * Create a new, empty TruthAssignment
   */
  public TruthAssignment() {
    UID = truthAssignmentCount++;
    map = new ConcurrentHashMap<>();
    inheritedMappings = new ConcurrentHashMap<>();
    parent = null;
    children = new ArrayList<>();
    leaves = new HashSet<>();
    constants = new HashSet<>();
  }

  /**
   * Create a new, empty TruthAssignment with the
   * provided ID. Useful for temporary TruthAssignments,
   * so that the static counter doesn't increase as quickly
   */
  public TruthAssignment(long id) {
    UID = id;
    map = new ConcurrentHashMap<>();
    inheritedMappings = new ConcurrentHashMap<>();
    parent = null;
    children = new ArrayList<>();
    leaves = new HashSet<>();
    constants = new HashSet<>();
  }

  /**
   * Create a copy of another TruthAssignment, th
   *
   * @param ta the TruthAssignment to copy
   */
  public TruthAssignment(TruthAssignment ta) {
    UID = truthAssignmentCount++;
    this.map = new ConcurrentHashMap<>();
    inheritedMappings = new ConcurrentHashMap<>(ta.inheritedMappings);
    ta.map.forEach((k, v) -> {
      map.put(k, new TruthValue(v));
      if (k.isAtomic())
        setDecomposed(k);
    });

    this.parent = ta.parent;
    children = new ArrayList<>(ta.children);
    leaves = new HashSet<>(ta.leaves);
    constants = new HashSet<>(ta.constants);
  }

  /**
   * Create a copy of another TruthAssignment
   * with a different parent
   *
   * @param ta the TruthAssignment to copy
   * @param p  the parent of the new TruthAssignment
   */
  public TruthAssignment(TruthAssignment ta, TruthAssignment p) {
    UID = truthAssignmentCount++;
    this.map = new ConcurrentHashMap<>();
    this.inheritedMappings = new ConcurrentHashMap<>(ta.inheritedMappings);
    this.parent = p;
    children = new ArrayList<>(ta.children);
    leaves = new HashSet<>(ta.leaves);
    constants = new HashSet<>(ta.constants);
    parent.children.add(this);
    parent.leaves.add(this);
    ta.map.forEach((k, v) -> {
      v.getValues().forEach((b, i) -> set(k, b, i));
      if (k.isAtomic())
        setDecomposed(k);
    });
  }

  /**
   * Get the name of this TruthAssignment.
   * The name is the letter "h" followed by its UID
   *
   * @return the name of this TruthAssignment
   */
  public String getName() {
    return "h" + UID;
  }

  /**
   * Check whether this TruthAssignment contains any mappings
   * @return true if this has no mappings, false otherwise
   */
  public boolean isEmpty() { return map.isEmpty();
  }

  /**
   * Obtain a hashCode for this TruthAssignment
   * @return the UID of this
   */
  public int hashCode() {
    return (int) UID;
  }

  /**
   * Check whether this has the same mappings as t
   * @param t the TruthAssingment to check mappings against
   * @return true if the mappings of t and this are equal
   */
  public boolean assignmentsEqual(TruthAssignment t) {
    return t.map.equals(map);
  }

  /**
   * Check to see whether two TruthAssignments are equal
   * @param o the second Object to check equality against
   * @return true if o is equal to this, false otherwise
   */
  public boolean equals(Object o) {
    return this == o;
    /*
    if (o == this)
      return true;
    if (o instanceof TruthAssignment) {
      TruthAssignment h = (TruthAssignment) o;
      return UID == h.UID && parent == h.parent; //&& map.equals(h.map) && children.equals(h.children);
    }
    return false;
    */
  }

  /**
   * Get a String representation of this TruthAssignment
   * @return a string displaying the mappings in the TruthAssignment
   */
  public String toString() {
    return getName();
  }

  /**
   * Get the TruthValue assigned to a given Sentence
   *
   * @param s the Sentence to obtain a TruthValue for
   * @return the mapped TruthValue (if found), null otherwise
   */
  public TruthValue getTruthValue(Sentence s) {
    TruthValue v = map.get(s);
    if (v != null)
      return v;
    return inheritedMappings.get(s).map.get(s);
  }

  /**
   * Print out a representation of this TruthAssignment
   */
  public void print() {
    print("", true);
  }

  /**
   * A helper function for printing TruthAssignments
   * @param prefix the branch lines before this TruthAssignment
   * @param isTail true if this is a leaf node
   */
  private void print(String prefix, boolean isTail) {
    System.out.println(prefix + getName());
    StringBuilder builder = new StringBuilder(prefix);
    if (isTail)
      builder.append("└── ").append(map.toString());
    else
      builder.append("├── ").append(map.toString());
    if (children.isEmpty()) {
      if (areParentsConsistent())
        builder.append(" isConsistent: ✓");
      else
        builder.append(" isConsistent: ✗");

      if (decomposedAll())
        builder.append(" decomposedAll: ✓");
      else
        builder.append(" decomposedAll: ✗");
    }
    System.out.println(builder.toString());

    String newPrefix;
    if (isTail)
      newPrefix = prefix + "    ";
    else
      newPrefix = prefix + "│   ";

    IntStream.range(0, children.size() - 1).mapToObj(children::get).forEach(c -> c.print(newPrefix, false));
    if (!children.isEmpty())
      children.get(children.size() - 1).print(newPrefix, true);
  }

  /**
   * Add a mapping from a Sentence to a TruthAssignment containing
   * it to all children. Used for caching mappings, rather than
   * using recursion.
   * @param s the Sentence to be found in t
   * @param t the TruthAssignment containing a mapping for s
   */
  private void addMappingDownward(Sentence s, TruthAssignment t) {
    children.forEach(c -> {
      c.inheritedMappings.putIfAbsent(s, t);
      c.addMappingDownward(s, t);
    });
  }

  /**
   * Add a mapping from a Sentence to a TruthAssignment containing
   * it to all children. Used for caching mappings, rather than
   * using recursion.
   *
   * @param s the Sentence to be found in t
   * @param t the TruthAssignment containing a mapping for s
   */
  private void addMappingsDownward(Collection<Pair> c) {
    if (c.isEmpty())
      return;

    children.forEach(child -> {
      c.forEach(p -> child.inheritedMappings.putIfAbsent(p.sentence, p.truthAssignment));
      child.addMappingsDownward(c);
    });
  }

  private void addMappingsAndConstants(Collection<Pair> c, Collection<Sentence> constants) {
    if (c.isEmpty() && constants.isEmpty())
      return;

    this.constants.addAll(constants);
    addInstantiatedConstants(constants);
    children.forEach(child -> {
      c.forEach(p -> child.inheritedMappings.putIfAbsent(p.sentence, p.truthAssignment));
      child.addMappingsAndConstants(c, constants);
    });
  }

  public List<Pair> getInheritedMappings() {
    return inheritedMappings.entrySet().stream().map(e -> Pair.makePair(e.getKey(), e.getValue())).collect(Collectors.toList());
  }

  /**
   * Get a Set of all constants which are represented in this TruthAssignment
   * @return a Set of valid constants in this TruthAssignment
   */
  public Set<Sentence> getConstants() {
    return constants;
  }

  /**
   * Propagate newly generated constants to Universally Quantified Sentences to
   * all parents
   * @param constants the constants to be added to this
   */
  private void addInstantiatedConstants(Collection<Sentence> constants) {
    if (!constants.isEmpty()) {
      map.entrySet().stream().filter(e -> e.getValue().isModelled() && e.getKey() instanceof ForAll)
              .forEach(e -> e.getValue().addInstantiations(constants));

      inheritedMappings.entrySet().stream().filter(e -> e.getValue().models(e.getKey()) && e.getKey() instanceof ForAll)
              .forEach(e -> e.getValue().getTruthValue(e.getKey()).addInstantiations(constants));
    }
  }

  /**
   * Make sure that all parents have the relevant constants
   */
  private void refreshInstantiatedConstants() {
    addInstantiatedConstants(getConstants());
  }

  /**
   * Add constant c to all children of this
   * @param c the constant to be added
   */
  public void addConstant(Sentence c) {
    constants.add(c);
    addInstantiatedConstants(Collections.singletonList(c));
    children.parallelStream().forEach(child -> child.addConstant(c));
  }

  /**
   * Add all constants in c to all children of this
   * @param c a Collection of constants to be added
   */
  public void addConstants(Collection<Sentence> c) {
    if (c.isEmpty() || c.stream().allMatch(constants::contains))
      return;
    constants.addAll(c);
    addInstantiatedConstants(c);
    children.forEach(child -> child.addConstants(c));
  }

  public TruthTree makeTruthTree() {
    TreeBranch root = makeBranch(map.keySet(), children.isEmpty());
    children.forEach(child -> root.addChild(child.makeTruthTree().getRoot()));
    TruthTree tree = new TruthTree(root);
    return tree;
  }

  private TreeBranch makeBranch(Set<Sentence> sens, boolean isLeaf) {
    TreeBranch newBranch = new TreeBranch();
    sens.forEach(s -> newBranch.addStatement(s.toSExpression() + " " + map.get(s).getValues().keySet().toString()));
    if (isLeaf) {
      newBranch.addStatement((isConsistent() ? "✓" : "✗"));
    }
    return newBranch;
  }

  /**
   * Get the parent TruthAssignment of this
   * @return the parent of this
   */
  public TruthAssignment getParent() {
    return parent;
  }

  /**
   * Reset the parent of this
   * @param h the new Parent of this
   */
  public void setParent(TruthAssignment h) {
    parent = h;
  }

  /**
   * Get the UID of the inference which created this assignment
   * @param s the sentence assigned to models
   * @param models the truth value of s
   * @return the UID of the inference which mapped s to models, or -1 if no mapping exists
   */
  public int getInferenceNum(Sentence s, boolean models) {
    TruthValue v = map.get(s);
    if (v == null) {
      TruthAssignment t = inheritedMappings.get(s);
      if (t == null)
        return -1;
      return t.map.get(s).getInferenceNum(models);
    }

    return v.getInferenceNum(models);
  }

  /**
   * Add a new mapping to the TruthAssignment
   *
   * @param s the Sentence to be set to true
   */
  public void setTrue(Sentence s, int inferenceNum) {
    if (!hasMapping(s, true)) {
      TruthValue t = map.get(s);
      if (t != null) {
        t.setTrue(inferenceNum);
        addConstants(s.getConstants());
      } else {
        t = new TruthValue(s);
        t.setTrue(inferenceNum);
        map.put(s, t);
        addMappingsAndConstants(Collections.singletonList(Pair.makePair(s, this)), s.getConstants());
      }
      if (s.isAtomic())
        setDecomposed(s);

      if (s instanceof ForAll)
        map.get(s).addInstantiations(getConstants());
    }
  }

  /**
   * Add a new mapping to the TruthAssignment
   *
   * @param s the Sentence to be set to false
   */
  public void setFalse(Sentence s, int inferenceNum) {
    if (!hasMapping(s, false)) {
      TruthValue t = map.get(s);
      if (t != null) {
        t.setFalse(inferenceNum);
        addConstants(s.getConstants());
      } else {
        t = new TruthValue(s);
        t.setFalse(inferenceNum);
        map.put(s, t);
        addMappingsAndConstants(Collections.singletonList(Pair.makePair(s, this)), s.getConstants());
      }

      if (s.isAtomic())
        setDecomposed(s);
    }
  }

  /**
   * Add a new mapping to the TruthAssignment
   *
   * @param s the Sentence to be set to b
   * @param b the new truth value of s
   */
  public void set(Sentence s, boolean b, int inferenceNum) {
    if (!hasMapping(s, b)) {
      TruthValue t = map.get(s);
      if (t != null) {
        t.set(b, inferenceNum);
        addConstants(s.getConstants());
      } else {
        t = new TruthValue(s);
        t.set(b, inferenceNum);
        map.put(s, t);
        addMappingsAndConstants(Collections.singletonList(Pair.makePair(s, this)), s.getConstants());
      }

      if (s.isAtomic())
        setDecomposed(s);
      if (b && s instanceof ForAll)
        map.get(s).addInstantiations(getConstants());
    }
  }

  /**
   * Return true if this |= s (or a parent of this |= s)
   *
   * @param s the expression.sentence to be tested for
   * @return true if this |= s, false if this |= (not s),
   * null if not found
   */
  public Boolean models(Sentence s) {
    if (s == null)
      return null;
    TruthValue v = map.get(s);
    if (v != null)
      return v.isModelled();
    TruthAssignment t = inheritedMappings.get(s);
    if (t == null)
      return null;
    return t.models(s);
  }

  /**
   * Return true if this or a parent of this has a mapping for s
   *
   * @param s the expression.sentence to search for
   * @return true if s is mapped to some value,
   * false otherwise
   */
  public boolean isMapped(Sentence s) {
    return inheritedMappings.containsKey(s) || map.containsKey(s);
  }

  /**
   * Return true if this or a parent of this maps s to TruthValue b
   *
   * @param s the sentence to search for
   * @param b the assignment to search for
   * @return true if a mapping from s to b exists, false otherwise
   */
  public boolean hasMapping(Sentence s, boolean b) {
    TruthValue v = map.get(s);
    if (v != null && v.contains(b))
      return true;
    TruthAssignment h = inheritedMappings.get(s);
    return h != null && h.map.get(s).contains(b);
  }

  /**
   * @return all Sentences mapped in this TruthAssignment only
   */
  public Set<Sentence> keySet() {
    return map.keySet();
  }

  public Stream<Pair> flattenParallel() {
    if (parent == null)
      return map.keySet().parallelStream().map(s -> Pair.makePair(s, this));
    return //Stream.concat(parent.flattenParallel(),
            Stream.concat(inheritedMappings.entrySet().parallelStream().map(e -> Pair.makePair(e.getKey(), e.getValue())),
                    map.keySet().parallelStream().map(s -> Pair.makePair(s, this)));
  }

  public Stream<Pair> flattenSerial() {
    if (parent == null)
      return map.keySet().stream().map(s -> Pair.makePair(s, this));
    return Stream.concat(inheritedMappings.entrySet().stream().map(e -> Pair.makePair(e.getKey(), e.getValue())),
            map.keySet().stream().map(s -> Pair.makePair(s, this)));
  }

  /**
   * Get a ParallelStream of all active mappings of this TruthAssignment
   * @return a flattend ParallelStream of this
   */
  public Stream<Pair> flattenUndecomposedParallel() {
    if (parent == null)
      return map.keySet().parallelStream().filter(s -> !isDecomposed(s)).map(s -> Pair.makePair(s, this));
    return Stream.concat(inheritedMappings.entrySet().parallelStream()
                    .filter(e -> !e.getValue().isDecomposed(e.getKey())).map(e -> Pair.makePair(e.getKey(), e.getValue())),
            map.keySet().parallelStream().filter(s -> !isDecomposed(s)).map(s -> Pair.makePair(s, this)));
  }

  /**
   * Get a Stream of all active mappings of this TruthAssignment
   * @return a flattend Stream of this
   */
  public Stream<Pair> flattenUndecomposedSerial() {
    if (parent == null)
      return map.keySet().stream().filter(s -> !isDecomposed(s)).map(s -> Pair.makePair(s, this));
    return Stream.concat(inheritedMappings.entrySet().stream()
                    .filter(e -> !e.getValue().isDecomposed(e.getKey())).map(e -> Pair.makePair(e.getKey(), e.getValue())),
            map.keySet().stream().filter(s -> !isDecomposed(s)).map(s -> Pair.makePair(s, this)));
  }


  /**
   * Get all mappings of atomic statements in this TruthAssignment
   * @return a map of Sentences to their truth values
   */
  public Map<Sentence, Boolean> getCounterExample() {
    Map<Sentence, Boolean> s = new HashMap<>();
    map.entrySet().stream().filter(e -> e.getKey().isAtomic()).forEach(e -> s.put(e.getKey(), e.getValue().isModelled()));

    if (parent != null)
      s.putAll(parent.getCounterExample());
    return s;
  }

  /**
   * Add all of the mappings of h into this TruthAssignment
   * @param h the mappings to add to this
   */
  public Stream<Pair> merge(TruthAssignment h) {
    List<Pair> l = h.map.entrySet().stream().map(e -> {
      TruthValue truthValue = map.get(e.getKey());
      if (truthValue == null) {
        truthValue = new TruthValue(e.getValue());
        map.put(e.getKey(), truthValue);
        if (e.getKey().isAtomic())
          truthValue.setDecomposed();
        return Pair.makePair(e.getKey(), this);
      } else {
        truthValue.putAll(e.getValue());
        return Pair.makePair(truthValue.getSentence(), this);
      }
    }).collect(Collectors.toList());

    refreshInstantiatedConstants();
    addMappingsAndConstants(l, h.getConstants());
    return l.stream();
  }

  /**
   * Check whether the mappings in this TruthAssignment are consistent with
   * each other and mappings in all parents
   * @return true if all such mappings are consistent, false otherwise
   */
  public boolean consistencyTest() {
    return map.keySet().stream().allMatch(s -> {
      if (!map.get(s).isConsistent())  // Check if the Sentence has multiple values in THIS TruthAssignment
        return false;
      else if (s instanceof BooleanSentence && map.get(s).isModelled() != s.eval(this))
        return false;

      /*
      TruthAssignment h = inheritedMappings.get(s);  // This is slow, for some reason ???
      if (h == null)
        return true;
      Boolean b = h.models(s);
      return b == null || b == models(s);
      */

      if (parent == null)
        return true;
      Boolean b = parent.models(s);
      return b == null || b == models(s);
    });
  }

  /**
   * Make sure the mappings of Sentences is the same as their evaluation
   * under this TruthAssignment
   * @return true if all mappings are consistent, false otherwise
   */
  public boolean isConsistent() {
    return consistencyTest() && (children.isEmpty() || children.parallelStream().anyMatch(TruthAssignment::isConsistentSerial));
  }

  /**
   * A serial version of isConsistent, used in recursion to avoid
   * construction of too many parallelStreams
   * @return true if all mappings are consistent, false otherwise
   */
  private boolean isConsistentSerial() {
    return consistencyTest() && (children.isEmpty() || children.stream().anyMatch(TruthAssignment::isConsistentSerial));
  }

  /**
   * Make sure that this TruthAssignment and all of its parents are consistent
   * @return true if all mappings are consistent, false otherwise
   */
  public boolean areParentsConsistent() {
    return consistencyTest() && (parent == null || parent.areParentsConsistent());
  }

  /**
   * Mark s as having been decomposed under this TruthAssignment
   * @param s the Sentence which has been decomposed
   */
  public void setDecomposed(Sentence s) {
    TruthValue tv = map.get(s);
    if (tv == null) {
      System.exit(1);
      TruthAssignment p = inheritedMappings.get(s);
      if (p != null)
        tv = p.map.get(s);
    }
    if (tv != null)
      tv.setDecomposed();
  }

  /**
   * Check whether s has been reasoned over in this TruthAssignment
   *
   * @param s theSentence to search for
   * @return true if s has been reasoned over, false otherwise
   */
  private boolean isDecomposed(Sentence s) {
    TruthValue tv = map.get(s);
    if (tv == null) {
      System.exit(1);
      TruthAssignment p = inheritedMappings.get(s);
      if (p == null)
        return false;
      tv = p.map.get(s);
      if (tv == null)
        return false;
    }
    // Check for finished Universal Quantifiers
    if (tv.isModelled() && s instanceof ForAll)
      return tv.instantiatedAll() && (getConstants().size() > 0 || tv.getInstantiatedConstants().size() > getConstants().size());
    return tv.isDecomposed();
  }

  /**
   * Check if all mappings in this TruthAssignment have been decomposed
   * @return true if all Sentences in this and its parents have been decomposed
   */
  public boolean decomposedAll() {
    return flattenUndecomposedSerial().count() == 0;
  }

  /**
   * Check if all mappings in this TruthAssignment have been decomposed
   * @return true if all Sentences in this and its parents have been decomposed
   */
  public boolean decomposedAllPropositions() {
    return flattenUndecomposedSerial().filter(p -> !p.sentence.isQuantifier()).count() == 0;
  }

  /**
   * Get the child TruthAssignments of this TruthAssignment
   * @return a set of all children
   */
  public List<TruthAssignment> getChildren() {
    return children;
  }

  /**
   * Recursively add children to all leaf nodes
   * @param h the children of the leaves of this to add
   */
  public Stream<Pair> addChildren(Collection<TruthAssignment> h) {
    List<Pair> flattened = flattenSerial().collect(Collectors.toList());
    List<Pair> l = h.stream().flatMap(c -> {
      TruthAssignment child = new TruthAssignment(c, this);
      leaves.add(child);
      child.addConstants(constants);
      child.refreshInstantiatedConstants();
      flattened.forEach(p -> child.inheritedMappings.put(p.sentence, p.truthAssignment));
      return child.keySet().stream().map(s -> Pair.makePair(s, child));
    }).collect(Collectors.toList());

    if (parent != null)
      parent.replaceLeaves(leaves, this);
    return l.stream();
  }

  private void replaceLeaves(Collection<TruthAssignment> newLeaves, TruthAssignment oldLeaf) {
    leaves.remove(oldLeaf);
    leaves.addAll(newLeaves);
    if (parent != null)
      parent.replaceLeaves(newLeaves, oldLeaf);
  }

  /**
   * Get all descendants of this which have noe children
   * @return the set of leaf TruthAssignments under this
   */
  public Stream<TruthAssignment> getLeaves() {
    if (leaves.isEmpty())
      return Stream.of(this);
    return new HashSet<>(leaves).stream();
  }

  public Stream<TruthAssignment> getLeavesParallel() {
    if (leaves.isEmpty())
      return Stream.of(this);
    return new HashSet<>(leaves).parallelStream();
  }

  /**
   * Get the top-level TruthAssignment in this tree
   * @return the uppermost parent of this
   */
  public TruthAssignment getRoot() {
    if (parent == null)
      return this;
    return parent.getRoot();
  }

  /**
   * Get the TruthAssignment with a mapping to s
   * @param s the Sentence to search for
   * @return the TruthAssignment containing s, null if not found
   */
  public TruthAssignment getParentContaining(Sentence s) {
    if (map.containsKey(s))
      return this;
    return inheritedMappings.get(s);
  }

  /**
   * Get the uppermost TruthAssignments in which this Constant is
   * present
   *
   * @param s the constant to search for
   * @return a List of all uppermost children containing s
   */
  public List<TruthAssignment> getConstantOrigins(Sentence s) {
    return getConstantOriginsHelper(s).collect(Collectors.toList());
  }

  /**
   * A helper function to get all uppermost children containing
   *
   * @param s the constant to search for
   * @return a Stream of all uppermost children containing constant s
   */
  private Stream<TruthAssignment> getConstantOriginsHelper(Sentence s) {
    if (constants.contains(s))
      return Stream.of(this);
    return children.parallelStream().flatMap(c -> c.getConstantOriginsHelper(s));
  }

  /**
   * Check whether this TruthAssignment is satisfiable
   * @return true if all reasoning is completed, and all mappings are consistent
   */
  public boolean isSatisfied() {
    return decomposedAll() && isConsistent();
  }
}
