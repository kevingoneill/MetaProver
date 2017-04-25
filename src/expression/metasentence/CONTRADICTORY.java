package expression.metasentence;

import expression.sentence.Iff;
import expression.sentence.Sentence;
import logicalreasoner.truthassignment.TruthAssignment;
import metareasoner.metainference.MetaInference;
import metareasoner.proof.Proof;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

/**
 * The CONTRADICTORY MetaSentence expresses the meta-logical
 * notion of two logical statements being contradictory to each
 * other.
 */
public class CONTRADICTORY extends MetaSentence {
  public CONTRADICTORY(Sentence s1, Sentence s2) {
    super(new ArrayList<>(Arrays.asList(s1, s2)), "CONTRADICTORY", "contradictory", new HashSet<>());
  }

  public String toSExpression() {
    return "[" + args.get(0) + " and " + args.get(1) + " are " + symbol + "]";
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

    TruthAssignmentVar t1 = new TruthAssignmentVar(new TruthAssignment());
    MODELS m1 = new MODELS(t1, (Sentence) args.get(0), true, inferenceNum, false, false),
            m2 = new MODELS(t1, (Sentence) args.get(1), false, inferenceNum, false, false);

    MetaSentence s = new IFF(m1, m2, new HashSet<>(Collections.singletonList(t1)));

    //MetaSentence s = new IS(Sentence.makeSentence("iff", Arrays.asList((Sentence) args.get(0), (Sentence) args.get(1))), MetaConstant.CONTRADICTION);
    //MetaSentence s2 = new IS(new And(new Not((Sentence) args.get(0)), new Not((Sentence) args.get(1))), MetaConstant.CONTRADICTION);
    ArrayList<MetaSentence> a = new ArrayList<>();
    a.add(s);
    //a.add(s2);
    //MetaSentence and = new AND(a, vars);
    //a.clear();
    return new MetaInference(this, a, inferenceNum, false, symbol);
  }
}
