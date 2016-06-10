package logicalreasoner.truthassignment;

import expression.sentence.Sentence;

/**
 * Created by kevin on 6/9/16.
 */
public class Pair {
  public Sentence sentence;
  public TruthAssignment truthAssignment;

  public Pair(Sentence s, TruthAssignment h) {
    sentence = s;
    truthAssignment = h;
  }

  public boolean equals(Object o) {
    if (o instanceof Pair) {
      Pair p = (Pair) o;
      return sentence.equals(p.sentence) && truthAssignment.equals(p.truthAssignment);
    }
    return false;
  }
}
