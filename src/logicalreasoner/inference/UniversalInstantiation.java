package logicalreasoner.inference;

import expression.sentence.ForAll;
import expression.sentence.Sentence;
import expression.sentence.Variable;
import logicalreasoner.truthassignment.Pair;
import logicalreasoner.truthassignment.TruthAssignment;

import java.util.ArrayList;
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
    ForAll f = (ForAll) origin;
    List<Pair> l = instances.stream().flatMap(instance -> {
      ArrayList<TruthAssignment> origins = h.getConstantOrigins(instance);
      if (origins.isEmpty()) {
        TruthAssignment truthAssignment = new TruthAssignment();
        truthAssignment.setTrue(f.instantiate(instance, var), inferenceNum);
        return h.merge(truthAssignment);
      } else {
        return origins.stream().flatMap(o -> {
          TruthAssignment truthAssignment = new TruthAssignment();
          truthAssignment.setTrue(f.instantiate(instance, var), inferenceNum);
          return o.merge(truthAssignment);
        });
      }
    }).collect(Collectors.toList());
    return l.stream();
  }

  /*
  public ArrayList<TruthAssignment> getInstanceRoot(Sentence constant, TruthAssignment parent) {
    if (parent.getConstants().contains(constant))
      return new ArrayList<>(Collections.singletonList(parent));


  }
  */

  public List<Sentence> getInstances() {
    return instances.stream().map(i -> origin.instantiate(i, var)).collect(Collectors.toList());
  }

  public Variable getVar() {
    return var;
  }

  public boolean equals(Object o) {
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
