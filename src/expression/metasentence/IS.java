package expression.metasentence;

import expression.sentence.Sentence;
import logicalreasoner.truthassignment.TruthAssignment;
import metareasoner.metainference.MetaInference;
import metareasoner.proof.Proof;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by kevin on 3/23/16.
 */
public class IS extends MetaSentence {

  public IS(Sentence s, MetaConstant c) {
    super(new ArrayList<>(Arrays.asList(s, c)), "IS", "is a", new HashSet<>());
  }

  public MetaConstant getConstant() {
    return (MetaConstant) args.get(1);
  }

  public Sentence getSentence() {
    return (Sentence) args.get(0);
  }

  public MetaInference reasonForwards(Proof p, int inferenceNum) {
    return reason(p, inferenceNum);
  }

  public MetaInference reasonBackwards(Proof p, int inferenceNum) {
    return reason(p, inferenceNum);
  }

  public MetaInference reason(Proof p, int inferenceNum) {
    ArrayList<MetaSentence> a = new ArrayList<>();
    TruthAssignmentVar t = new TruthAssignmentVar(new TruthAssignment());

    if (getConstant().getValue() == null) {
      TruthAssignmentVar child1 = t.addChild(new TruthAssignment()),
              child2 = t.addChild(new TruthAssignment());

      MODELS m1 = new MODELS(child1, getSentence(), true, inferenceNum, false),
              m2 = new MODELS(child2, getSentence(), false, inferenceNum, false);

      ArrayList<MetaSentence> args = new ArrayList<>();
      args.add(m1);
      args.add(m2);
      HashSet<TruthAssignmentVar> vars = new HashSet<>();
      vars.add(t);

      a.add(new OR(args, vars));
    } else {
      MODELS m = new MODELS(t, getSentence(), getConstant().getValue(), inferenceNum, true);
      a.add(m);
    }

    return new MetaInference(this, a, inferenceNum, false, getConstant().toSExpression());
  }
}
