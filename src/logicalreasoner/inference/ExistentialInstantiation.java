package logicalreasoner.inference;

import expression.sentence.Exists;
import expression.sentence.Sentence;
import expression.sentence.Variable;
import logicalreasoner.truthassignment.TruthAssignment;

/**
 * Created by kevin on 7/18/16.
 */
public class ExistentialInstantiation extends Decomposition {
  private Sentence instance;
  private Variable var;

  public ExistentialInstantiation(TruthAssignment h, Exists e, int i, int j, Sentence s, Variable v) {
    super(h, e, i, j);
    setTrue(e.getSentence().instantiate(s, v));
    instance = s;
    var = v;
  }

  public Sentence getInstance() {
    return origin.instantiate(instance, var);
  }

  public Variable getVar() {
    return var;
  }

  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o instanceof ExistentialInstantiation) {
      ExistentialInstantiation i = (ExistentialInstantiation) o;
      return super.equals(i) && instance.equals(i.instance) && var.equals(i.var);
    }
    return false;
  }

  public String toString() {
    return "ExistentialInstantiation " + inferenceNum + "- of " + instance + " over " + origin
            + "=" + parent.models(origin) + " [" + justificationNum + "]";
  }
}
