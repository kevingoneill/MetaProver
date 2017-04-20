package logicalreasoner.truthassignment;

import expression.Sort;
import expression.sentence.Sentence;
import logicalreasoner.inference.Inference;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The TruthValue class represents the assignment of a Sentence in
 * a given TruthAssignment. This can be a Set of boolean values, each with
 * an associated number which represents the inference which set this TruthValue.
 */
public class TruthValue implements Serializable {
  private HashMap<Boolean, Integer> vals;
  private HashMap<Integer, Inference> justifications;
  private boolean isDecomposed;
  private Sentence sentence;
  private Sort quantifiedSort = null;
  private Set<Sentence> instantiations, uninstantiatedConstants;

  public TruthValue(Sentence s) {
    vals = new HashMap<>();
    isDecomposed = false;
    sentence = s;
    if (s.isQuantifier())
      quantifiedSort = s.getSubSentence(0).getSort();
    instantiations = new HashSet<>();
    uninstantiatedConstants = new HashSet<>();
    justifications = new HashMap<>();
  }

  public TruthValue(TruthValue tv) {
    vals = new HashMap<>(tv.vals);
    isDecomposed = false;
    sentence = tv.sentence;
    quantifiedSort = tv.quantifiedSort;
    instantiations = new HashSet<>();
    uninstantiatedConstants = new HashSet<>();
    justifications = new HashMap<>(tv.justifications);
  }

  public Sentence getSentence() {
    return sentence;
  }

  public HashMap<Boolean, Integer> getValues() {
    return new HashMap<>(vals);
  }

  public void setTrue(int stepNum) {
    vals.putIfAbsent(true, stepNum);
  }

  public void setFalse(int stepNum) {
    vals.putIfAbsent(false, stepNum);
  }

  public void set(Boolean b, int i) {
    vals.putIfAbsent(b, i);
  }

  public boolean isConsistent() {
    return vals.size() != 2;
  }

  public void putAll(TruthValue truthValue) {
    truthValue.vals.forEach(vals::putIfAbsent);
    truthValue.justifications.forEach(justifications::putIfAbsent);
  }

  public boolean containsTrue() {
    return vals.containsKey(true);
  }

  public boolean containsFalse() {
    return vals.containsKey(false);
  }

  public boolean contains(boolean b) {
    return vals.containsKey(b);
  }

  public int getInferenceNum(boolean b) {
    if (vals.containsKey(b))
      return vals.get(b);
    return -1;
  }

  public boolean isModelled() {
    return vals.containsKey(true);
  }

  public void setDecomposed() {
    isDecomposed = true;
  }

  public boolean isDecomposed() {

    return isDecomposed;
  }

  public void addInstantiations(Collection<Sentence> constants) {
    constants.forEach(this::addInstantiation);
  }

  public void addInstantiation(Sentence c) {
    if (!instantiations.contains(c) && c.getSort().isSubSort(quantifiedSort))
      uninstantiatedConstants.add(c);
  }

  public Set<Sentence> getUninstantiatedConstants() {
    return uninstantiatedConstants;
  }

  public Set<Sentence> getInstantiatedConstants() {
    return instantiations;
  }

  public boolean instantiatedAll() {
    return uninstantiatedConstants.isEmpty();
  }

  public void addJustification(int inferenceNum, Inference inference) {
    justifications.put(inferenceNum, inference);
  }

  public Inference getJustification(int inferenceNum) {
    return justifications.get(inferenceNum);
  }

  public Map<Integer, Inference> getJustifications() {
    return justifications;
  }

  public String toString() {
    //return vals.keySet().toString() + " " + (isDecomposed ? "✓" : "");
    return vals.keySet().stream().map(v -> (v ? "T " : "F ")).collect(Collectors.joining()) + (isDecomposed ? "✓" : "");
  }

  public int hashCode() {
    return sentence.hashCode();
  }

  public boolean equals(Object o) {
    if (o instanceof TruthValue) {
      TruthValue tv = (TruthValue) o;
      return isDecomposed == tv.isDecomposed && vals.keySet().equals(tv.vals.keySet());
    }
    return false;
  }


}
