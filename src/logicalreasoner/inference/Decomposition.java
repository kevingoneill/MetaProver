package logicalreasoner.inference;

import expression.sentence.Sentence;
import logicalreasoner.truthassignment.Pair;
import logicalreasoner.truthassignment.TruthAssignment;
import logicalreasoner.truthassignment.TruthValue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A Decomposition is the direct inference of one or more
 * Sentences from another Sentence.
 */
public class Decomposition extends Inference {
  protected Map<Sentence, TruthValue> additions;
  protected Set<Sentence> constants;

  public Decomposition(TruthAssignment h, Sentence o, int i, int j) {
    super(h, o, i, j);
    additions = new HashMap<>();
    constants = new HashSet<>();
  }

  @Override
  public Stream<Pair> infer(TruthAssignment h) {
    inferredOver.add(h);
    return h.merge(additions, constants);
  }

  public void setTrue(Sentence s) {
    TruthValue v = additions.get(s);
    if (v == null) {
      v = new TruthValue(s);
      additions.put(s, v);
      constants.addAll(s.getConstants());
    }

    v.setTrue(inferenceNum);
    v.addJustification(inferenceNum, this);
  }

  public void setFalse(Sentence s) {
    TruthValue v = additions.get(s);
    if (v == null) {
      v = new TruthValue(s);
      additions.put(s, v);
      constants.addAll(s.getConstants());
    }

    v.setFalse(inferenceNum);
    v.addJustification(inferenceNum, this);
  }

  public Map<Sentence, TruthValue> getAdditions() {
    return additions;
  }

  public boolean equals(Object o) {
    if (o == this)
      return true;
    if (o instanceof Decomposition) {
      Decomposition i = (Decomposition) o;
      return super.equals(i) && additions.equals(i.additions);
    }
    return false;
  }

  public String toString() {
    return "Decomposition " + inferenceNum + "- origin: " + origin + "=" + parent.models(origin) + " [" + justificationNum + "] inferences: { "
            + additions.keySet().stream().map(s -> s.toString() + "=" + additions.get(s)
            + " [" + inferenceNum + "] ").collect(Collectors.joining()) + "}";
  }
}
