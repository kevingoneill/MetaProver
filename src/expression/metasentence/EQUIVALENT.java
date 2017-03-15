package expression.metasentence;

import expression.sentence.Iff;
import expression.sentence.Sentence;
import metareasoner.metainference.MetaInference;
import metareasoner.proof.Proof;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by kevin on 3/22/16.
 */
public class EQUIVALENT extends MetaSentence {
  public EQUIVALENT(Sentence e1, Sentence e2) {
    super(new ArrayList<>(Arrays.asList(e1, e2)), "EQUIVALENT", "⟺", new HashSet<>());
  }

  public MetaInference reasonForwards(Proof p, int inferenceNum) {
    return reason(p, inferenceNum);
  }

  public MetaInference reasonBackwards(Proof p, int inferenceNum) {
    return reason(p, inferenceNum);
  }

  @Override
  public MetaSentence toplevelCopy(HashSet<TruthAssignmentVar> vars) {
    return this;
  }

  public MetaInference reason(Proof p, int inferenceNum) {
    Sentence arg1 = (Sentence) args.get(0),
            arg2 = (Sentence) args.get(1);
    Iff iff = new Iff(arg1, arg2);
    MetaSentence s = new IS(iff, MetaConstant.TAUTOLOGY);
    /*
    TruthAssignmentVar t1 = new TruthAssignmentVar(new TruthAssignment()),
            child1 = t1.addChild(new TruthAssignment()),
            child2 = t1.addChild(new TruthAssignment());

    MODELS m1 = new MODELS(child1, arg1, true, inferenceNum, false),
            m2 = new MODELS(child1, arg2, true, inferenceNum, false),
            m3 = new MODELS(child2, arg1, false, inferenceNum, false),
            m4 = new MODELS(child2, arg2, false, inferenceNum, false);

    MetaSentence s = new IFF(m1, m2, new HashSet<>(Collections.singletonList(t1)));
    */


    ArrayList<MetaSentence> a = new ArrayList<>();
    a.add(s);
    return new MetaInference(this, a, inferenceNum, false, symbol);
  }
}
