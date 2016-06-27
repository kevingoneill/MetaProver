package logicalreasoner.inference;

import expression.sentence.Sentence;
import logicalreasoner.truthassignment.Pair;
import logicalreasoner.truthassignment.TruthAssignment;

import java.util.stream.Stream;

/**
 * The inference stores changes to be made to a parent TruthAssignment.
 */
public abstract class Inference {
  private Integer HASH_CODE;
  TruthAssignment parent;
  Sentence origin;
  int inferenceNum, justificationNum;

  public Inference(TruthAssignment p, Sentence o, int i, int j) {
    parent = p;
    origin = o;
    inferenceNum = i;
    justificationNum = j;
  }

  public int getInferenceNum() {
    return inferenceNum;
  }

  public int getJustificationNum() { return justificationNum; }

  public TruthAssignment getParent() {
    return parent;
  }

  public Sentence getOrigin() {
    return origin;
  }

  public abstract Stream<Pair> infer(TruthAssignment h);

  public int hashCode() {
    if (HASH_CODE == null) {
      if (origin == null)
        HASH_CODE = parent.hashCode();
      else
        HASH_CODE = origin.hashCode();
    }
    return HASH_CODE;
  }

  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o instanceof Inference) {
      Inference i = (Inference) o;
      return inferenceNum == i.inferenceNum && justificationNum == i.justificationNum && i.origin == origin && i.parent == parent;
    }
    return false;
  }

  public String toString() {
    return "Inference " + inferenceNum + " over origin: " + origin;
  }
}
