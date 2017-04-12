package logicalreasoner.inference;

import expression.sentence.Sentence;
import logicalreasoner.truthassignment.Pair;
import logicalreasoner.truthassignment.TruthAssignment;

import java.util.stream.Stream;

/**
 * Created by kevin on 6/29/16.
 */
public class Closure extends Inference {

  private TruthAssignment parent2;

  public Closure(Sentence s1, TruthAssignment h1, TruthAssignment h2, int i) {
    super(h1, s1, i, -1);
    parent2 = h2;
  }

  public TruthAssignment getParent2() {
    return parent2;
  }

  @Override
  public Stream<Pair> infer(TruthAssignment h) {
    return Stream.empty();
  }

  public String toString() {
    return "Closure of branch " + parent.getName() + " because of conflict with " + parent2.getName() + " over " + origin;
  }
}
