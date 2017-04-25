package logicalreasoner.truthassignment;

import expression.Sort;
import expression.sentence.BooleanSentence;
import expression.sentence.ForAll;
import expression.sentence.Sentence;
import logicalreasoner.inference.Closure;

import java.io.Serializable;
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
public class TruthAssignment implements Serializable {
  private static long truthAssignmentCount = 0;

  private long UID;
  private Map<Sentence, TruthValue> map;     // The explicit Sentence -> Boolean mapping
  private Map<Sentence, TruthAssignment> inheritedMappings;
  private TruthAssignment parent;
  private List<TruthAssignment> children;
  private Set<TruthAssignment> leaves;
  private Set<Sentence> constants;
  private Map<Sentence, Boolean> suppositions;

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
    suppositions = new HashMap<>();
  }

  /**
   * Create a new, empty TruthAssignment with the
   * provided ID. Useful for temporary TruthAssignments,
   * so that the static counter doesn't increase as quickly
   */
  public TruthAssignment(long id) {
    UID = id;
    map = new HashMap<>();
    inheritedMappings = new HashMap<>();
    parent = null;
    children = new ArrayList<>();
    leaves = new HashSet<>();
    constants = new HashSet<>();
    suppositions = new HashMap<>();
  }

  public TruthAssignment(Map<Sentence, TruthValue> m, long id) {
    this(id);
    merge(m, m.keySet().stream().flatMap(s -> s.getConstants().stream()).collect(Collectors.toSet()));
  }

  public TruthAssignment(Map<Sentence, TruthValue> m) {
    this(m, truthAssignmentCount++);
  }

  /**
   * Create a copy of another TruthAssignment, th
   *
   * @param ta the TruthAssignment to copy
   */
  public TruthAssignment(TruthAssignment ta) {
    UID = truthAssignmentCount++;
    this.map = new HashMap<>();
    suppositions = ta.suppositions;
    inheritedMappings = new HashMap<>(ta.inheritedMappings);
    ta.map.forEach(this::set);
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
    this.map = new HashMap<>();
    this.inheritedMappings = new HashMap<>(ta.inheritedMappings);
    suppositions = ta.suppositions;
    this.parent = p;
    children = new ArrayList<>(ta.children);
    leaves = new HashSet<>(ta.leaves);
    constants = new HashSet<>(ta.constants);
    parent.children.add(this);
    parent.leaves.add(this);

    addConstants(p.constants);
    inheritedMappings.putAll(p.inheritedMappings);
    p.map.keySet().forEach(s -> inheritedMappings.put(s, p));
    ta.map.forEach(this::set);
  }

  public int getUID() {
    return (int) UID;
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
    return t.map.keySet().stream().allMatch(s -> map.containsKey(s) && t.models(s) == models(s));
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
   * @param c the constants, paired with the TruthAssignments containing them,
   *          to be added to this.
   */
  public void addMappingsDownward(Collection<Pair> c) {
    if (c.isEmpty())
      return;

    children.forEach(child -> {
      c.forEach(p -> child.inheritedMappings.putIfAbsent(p.sentence, p.truthAssignment));
      child.addMappingsDownward(c);
    });
  }

  /**
   * Add a mapping from a Sentence to a TruthAssignment containing
   * it to all children. Used for caching mappings, rather than
   * using recursion.
   *
   * @param c the paired Constants and TruthAssignments to add
   */
  public void addMappings(Collection<Pair> c) {
    if (c.isEmpty())
      return;

    c.forEach(p -> inheritedMappings.putIfAbsent(p.sentence, p.truthAssignment));
    addMappingsDownward(c);
  }

  public void clearMappings() {
    inheritedMappings.clear();
  }

  public void addMappingsAndConstants(Collection<Pair> c, Collection<Sentence> constants) {
    if (c.isEmpty() && constants.isEmpty())
      return;

    this.constants.addAll(constants);
    if (children.isEmpty())
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
   * Get a Set of all constants of sort superSort which are represented in this TruthAssignment
   * @return a Set of valid constants in this TruthAssignment
   */
  public Set<Sentence> getConstants(Sort superSort) {
    return constants.stream().filter(c -> c.getSort().isSubSort(superSort)).collect(Collectors.toSet());
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

  public void addSupposition(Sentence s) {
    suppositions.put(s, map.get(s).isModelled());
  }

  public List<Map.Entry<Sentence, Boolean>> getSuppositions() {
    if (parent == null)
      return getImmediateSuppositions();
    List<Map.Entry<Sentence, Boolean>> m = new ArrayList<>(suppositions.entrySet());
    m.addAll(parent.getSuppositions());
    return m;
  }

  public List<Map.Entry<Sentence, Boolean>> getImmediateSuppositions() {
    return new ArrayList<>(suppositions.entrySet());
  }

  /**
   * Add a new mapping to the TruthAssignment
   *
   * @param s the Sentence to be set to true
   */
  public void setTrue(Sentence s, int inferenceNum) {
    set(s, true, inferenceNum);
  }

  /**
   * Add a new mapping to the TruthAssignment
   *
   * @param s the Sentence to be set to false
   */
  public void setFalse(Sentence s, int inferenceNum) {
    set(s, false, inferenceNum);
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

  public void set(Sentence s, TruthValue v) {
    if (!((!v.containsTrue() || hasMapping(s, true))
            && (!v.containsFalse() || hasMapping(s, false)))
            && v.getSentence() == s) {
      map.put(s, new TruthValue(v));
      addMappingsAndConstants(Collections.singletonList(Pair.makePair(s, this)), s.getConstants());

      if (s.isAtomic())
        setDecomposed(s);
      if (v.isModelled() && s instanceof ForAll)
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
    return map.containsKey(s) || inheritedMappings.containsKey(s);
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

  /**
   * Stream all of the TruthValues local to this TruthAssignment
   *
   * @return a stream of TruthValues
   */
  public Stream<TruthValue> stream() {
    return map.values().stream();
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
    return merge(h.map, h.constants);
  }

  /**
   * Add all of the mappings of h into this TruthAssignment
   *
   * @param h the mappings to add to this
   */
  public Stream<Pair> merge(Map<Sentence, TruthValue> h, Collection<Sentence> newConstants) {
    List<Pair> l = h.entrySet().stream().map(e -> {
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

    if (constants.containsAll(newConstants))
      addMappingsAndConstants(l, constants);
    else
      addMappingsAndConstants(l, Stream.concat(getConstants().stream(), newConstants.stream()).collect(Collectors.toSet()));

    return l.stream();
  }

  /**
   * Check whether the mappings in this TruthAssignment are consistent with
   * each other and mappings in all parents
   * @return true if all such mappings are consistent, false otherwise
   */
  public boolean consistencyTest() {
    return map.keySet().stream().allMatch(s -> {
      TruthValue v = map.get(s);
      // Check if the Sentence has multiple values in THIS TruthAssignment
      if (!v.isConsistent()
              || (s instanceof BooleanSentence && v.isModelled() != s.eval(this)))
        return false;

      if (parent == null)
        return true;
      Boolean b = parent.models(s);
      TruthAssignment h = inheritedMappings.get(s);

      return (b == null || b == models(s)) && (h == null || h.models(s) == models(s));
    });
  }

  private Closure closeBranchHelper(int inferenceNum) {
    return map.keySet().stream().map(s -> {
      if (!map.get(s).isConsistent())  // Check if the Sentence has multiple values in THIS TruthAssignment
        return new Closure(s, this, this, inferenceNum);
      else if (s instanceof BooleanSentence && map.get(s).isModelled() != s.eval(this))
        return new Closure(s, this, this, inferenceNum);

      TruthAssignment h = inheritedMappings.get(s);  // This is slow, for some reason ???
      if (h == null)
        return null;
      Boolean b = h.models(s);
      if (b != null && b != models(s))
        return new Closure(s, this, h, inferenceNum);
      return null;
    }).filter(c -> c != null).findFirst().orElse(null);
  }

  public Closure closeBranch(int inferenceNum) {
    Closure c = closeBranchHelper(inferenceNum);
    if (c != null || parent == null)
      return c;
    return parent.closeBranch(inferenceNum);
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

    tv.setDecomposed();
  }

  /**
   * Check whether s has been reasoned over in this TruthAssignment
   *
   * @param s theSentence to search for
   * @return true if s has been reasoned over, false otherwise
   */
  public boolean isDecomposed(Sentence s) {
    TruthValue tv = map.get(s);
    if (tv == null) {
      TruthAssignment p = inheritedMappings.get(s);
      if (p == null)
        return false;
      tv = p.map.get(s);
      if (tv == null)
        return false;
    }
    // Check for finished Universal Quantifiers
    if (tv.isModelled() && s instanceof ForAll) {
      Sort sort = ((ForAll) s).getVariable().getSort();
      Set<Sentence> constants = getConstants(sort);
      return tv.instantiatedAll() && (constants.size() > 0 || tv.getInstantiatedConstants().size() > constants.size());
    }
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

  public boolean hasChildren() {
    return !children.isEmpty();
  }

  public TruthAssignment getChild(int index) {
    return children.get(index);
  }

  /**
   * Recursively add children to all leaf nodes
   * @param h the children of the leaves of this to add
   */
  public Stream<Pair> addChildren(Collection<TruthAssignment> h) {
    return h.parallelStream().flatMap(c -> {
      TruthAssignment child = new TruthAssignment(c, this);
      leaves.add(child);
      child.refreshInstantiatedConstants();

      if (parent != null)
        parent.replaceLeaf(child, this);

      // enable this if ever needed - wasteful if unused.
      //return child.map.keySet().stream().map(s -> Pair.makePair(s, child));
      return Stream.empty();
    });
  }

  private void replaceLeaves(Collection<TruthAssignment> newLeaves, TruthAssignment oldLeaf) {
    leaves.remove(oldLeaf);
    leaves.addAll(newLeaves);
    if (parent != null)
      parent.replaceLeaves(newLeaves, oldLeaf);
  }

  private void replaceLeaf(TruthAssignment newLeaf, TruthAssignment oldLeaf) {
    leaves.remove(oldLeaf);
    leaves.add(newLeaf);
    if (parent != null)
      parent.replaceLeaf(newLeaf, oldLeaf);
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

  public int getNumLeaves() {
    return leaves.size();
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
