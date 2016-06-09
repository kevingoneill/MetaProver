package logicalreasoner.truthassignment;

import expression.sentence.BooleanSentence;
import expression.sentence.ForAll;
import expression.sentence.Sentence;
import gui.truthtreevisualization.TreeBranch;
import gui.truthtreevisualization.TruthTree;

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
  private static int truthAssignmentCount = 0;

  private int UID;
  private Map<Sentence, TruthValue> map;     // The explicit Sentence -> Boolean mapping
  private TruthAssignment parent;
  private List<TruthAssignment> children;
  private Set<Sentence> constants;

  /**
   * Create a new, empty TruthAssignment
   */
  public TruthAssignment() {
    UID = truthAssignmentCount++;
    map = new HashMap<>();
    parent = null;
    children = new ArrayList<>();
    constants = new HashSet<>();
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
    constants = new HashSet<>();
  }

  /**
   * Create a copy of another TruthAssignment, th
   *
   * @param ta the TruthAssignment to copy
   */
  public TruthAssignment(TruthAssignment ta) {
    UID = ta.UID;
    this.map = new HashMap<>();
    ta.map.forEach((k, v) -> {
      map.put(k.makeCopy(), new TruthValue(v));
      if (k.isAtomic())
        setDecomposed(k);
    });
    this.parent = ta.parent;
    children = new ArrayList<>(ta.children);
    constants = new HashSet<>(ta.constants);
  }

  public String getName() {
    return "h" + UID;
  }

  public boolean isEmpty() { return map.isEmpty(); }

  public int hashCode() {
    return map.hashCode();
  }

  public boolean assignmentsEqual(TruthAssignment t) {
    return t.map.equals(map);
  }

  public boolean equals(Object o) {
    if (o instanceof TruthAssignment) {
      TruthAssignment h = (TruthAssignment) o;
      return parent == h.parent && map.equals(h.map) && children.equals(h.children);
    }
    return false;
  }

  public String toString() {
    return map.toString();
  }

  public void print() {
    System.out.println(getName());
    print("", true);
  }

  private void print(String prefix, boolean isTail) {
    System.out.println(prefix + (isTail ? "└── " : "├── ") + toString() + " isConsistent: " + (isConsistent() ? "✓" : "✗") + " decomposedAll: " + (decomposedAll() ? "✓" : "✗"));
    Iterator<TruthAssignment> itr = children.iterator();
    int i = 0;

    while (i < children.size() - 1 && itr.hasNext()) {
      itr.next().print(prefix + (isTail ? "    " : "│   "), false);
      ++i;
    }
    if (itr.hasNext()) {
      itr.next().print(prefix + (isTail ? "    " : "│   "), true);
    }
  }

  public Set<Sentence> getConstants() {
    return constants;
  }

  public boolean hasImmediateConstant(Sentence s) {
    return map.keySet().stream().anyMatch(k -> k.getConstants().contains(s));
  }

  private void addConstantDownwards(Sentence c) {
    constants.add(c);
    children.forEach(child -> child.addConstantDownwards(c));
  }

  private void addConstantUpwards(Sentence c) {
    constants.add(c);
    if (parent != null)
      parent.addConstantUpwards(c);
  }

  public void addConstant(Sentence c) {
    constants.add(c);
    if (parent != null)
      parent.addConstantUpwards(c);
    children.forEach(child -> child.addConstantDownwards(c));
  }

  private void addConstantsDownwards(Collection<Sentence> c) {
    if (c.isEmpty())
      return;
    constants.addAll(c);
    children.forEach(child -> child.addConstantsDownwards(c));
  }

  private void addConstantsUpwards(Collection<Sentence> c) {
    if (c.isEmpty())
      return;
    constants.addAll(c);
    if (parent != null)
      parent.addConstantsUpwards(c);
  }

  public void addConstants(Collection<Sentence> c) {
    if (c.isEmpty() || c.stream().allMatch(constants::contains))
      return;
    constants.addAll(c);
    if (parent != null)
      parent.addConstantsUpwards(c);
    children.forEach(child -> child.addConstantsDownwards(c));
  }

  public TruthTree makeTruthTree() {
    TreeBranch root = makeBranch(map.keySet(), children.isEmpty());
    children.forEach(child -> root.addChild(child.makeTruthTree().getRoot()));
    TruthTree tree = new TruthTree(root);
    return tree;
  }

  private TreeBranch makeBranch(Set<Sentence> sens, boolean isLeaf) {
    TreeBranch newBranch = new TreeBranch();
    sens.forEach(s -> {
//      String prefix = "";
//      if (map.get(s).isConsistent() && map.get(s).containsFalse()) {
//        prefix = "¬";
//      }
      newBranch.addStatement(s.toSymbol() + " " + map.get(s).getVals().keySet().toString());
    });
    if (isLeaf) {
      newBranch.addStatement((isConsistent() ? "✓" : "✗"));
    }
    return newBranch;
  }

  public TruthAssignment getParent() {
    return parent;
  }

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
    if (!map.containsKey(s)) {
      if (parent != null)
        return parent.getInferenceNum(s, models);
      return -1;
    }
    if (models(s) == models)
      return map.get(s).getInferenceNum(models);
    return -1;
  }

  /**
   * Add a new mapping to the TruthAssignment
   *
   * @param s the Sentence to be set to true
   */
  public void setTrue(Sentence s, int inferenceNum) {
    if (map.keySet().contains(s))
      map.get(s).setTrue(inferenceNum);
    else {
      TruthValue t = new TruthValue();
      t.setTrue(inferenceNum);
      map.put(s, t);
    }

    if (s.isAtomic())
      setDecomposed(s);
    addConstants(s.getConstants());
  }

  /**
   * Add a new mapping to the TruthAssignment
   *
   * @param s the Sentence to be set to false
   */
  public void setFalse(Sentence s, int inferenceNum) {
    if (map.keySet().contains(s))
      map.get(s).setFalse(inferenceNum);
    else {
      TruthValue t = new TruthValue();
      t.setFalse(inferenceNum);
      map.put(s, t);
    }

    if (s.isAtomic())
      setDecomposed(s);
    addConstants(s.getConstants());
  }

  /**
   * Add a new mapping to the TruthAssignment
   *
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

    if (s.isAtomic())
      setDecomposed(s);
    addConstants(s.getConstants());
  }

  /**
   * Return true if this |= s (or a parent of this |= s)
   *
   * @param s the expression.sentence to be tested for
   * @return true if this |= s, false if this |= (not s),
   * null if not found
   */
  public Boolean models(Sentence s) {
    if (map.containsKey(s))
      return map.get(s).isModelled();
    if (parent != null)
      return parent.models(s);
    return false;
  }

  /**
   * Return true if this or a parent of this has a mapping for s
   *
   * @param s the expression.sentence to search for
   * @return true if s is mapped to some value,
   * false otherwise
   */
  public boolean isMapped(Sentence s) {
    return map.containsKey(s) || parent != null && parent.isMapped(s);
    //return flattenUpwards().anyMatch(s::equals);
  }

  /**
   * @return all Sentences mapped in this TruthAssignment only
   */
  public Set<Sentence> keySet() {
    return map.keySet();
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

    Set<Sentence> s = new HashSet<>(map.keySet());
    s.addAll(parent.getSentencesUpwards());
    return s;
  }

  private Stream<Sentence> flattenUpwards() {
    if (parent == null)
      return map.keySet().stream();
    return Stream.concat(map.keySet().stream(), parent.flattenUpwards());
  }

  private Stream<Sentence> flattenDownwards() {
    if (children.isEmpty())
      return map.keySet().stream();
    return Stream.concat(map.keySet().stream(), children.parallelStream().flatMap(TruthAssignment::flattenDownwards));
  }

  public Stream<Sentence> flatten() {
    if (parent == null) {
      if (children.isEmpty())
        return map.keySet().stream();
      return Stream.concat(map.keySet().stream(), children.stream().flatMap(TruthAssignment::flattenDownwards));
    }
    if (children.isEmpty())
      return Stream.concat(parent.flattenUpwards(), map.keySet().stream());
    return Stream.concat(parent.flattenUpwards(), Stream.concat(map.keySet().stream(), children.stream().flatMap(TruthAssignment::flattenDownwards)));
  }

  public Map<Sentence, TruthAssignment> flatten2() {
    Map<Sentence, TruthAssignment> m = new HashMap<>();
    if (parent != null)
      m.putAll(parent.flatten2());
    map.keySet().forEach(k -> {
      if (!decomposedTest(k))
        m.put(k, this);
    });
    return m;
  }

  public Stream<Pair> flatten3() {
    if (parent == null)
      return map.keySet().parallelStream().filter(s -> !decomposedTest(s)).map(s -> new Pair(s, this));
    return Stream.concat(parent.flatten3(), map.keySet().parallelStream().filter(s -> !decomposedTest(s)).map(s -> new Pair(s, this)));
  }

  public Map<Sentence, TruthAssignment> getUnfinishedQuantifiersUpwards() {
    Map<Sentence, TruthAssignment> m = new HashMap<>();
    map.forEach((k, v) -> {
      if (k.isQuantifier() && !decomposedTest(k))
        m.put(k, this);
    });
    if (parent != null)
      m.putAll(parent.getUnfinishedQuantifiersUpwards());
    return m;
  }

  public Map<Sentence, Boolean> getCounterExample() {
    Map<Sentence, Boolean> s = new HashMap<>();
    map.entrySet().stream().filter(e -> e.getKey().isAtomic()).forEach(e -> s.put(e.getKey(), e.getValue().isModelled()));

    if (parent != null)
      s.putAll(parent.getCounterExample());
    return s;
  }

  /**
   * Get a Stream of all true Sentences in this
   * @return a Stream of all true Sentences in this
   */
  public Stream<Sentence> getTrueSentences() {
    return flatten().filter(this::models);
  }

  /**
   * Get a Stream of all false Sentences in this
   * @return a Stream of all false Sentences in this
   */
  public Stream<Sentence> getFalseSentences() {
    return flatten().filter(sentence -> !models(sentence));
  }

  /**
   * Add all of the mappings of h into this TruthAssignment
   *
   * @param h the mappings to add to this
   */
  public void merge(TruthAssignment h) {
    h.map.forEach((k, v) -> {
      if (!map.containsKey(k)) {
        map.put(k.makeCopy(), new TruthValue(v));
        if (k.isAtomic())
          setDecomposed(k);
      }
      else
        map.get(k).putAll(v);
    });
    addConstants(h.getConstants());
  }

  private boolean consistencyTest() {
    return map.keySet().stream().allMatch(s -> {
      if (!map.get(s).isConsistent())  // Check if the Sentence has multiple values in THIS TruthAssignment
        return false;
      else if (s instanceof BooleanSentence && map.get(s).isModelled() != s.eval(this))
        return false;
      return parent == null || !parent.isMapped(s) || parent.models(s) == models(s);
    });
  }

  /**
   * Make sure the mappings of Sentences is the same as their evaluation
   * under this TruthAssignment
   *
   * @return true if all mappings are consistent, false otherwise
   */
  public boolean isConsistent() {
    return consistencyTest() && (children.isEmpty() || children.parallelStream().anyMatch(TruthAssignment::isConsistentSerial));
  }

  private boolean isConsistentSerial() {
    return consistencyTest() && (children.isEmpty() || children.stream().anyMatch(TruthAssignment::isConsistentSerial));
  }

  public boolean areParentsConsistent() {
    return consistencyTest() && (parent == null || parent.areParentsConsistent());
  }

  /**
   * Mark this expression.sentence as having been decomposed under this TruthAssignment
   *
   * @param s the expression.sentence which has been decomposed
   */
  public void setDecomposed(Sentence s) {
    if (map.containsKey(s))
      map.get(s).setDecomposed();
    else if (parent != null)
      parent.setDecomposed(s);
  }

  private Boolean decomposedTest(Sentence s) {
    TruthValue tv = map.get(s);
    if (tv == null)
      return null;
    // Check for finished Universal Quantifiers
    if (tv.isModelled() && s instanceof ForAll)
      return getConstants().size() > 0 && ((ForAll) s).getInstantiations().size() == getConstants().size();
    return tv.isDecomposed();
  }

  /**
   * Check if this Sentence has already been reasoned upon.
   *
   * @param s the Sentence to check
   * @return true if s has been decomposed in this or a child of this,
   * false otherwise
   */
  public Boolean isDecomposed(Sentence s) {
    if (map.containsKey(s))
      return decomposedTest(s);
    if (parent != null)
      return parent.isDecomposed(s);
    return false;
  }

  /**
   * Check if all mappings in this TruthAssignment have been decomposed
   *
   * @return true if all Sentences in this and its parents have been decomposed
   */
  public boolean decomposedAll() {
    return map.keySet().stream().allMatch(this::decomposedTest) && (parent == null || parent.decomposedAll());
    //return flattenUndecomposed().count() == 0;
  }

  /**
   * Check if all mappings in this TruthAssignment have been decomposed
   *
   * @return true if all Sentences in this and its parents have been decomposed
   */
  public boolean decomposedAllPropositions() {
    return map.keySet().stream().filter(s -> !s.isQuantifier()).allMatch(this::decomposedTest) && (parent == null || parent.decomposedAllPropositions());
    //return flatten().filter(s -> !s.isQuantifier()).allMatch(this::isDecomposed);
  }

  /**
   * Get the child TruthAssignments of this TruthAssignment
   *
   * @return a set of all children
   */
  public List<TruthAssignment> getChildren() {
    return children;
  }

  /**
   * Recursively add children to all leaf nodes
   *
   * @param h the children of the leaves of this to add
   */
  public void addChildren(Collection<TruthAssignment> h) {
    h.forEach(c -> {
      TruthAssignment child = new TruthAssignment(c);
      children.add(child);
      child.setParent(this);
      addConstantsUpwards(child.getConstants());
      child.addConstantsDownwards(constants);
    });
  }

  /**
   * Get all descendants of this which have noe children
   *
   * @return the set of leaf TruthAssignments under this
   */
  public Set<TruthAssignment> getLeaves() {
    if (children.isEmpty()) {
      HashSet<TruthAssignment> h = new HashSet<>();
      h.add(this);
      return h;
    }
    return children.stream().flatMap(s -> s.getLeaves().stream()).collect(Collectors.toSet());
  }

  /**
   * Get the top-level TruthAssignment in this tree
   *
   * @return
   */
  public TruthAssignment getRoot() {
    if (parent == null)
      return this;

    return parent.getRoot();
  }

  public TruthAssignment getParentContaining(Sentence s) {
    if (map.containsKey(s))
      return this;

    if (parent != null)
      return parent.getParentContaining(s);
    return null;
  }

  public TruthAssignment getParentContainingConstant(Sentence s) {
    if (hasImmediateConstant(s))
      return this;
    if (parent != null)
      return parent.getParentContainingConstant(s);
    return null;
  }

  public ArrayList<TruthAssignment> getConstantOrigins(Sentence s) {
    if (hasImmediateConstant(s))
      return new ArrayList<>(Arrays.asList(this));
    TruthAssignment h = getParentContainingConstant(s);
    if (h != null)
      return new ArrayList<>(Arrays.asList(h));
    return getChildrenContainingConstant(s);
  }

  public ArrayList<TruthAssignment> getChildrenContainingConstant(Sentence s) {
    ArrayList<TruthAssignment> a = new ArrayList<>();
    for (int i = 0; i < children.size(); ++i) {
      TruthAssignment h = children.get(i);
      if (h.hasImmediateConstant(s))
        a.add(h);
      else if (h.constants.contains(s))
        a.addAll(h.getChildrenContainingConstant(s));
    }
    return a;
  }

  public boolean isSatisfied() {
    return decomposedAll() && areParentsConsistent();
  }
}
