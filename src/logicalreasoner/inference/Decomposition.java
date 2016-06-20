package logicalreasoner.inference;

import expression.sentence.Sentence;
import logicalreasoner.truthassignment.Pair;
import logicalreasoner.truthassignment.TruthAssignment;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A Decomposition is the direct inference of one or more
 * Sentences from another Sentence.
 */
public class Decomposition extends Inference {
  protected TruthAssignment additions;

  public Decomposition(TruthAssignment h, Sentence o, int i, int j) {
    super(h, o, i, j);
    additions = new TruthAssignment(-1);
  }

  @Override
  public Stream<Pair> infer(TruthAssignment h) {
    return h.merge(additions);
  }

  public void setTrue(Sentence s) {
    additions.setTrue(s, inferenceNum);
  }

  public void setFalse(Sentence s, int i) {
    additions.setFalse(s, i);
  }

  public void setTrue(Sentence s, int i) {
    additions.setTrue(s, i);
  }

  public void setFalse(Sentence s) {
    additions.setFalse(s, inferenceNum);
  }

  public TruthAssignment getAdditions() {
    return additions;
  }

  public boolean equals(Object o) {
    if (o instanceof Decomposition) {
      Decomposition i = (Decomposition) o;
      return super.equals(i) && additions.equals(i.additions);
    }
    return false;
  }

  public String toString() {
    return "Decomposition " + inferenceNum + "- origin: " + origin + "=" + parent.models(origin) + " [" + justificationNum + "] inferences: { "
            + additions.keySet().stream().map(s -> s.toString() + "=" + additions.models(s)
            + " [" + additions.getInferenceNum(s, additions.models(s)) + "] ").collect(Collectors.joining()) + "}";
  }
}
