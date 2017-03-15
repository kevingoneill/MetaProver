package expression.metasentence;

import expression.Expression;
import expression.sentence.Implies;
import expression.sentence.Sentence;
import metareasoner.metainference.MetaInference;
import metareasoner.proof.Proof;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

/**
 * SUBSUMES is the meta-logical equivalent of implies
 */
public class SUBSUMES extends MetaSentence {

  public SUBSUMES(Expression e1, Expression e2) {
    super(new ArrayList<>(Arrays.asList(e1, e2)), "SUBSUMES", "⟹", new HashSet<>());
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
    Implies implies = new Implies((Sentence) args.get(0), (Sentence) args.get(1));
    MetaSentence s = new IS(implies, MetaConstant.TAUTOLOGY);

    /*
    TruthAssignmentVar t = new TruthAssignmentVar(new TruthAssignment()),
            child1 = t.addChild(new TruthAssignment()),
            child2 = t.addChild(new TruthAssignment());

    MODELS m1 = new MODELS(child1, (Sentence) args.get(0), false, inferenceNum, false),
            m2 = new MODELS(child2, (Sentence) args.get(1), true, inferenceNum, false);
    MetaSentence s = new OR(m1, m2, new HashSet<>(Collections.singletonList(t)));
    */
    ArrayList<MetaSentence> a = new ArrayList<>();
    a.add(s);
    return new MetaInference(this, a, inferenceNum, false, symbol);
  }


}
