package logicalreasoner.inference;

import expression.sentence.ForAll;
import expression.sentence.Sentence;
import expression.sentence.Variable;
import logicalreasoner.truthassignment.TruthAssignment;

import java.util.stream.Collectors;

/**
 * An instantiation is a type of decomposition which
 * instantiates a quantified variable with a Constant, or
 * other sentence
 */
public class UniversalInstantiation extends Decomposition {

  Sentence instance;
  Variable var;

  public UniversalInstantiation(TruthAssignment h, ForAll o, int i, int j, Sentence s, Variable v) {
    super(h, o, i, j);
    setTrue(o.getSentence().instantiate(s, v));
    instance = s;
    var = v;
  }

  public boolean equals(Object o) {
    if (o instanceof UniversalInstantiation) {
      UniversalInstantiation i = (UniversalInstantiation) o;
      return super.equals(i) && instance.equals(i.instance) && var.equals(i.var);
    }
    return false;
  }

  public String toString() {
    return "UniversalInstantiation " + inferenceNum + "- of " + instance + " over " + origin
            + "=" + parent.models(origin) + " [" + justificationNum + "] instances: { "
            + additions.keySet().stream().map(s -> s.toString() + "=" + additions.models(s)
            + " [" + additions.getInferenceNum(s, additions.models(s)) + "] ").collect(Collectors.joining()) + "}";
  }
}
