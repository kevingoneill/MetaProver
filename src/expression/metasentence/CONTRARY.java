package expression.metasentence;

import expression.sentence.Sentence;
import logicalreasoner.truthassignment.TruthAssignment;
import metareasoner.metainference.MetaInference;
import metareasoner.proof.Proof;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

/**
 * The CONTRARY class represents the meta-logical property of
 * two Sentences being contrary; that is, they cannot both be
 * true.
 */
public class CONTRARY extends MetaSentence {
  public CONTRARY(Sentence s1, Sentence s2) {
    super(new ArrayList<>(Arrays.asList(s1, s2)), "CONTRARY", "contrary", new HashSet<>());
  }

  public String toSExpression() {
    return "[" + args.get(0).toSExpression() + " and " + args.get(1).toSExpression() + " are " + symbol + "]";
  }

  @Override
  public MetaSentence toplevelCopy(HashSet<TruthAssignmentVar> vars) {
    return this;
  }

  public MetaInference reasonForwards(Proof p, int inferenceNum) {
    return reason(p, inferenceNum);
  }

  public MetaInference reasonBackwards(Proof p, int inferenceNum) {
    return reason(p, inferenceNum);
  }

  public MetaInference reason(Proof p, int inferenceNum) {
    TruthAssignmentVar t = new TruthAssignmentVar(new TruthAssignment()),
            child1 = t.addChild(new TruthAssignment()),
            child2 = t.addChild(new TruthAssignment());

    MODELS m1 = new MODELS(child1, (Sentence) args.get(0), false, inferenceNum, false, true),
            m2 = new MODELS(child2, (Sentence) args.get(1), false, inferenceNum, false, true);

    MetaSentence s = new OR(m1, m2, new HashSet<>(Collections.singletonList(t)));
    ArrayList<MetaSentence> a = new ArrayList<>();
    a.add(s);
    return new MetaInference(this, a, inferenceNum, false, symbol);
  }
}
