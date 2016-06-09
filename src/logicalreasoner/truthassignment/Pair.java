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
}
