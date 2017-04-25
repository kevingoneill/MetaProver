package expression.metasentence;

import expression.sentence.Sentence;
import logicalreasoner.inference.Branch;
import logicalreasoner.inference.Decomposition;
import logicalreasoner.inference.Inference;
import metareasoner.metainference.MetaInference;
import metareasoner.proof.Proof;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.stream.Collectors;

/**
 * The MODELS class represents the meta-logical property of a
 * TruthAssignment setting some Sentence to some Boolean (null
 * if unknown/contingency)
 */
public class MODELS extends MetaSentence {

  Boolean isModelled, isTopLevel;

  public MODELS(TruthAssignmentVar t, Sentence s, Boolean b, int inferenceNum, boolean printQuantifier, boolean setS) {
    super(new ArrayList<>(Arrays.asList(t, s)),
            "models", b == null ? "⊨?" : (b ? "⊨" : "⊭"),
            (printQuantifier ? new HashSet<>(Collections.singletonList(t)) : new HashSet<>()));
    isModelled = b;
    if (setS && b != null && !t.getTruthAssignment().keySet().contains(s)) {
      if (b)
        t.setTrue(s, inferenceNum);
      else
        t.setFalse(s, inferenceNum);
    }
    isTopLevel = printQuantifier;
  }

  public TruthAssignmentVar getTruthAssignmentVar() {
    return (TruthAssignmentVar) args.get(0);
  }

  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o instanceof MODELS) {
      MODELS m = (MODELS) o;
      return m.isModelled == isModelled
              && args.get(1).equals(m.args.get(1));
    }
    return false;
  }

  public String toSExpression() {
    return vars.stream().map(v -> "FORALL " + v.toSExpression()).collect(Collectors.joining()) + (vars.isEmpty() ? "" : " ")
            + args.get(0).toSExpression() + " " + symbol + " " + args.get(1);
  }

  @Override
  public MetaSentence toplevelCopy(HashSet<TruthAssignmentVar> vars) {
    return this;
  }

  public Sentence getSentence() {
    return (Sentence) args.get(1);
  }

  public boolean isModelled() {
    return isModelled;
  }
  public MetaInference reason(Proof p, int inferenceNum) {
    TruthAssignmentVar t = (TruthAssignmentVar) args.get(0);

    if (!t.getTruthAssignment().isConsistent()) {
      ArrayList<MetaSentence> inferences = new ArrayList<>();
      inferences.add(MetaConstant.CONTRADICTION);
      return new MetaInference(this, inferences, inferenceNum, true, getSentence().getSymbol());
    }

    Inference i = t.getNextInference();
    if (i != null) {
      if (i.getOrigin().equals(getSentence())) {
        if (i instanceof Decomposition) {
          Decomposition d = (Decomposition) i;
          ArrayList<MetaSentence> a = new ArrayList<>();
          d.getAdditions().keySet().forEach(s ->
                  a.add(new MODELS(t, s, t.models(s), inferenceNum, isTopLevel, true)));

          return new MetaInference(this, a, inferenceNum, true, d.getOrigin().getSymbol());
        } else if (i instanceof Branch) {
          Branch b = (Branch) i;
          ArrayList<MetaSentence> a = new ArrayList<>();

          b.getBranches().forEach(c -> {
            if (c.keySet().size() == 1)
              c.keySet().forEach(s -> a.add(new MODELS(t.getChild(c), s, c.models(s), inferenceNum, false, true)));
            else if (!c.keySet().isEmpty()) {
              ArrayList<MetaSentence> conjuncts = new ArrayList<>();
              c.keySet().forEach(s -> conjuncts.add(new MODELS(t.getChild(c), s, c.models(s), inferenceNum, false, true)));
              AND and = new AND(conjuncts, new HashSet<>());
              a.add(and);
            }
          });

          OR or = new OR(a, new HashSet<>(Collections.singleton(t)));
          a.clear();
          a.add(or);
          return new MetaInference(this, a, inferenceNum, true, b.getOrigin().getSymbol());
        }
      }
    }

    return null;
  }
}
