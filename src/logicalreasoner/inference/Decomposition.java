package logicalreasoner.inference;

import expression.sentence.Sentence;
import logicalreasoner.truthassignment.TruthAssignment;

import java.util.stream.Collectors;

/**
 * A Decomposition is the direct inference of one or more
 * Sentences from another Sentence.
 */
public class Decomposition extends Inference {
  private TruthAssignment additions;

  public Decomposition(TruthAssignment h, Sentence o, int i) {
    super(h, o, i);
    additions = new TruthAssignment(-1);
  }

  @Override
  public void infer(TruthAssignment h) {
    h.merge(additions);
  }

  public void setTrue(Sentence s) {
    additions.setTrue(s, inferenceNum);
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
    return "Decomposition " + inferenceNum + "- origin: " + origin + "=" + parent.models(origin) + " inferences: { "
            + additions.keySet().stream().map(s -> s.toString() + "=" + additions.models(s) + " ").collect(Collectors.joining()) + "}";
  }
}
