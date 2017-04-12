package logicalreasoner.truthassignment;

import expression.sentence.Sentence;

import java.util.HashMap;

/**
 * Created by kevin on 6/9/16.
 */
public class Pair {
  private static HashMap<Sentence, HashMap<TruthAssignment, Pair>> pairs = new HashMap<>();

  public Sentence sentence;
  public TruthAssignment truthAssignment;

  public Pair(Sentence s, TruthAssignment h) {
    sentence = s;
    truthAssignment = h;
  }

  public static Pair makePair(Sentence s, TruthAssignment h) {
    HashMap<TruthAssignment, Pair> map = pairs.get(s);
    if (map != null) {
      Pair p = map.get(h);
      if (p != null)
        return p;
      p = new Pair(s, h);
      map.put(h, p);
      return p;
    }
    map = new HashMap<>();
    Pair p = new Pair(s, h);
    map.put(h, p);
    return p;
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
