package expression.metasentence;

import logicalreasoner.truthassignment.TruthAssignment;
import metareasoner.metainference.MetaInference;
import metareasoner.proof.Proof;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

/**
 * The IFF class represents the metalogical IFF statement
 */
public class IFF extends MetaSentence {
  public IFF(MetaSentence s1, MetaSentence s2, HashSet<TruthAssignmentVar> v) {
    super(new ArrayList<>(Arrays.asList(s1, s2)), "IFF", "IFF", v);
  }

  public MetaInference reasonForwards(Proof p, int inferenceNum) {
    return reason(p, inferenceNum);
  }

  public MetaInference reasonBackwards(Proof p, int inferenceNum) {
    return reason(p, inferenceNum);
  }

  public MetaInference reason(Proof p, int inferenceNum) {
    if (args.get(0) instanceof MODELS && args.get(1) instanceof MODELS) {
      MODELS arg1 = (MODELS) args.get(0),
              arg2 = (MODELS) args.get(1);

      boolean models1 = arg1.isModelled(),
              models2 = arg2.isModelled();

      TruthAssignmentVar t1 = new TruthAssignmentVar(new TruthAssignment()),
              child1 = t1.addChild(new TruthAssignment()),
              child2 = t1.addChild(new TruthAssignment()),
              t2 = new TruthAssignmentVar(new TruthAssignment()),
              child3 = t2.addChild(new TruthAssignment()),
              child4 = t2.addChild(new TruthAssignment());

      MODELS m1 = new MODELS(child1, arg1.getSentence(), !models1, inferenceNum, false),
              m2 = new MODELS(child2, arg2.getSentence(), models2, inferenceNum, false),
              m3 = new MODELS(child3, arg1.getSentence(), models1, inferenceNum, false),
              m4 = new MODELS(child4, arg2.getSentence(), !models2, inferenceNum, false);

      MetaSentence s = new AND(new OR(m1, m2, new HashSet<>(Collections.singletonList(t1))),
              new OR(m4, m3, new HashSet<>(Collections.singletonList(t2))),
              new HashSet<>());

      ArrayList<MetaSentence> a = new ArrayList<>();
      a.add(s);
      return new MetaInference(this, a, inferenceNum, false, symbol);
    }

    return null;
  }
}
