package logicalreasoner.truthassignment;

import java.util.HashMap;

/**
 * The TruthValue class represents the assignment of a Sentence in
 * a given TruthAssignment. This can be a Set of boolean values, each with
 * an associated number which represents the inference which set this TruthValue.
 */
public class TruthValue {
  private HashMap<Boolean, Integer> vals;
  private boolean isDecomposed;

  public TruthValue() {
    vals = new HashMap<>();
    isDecomposed = false;
  }

  public TruthValue(TruthValue tv) {
    vals = new HashMap<>(tv.vals);
    isDecomposed = false;
  }
  
  public HashMap<Boolean, Integer> getVals() {
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
    //return vals.toString();
    return vals.keySet().toString() + " " + (isDecomposed ? "✓" : "");
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
