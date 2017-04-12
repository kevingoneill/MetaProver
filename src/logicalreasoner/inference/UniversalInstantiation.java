package logicalreasoner.inference;

import expression.sentence.ForAll;
import expression.sentence.Sentence;
import expression.sentence.Variable;
import logicalreasoner.truthassignment.Pair;
import logicalreasoner.truthassignment.TruthAssignment;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * An instantiation is a type of decomposition which
 * instantiates a quantified variable with a Constant, or
 * other sentence
 */
public class UniversalInstantiation extends Inference {

  private List<Sentence> instances;
  private Variable var;

  public UniversalInstantiation(TruthAssignment h, ForAll o, int i, int j, List<Sentence> s, Variable v) {
    super(h, o, i, j);
    //setTrue(o.getSentence().instantiate(s, v));
    instances = s;
    var = v;
  }


  @Override
  public Stream<Pair> infer(TruthAssignment h) {
    inferredOver.clear();
    ForAll f = (ForAll) origin;
    List<Pair> l = instances.stream().flatMap(instance -> {
      List<TruthAssignment> origins = h.getConstantOrigins(instance);
      if (origins.isEmpty())
        return inferHelper(h, f, instance);
      else
        return origins.stream().flatMap(o -> inferHelper(o, f, instance));
    }).collect(Collectors.toList());
    return l.stream();
  }

  private Stream<Pair> inferHelper(TruthAssignment h, ForAll f, Sentence instance) {
    inferredOver.add(h);
    TruthAssignment truthAssignment = new TruthAssignment();
    Sentence s = f.instantiate(instance, var);
    truthAssignment.setTrue(s, inferenceNum);
    truthAssignment.getTruthValue(s).addJustification(inferenceNum, this);
    return h.merge(truthAssignment);
  }

  public List<Sentence> getInstances() {
    return instances.stream().map(i -> origin.instantiate(i, var)).collect(Collectors.toList());
  }

  public List<Sentence> getInstanceVars() {
    return instances;
  }

  public Variable getVar() {
    return var;
  }

  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o instanceof UniversalInstantiation) {
      UniversalInstantiation i = (UniversalInstantiation) o;
      return super.equals(i) && instances.equals(i.instances) && var.equals(i.var);
    }
    return false;
  }

  public String toString() {
    return "UniversalInstantiation " + inferenceNum + "- of " + instances + " over " + origin
            + "=" + parent.models(origin) + " [" + justificationNum + "]";
  }
}
