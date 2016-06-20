package logicalreasoner.truthassignment;

import expression.sentence.BooleanSentence;
import expression.sentence.ForAll;
import expression.sentence.Sentence;
import gui.truthtreevisualization.TreeBranch;
import gui.truthtreevisualization.TruthTree;

import java.util.*;
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
  private static int truthAssignmentCount = 0;

  private int UID;
  private Map<Sentence, TruthValue> map;     // The explicit Sentence -> Boolean mapping
  private Map<Sentence, TruthAssignment> inheritedMappings;
  private TruthAssignment parent;
  private List<TruthAssignment> children;
  private Set<Sentence> constants;

  /**
   * Create a new, empty TruthAssignment
   */
  public TruthAssignment() {
    UID = truthAssignmentCount++;
    map = new HashMap<>();
    inheritedMappings = new HashMap<>();
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
    inheritedMappings = new HashMap<>();
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
    inheritedMappings = new HashMap<>(ta.inheritedMappings);
    ta.map.forEach((k, v) -> {
      map.put(k.makeCopy(), new TruthValue(v));
      if (k.isAtomic())
        setDecomposed(k);
    });

    this.parent = ta.parent;
    children = new ArrayList<>(ta.children);
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
    UID = ta.UID;
    this.map = new HashMap<>();
    this.inheritedMappings = new HashMap<>(ta.inheritedMappings);
    this.parent = p;
    children = new ArrayList<>(ta.children);
    constants = new HashSet<>(ta.constants);
    parent.children.add(this);
    ta.map.forEach((k, v) -> {
      Sentence s = k.makeCopy();
      v.getValues().forEach((b, i) -> set(s, b, i));
      if (s.isAtomic())
        setDecomposed(s);
    });
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
    StringBuilder builder = new StringBuilder(prefix);
    if (isTail)
      builder.append("└── ").append(toString());
    else
      builder.append("├── ").append(toString());
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

  private void addMappingDownward(Sentence s, TruthAssignment t) {
    children.forEach(c -> {
      c.inheritedMappings.putIfAbsent(s, t);
      c.addMappingDownward(s, t);
    });
  }

  public List<Pair> getInheritedMappings() {
    return inheritedMappings.entrySet().stream().map(e -> new Pair(e.getKey(), e.getValue())).collect(Collectors.toList());
  }

  public Set<Sentence> getConstants() {
    return constants;
  }

  private void addInstantiatedConstants(Collection<Sentence> constants) {
    if (!constants.isEmpty()) {
      map.entrySet().stream().filter(e -> e.getValue().isModelled() && e.getKey() instanceof ForAll)
              .forEach(e -> {
                //System.out.println("ADDING CONSTANTS " + getConstants() + " TO " + e.getKey());
                ((ForAll) e.getKey()).addInstantiations(constants);
              });
      if (parent != null)
        parent.addInstantiatedConstants(constants);
    }
  }

  private void refreshInstantiatedConstants() {
    addInstantiatedConstants(getConstants());
  }

  public boolean hasImmediateConstant(Sentence s) {
    return map.keySet().parallelStream().anyMatch(k -> k.getConstants().contains(s)) || (parent != null && parent.hasImmediateConstant(s));
  }

  private void addConstantDownwards(Sentence c) {
    constants.add(c);
    addInstantiatedConstants(Collections.singletonList(c));
    children.forEach(child -> child.addConstantDownwards(c));
  }

  private void addConstantUpwards(Sentence c) {
    constants.add(c);
    if (parent != null)
      parent.addConstantUpwards(c);
  }

  public void addConstant(Sentence c) {
    constants.add(c);
    //if (parent != null)
    //  parent.addConstantUpwards(c);
    addInstantiatedConstants(Collections.singletonList(c));
    children.forEach(child -> child.addConstantDownwards(c));
  }

  private void addConstantsDownwards(Collection<Sentence> c) {
    if (c.isEmpty())
      return;
    constants.addAll(c);
    addInstantiatedConstants(c);
    children.forEach(child -> child.addConstantsDownwards(c));
  }

  private void addConstantsUpwards(Collection<Sentence> c) {
    if (c.isEmpty())
      return;
    constants.addAll(c);
    addInstantiatedConstants(c);
    if (parent != null)
      parent.addConstantsUpwards(c);
  }

  public void addConstants(Collection<Sentence> c) {
    if (c.isEmpty() || c.stream().allMatch(constants::contains))
      return;
    constants.addAll(c);
    addInstantiatedConstants(c);
    //if (parent != null)
    //  parent.addConstantsUpwards(c);
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
      newBranch.addStatement(s.toSymbol() + " " + map.get(s).getValues().keySet().toString());
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
    if (!hasMapping(s, true)) {
      if (map.keySet().contains(s)) {
        TruthValue v = map.get(s);
        v.setTrue(inferenceNum);
      } else {
        TruthValue t = new TruthValue(s);
        t.setTrue(inferenceNum);
        map.put(s, t);
        addMappingDownward(s, this);
      }
      if (s.isAtomic())
        setDecomposed(s);
      addConstants(s.getConstants());

      if (s instanceof ForAll)
        ((ForAll) s).addInstantiations(getConstants());
    }
  }

  /**
   * Add a new mapping to the TruthAssignment
   *
   * @param s the Sentence to be set to false
   */
  public void setFalse(Sentence s, int inferenceNum) {
    if (!hasMapping(s, false)) {
      if (map.keySet().contains(s)) {
        TruthValue v = map.get(s);
        v.setFalse(inferenceNum);
      } else {
        TruthValue t = new TruthValue(s);
        t.setFalse(inferenceNum);
        map.put(s, t);
        addMappingDownward(s, this);
      }

      if (s.isAtomic())
        setDecomposed(s);
      addConstants(s.getConstants());
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
      if (map.keySet().contains(s)) {
        TruthValue v = map.get(s);
        v.set(b, inferenceNum);
      } else {
        TruthValue t = new TruthValue(s);
        t.set(b, inferenceNum);
        map.put(s, t);
        addMappingDownward(s, this);
      }

      if (s.isAtomic())
        setDecomposed(s);
      addConstants(s.getConstants());

      if (b && s instanceof ForAll)
        ((ForAll) s).addInstantiations(getConstants());
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
    if (map.containsKey(s))
      return map.get(s).isModelled();

    TruthAssignment t = inheritedMappings.get(s);
    return t == null || t.models(s);
  }

  /**
   * Return true if this or a parent of this has a mapping for s
   *
   * @param s the expression.sentence to search for
   * @return true if s is mapped to some value,
   * false otherwise
   */
  public boolean isMapped(Sentence s) {
    //return map.containsKey(s) || parent != null && parent.isMapped(s);
    return map.containsKey(s) || inheritedMappings.containsKey(s);
  }

  /**
   * Return true if this or a parent of this maps s to TruthVale b
   *
   * @param s the sentence to search for
   * @param b the assignment to search for
   * @return true if a mapping from s to b exists, false otherwise
   */
  public boolean hasMapping(Sentence s, boolean b) {
    TruthValue v = map.get(s);
    if (v != null)
      return v.contains(b) || parent != null && parent.hasMapping(s, b);
    return parent != null && parent.hasMapping(s, b);
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
  public Stream<Sentence> getSentencesUpwards() {
    if (parent == null)
      return map.keySet().parallelStream();
    return Stream.concat(parent.getSentencesUpwards(), map.keySet().parallelStream());
  }

  public Stream<Pair> flattenParallel() {
    if (parent == null)
      return map.keySet().parallelStream().map(s -> new Pair(s, this));
    return //Stream.concat(parent.flattenParallel(),
            Stream.concat(inheritedMappings.entrySet().parallelStream().map(e -> new Pair(e.getKey(), e.getValue())),
                    map.keySet().parallelStream().map(s -> new Pair(s, this)));
  }

  public Stream<Pair> flattenSerial() {
    if (parent == null)
      return map.keySet().stream().map(s -> new Pair(s, this));
    return Stream.concat(inheritedMappings.entrySet().stream().map(e -> new Pair(e.getKey(), e.getValue())),
            map.keySet().stream().map(s -> new Pair(s, this)));
  }

  public Stream<Pair> flattenUndecomposedParallel() {
    if (parent == null)
      return map.keySet().parallelStream().filter(s -> !decomposedTest(s)).map(s -> new Pair(s, this));
    return //Stream.concat(parent.flattenParallel(),
            Stream.concat(inheritedMappings.entrySet().parallelStream().map(e -> new Pair(e.getKey(), e.getValue()))
                            .filter(p -> !p.truthAssignment.isDecomposed(p.sentence)),
                    map.keySet().parallelStream().filter(s -> !decomposedTest(s)).map(s -> new Pair(s, this)));
  }

  public Stream<Pair> flattenUndecomposedSerial() {
    if (parent == null)
      return map.keySet().stream().filter(s -> !decomposedTest(s)).map(s -> new Pair(s, this));
    return Stream.concat(inheritedMappings.entrySet().stream().map(e -> new Pair(e.getKey(), e.getValue()))
                    .filter(p -> !p.truthAssignment.isDecomposed(p.sentence)),
            map.keySet().stream().filter(s -> !decomposedTest(s)).map(s -> new Pair(s, this)));
  }


  public Map<Sentence, Boolean> getCounterExample() {
    Map<Sentence, Boolean> s = new HashMap<>();
    map.entrySet().stream().filter(e -> e.getKey().isAtomic()).forEach(e -> s.put(e.getKey(), e.getValue().isModelled()));

    if (parent != null)
      s.putAll(parent.getCounterExample());
    return s;
  }

  /**
   * Add all of the mappings of h into this TruthAssignment
   *
   * @param h the mappings to add to this
   */
  public Stream<Pair> merge(TruthAssignment h) {
    List<Pair> l = h.map.entrySet().stream().flatMap(e -> {
      if (!map.containsKey(e.getKey())) {
        Sentence s = e.getKey().makeCopy();
        TruthValue truthValue = new TruthValue(e.getValue());
        map.put(s, truthValue);
        if (s.isAtomic())
          truthValue.setDecomposed();
        addMappingDownward(s, this);
        return Stream.of(new Pair(s, this));
      } else {
        TruthValue v = map.get(e.getKey());
        v.putAll(e.getValue());
        return Stream.of(new Pair(v.getSentence(), this));
      }
    }).collect(Collectors.toList());

    addConstants(h.getConstants());
    refreshInstantiatedConstants();
    //addInstantiatedConstants(h.getConstants());
    return l.stream();
  }

  public boolean consistencyTest() {
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
      return false;
    // Check for finished Universal Quantifiers
    if (tv.isModelled() && s instanceof ForAll) {
      //int numConstants = getLeaves().stream().mapToInt(c -> c.getConstants().size()).sum();

      return getConstants().size() > 0
              //        && getLeaves().allMatch(l -> l.getConstants().size() > 0 && l.getConstants().stream().allMatch(((ForAll) s).getInstantiations()::contains));
              && ((ForAll) s).instantiatedAll();
      //&& ((ForAll) s).getInstantiations().size() == numConstants;
    }
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
    //return map.keySet().stream().allMatch(this::decomposedTest) && (parent == null || parent.decomposedAll());
    //return flattenUndecomposed().count() == 0;
    return flattenParallel().allMatch(e -> e.truthAssignment.decomposedTest(e.sentence));
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
  public Stream<Pair> addChildren(Collection<TruthAssignment> h) {
    List<Pair> l = h.stream().flatMap(c -> {
      TruthAssignment child = new TruthAssignment(c, this);
      child.addConstantsDownwards(constants);
      child.refreshInstantiatedConstants();
      return child.keySet().stream().flatMap(s -> Stream.of(new Pair(s, this)));
    }).collect(Collectors.toList());
    inheritedMappings.forEach(this::addMappingDownward);
    map.keySet().forEach(s -> addMappingDownward(s, this));
    return l.stream();
  }

  /**
   * Get all descendants of this which have noe children
   *
   * @return the set of leaf TruthAssignments under this
   */
  public Stream<TruthAssignment> getLeaves() {
    if (children.isEmpty())
      return Stream.of(this);
    return children.stream().flatMap(TruthAssignment::getLeaves);
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
      return new ArrayList<>(Collections.singletonList(this));
    //TruthAssignment h = getParentContainingConstant(s);
    //if (h != null)
    //  return new ArrayList<>(Collections.singletonList(h));
    return getChildrenContainingConstant(s);
  }

  public ArrayList<TruthAssignment> getChildrenContainingConstant(Sentence s) {
    ArrayList<TruthAssignment> a = new ArrayList<>();
    for (int i = 0; i < children.size(); ++i) {
      TruthAssignment h = children.get(i);
      if (h.hasImmediateConstant(s))
        a.add(h);
      else //if (h.constants.contains(s))
        a.addAll(h.getChildrenContainingConstant(s));
    }
    return a;
  }

  public boolean isSatisfied() {
    return decomposedAll() && areParentsConsistent();
  }

  public Sentence getKey(Sentence s) {
    TruthValue v = map.get(s);
    if (v == null)
      return null;
    return v.getSentence();
  }

  public boolean containsAll(TruthAssignment h) {
    return h.map.entrySet().stream().allMatch(e -> e.getValue().getValues().entrySet().stream().allMatch(e2 -> hasMapping(e.getKey(), e2.getKey())));
  }
}
