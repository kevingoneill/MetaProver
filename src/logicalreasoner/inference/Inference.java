package logicalreasoner.inference;

import expression.sentence.Sentence;
import logicalreasoner.truthassignment.TruthAssignment;

/**
 * The inference stores changes to be made to a parent TruthAssignment.
 */
public abstract class Inference {
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

  public abstract void infer(TruthAssignment h);

  public int hashCode() {
    return origin.hashCode();
  }

  public boolean equals(Object o) {
    if (o instanceof Inference) {
      Inference i = (Inference) o;
      return i.origin.equals(origin) && i.parent.equals(parent);
    }
    return false;
  }

  public String toString() {
    return "Inference " + inferenceNum + " over origin: " + origin;
  }
}
