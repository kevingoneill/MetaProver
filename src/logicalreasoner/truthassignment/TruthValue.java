package logicalreasoner.truthassignment;

import expression.sentence.Sentence;

import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * The TruthValue class represents the assignment of a Sentence in
 * a given TruthAssignment. This can be a Set of boolean values, each with
 * an associated number which represents the inference which set this TruthValue.
 */
public class TruthValue {
  private HashMap<Boolean, Integer> vals;
  private boolean isDecomposed;
  private Sentence sentence;

  public TruthValue(Sentence s) {
    vals = new HashMap<>();
    isDecomposed = false;
    sentence = s;
  }

  public TruthValue(TruthValue tv) {
    vals = new HashMap<>(tv.vals);
    isDecomposed = false;
    sentence = tv.sentence;
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

  public String toString() {
    //return vals.keySet().toString() + " " + (isDecomposed ? "✓" : "");
    return vals.keySet().stream().map(v -> (v ? "T " : "F ")).collect(Collectors.joining()) + (isDecomposed ? "✓" : "");
  }

  public int hashCode() {
    return vals.hashCode();
  }

  public boolean equals(Object o) {
    if (o instanceof TruthValue) {
      TruthValue tv = (TruthValue) o;
      return isDecomposed == tv.isDecomposed && vals.keySet().equals(tv.vals.keySet());
    }
    return false;
  }


}
