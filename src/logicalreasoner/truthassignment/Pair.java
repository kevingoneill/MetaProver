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
      return sentence == p.sentence && truthAssignment == p.truthAssignment;
    }
    return false;
  }

  public int hashCode() {
    return sentence.hashCode();
  }

  public String toString() {
    return sentence.toString() + " : " + truthAssignment.getName();
  }
}
