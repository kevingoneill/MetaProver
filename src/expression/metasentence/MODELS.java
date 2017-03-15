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

  public MODELS(TruthAssignmentVar t, Sentence s, Boolean b, int inferenceNum, boolean printQuantifier) {
    super(new ArrayList<>(Arrays.asList(t, s)),
            "models", b == null ? "⊨?" : (b ? "⊨" : "⊭"),
            (printQuantifier ? new HashSet<>(Collections.singletonList(t)) : new HashSet<>()));
    isModelled = b;
    if (b != null && !t.getTruthAssignment().keySet().contains(s)) {
      if (b)
        t.setTrue(s, inferenceNum);
      else
        t.setFalse(s, inferenceNum);
    }
    isTopLevel = printQuantifier;
  }

  public boolean equals(Object o) {
    if (!super.equals(o))
      return false;
    MODELS m = (MODELS) o;
    return m.isModelled == isModelled && m.args.get(1) == args.get(1);
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

  public MetaInference reasonForwards(Proof p, int inferenceNum) {
    return reason(p, inferenceNum);
  }

  public MetaInference reasonBackwards(Proof p, int inferenceNum) {
    return reason(p, inferenceNum);
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
          //d.infer(t.getTruthAssignment());
          //System.out.println("INFERRING " + d.getAdditions());

          ArrayList<MetaSentence> a = new ArrayList<>();
          d.getAdditions().keySet().forEach(s ->
                  a.add(new MODELS(t, s, t.models(s), inferenceNum, isTopLevel)));

          System.out.println("INFERRING: " + a);
          return new MetaInference(this, a, inferenceNum, true, d.getOrigin().getSymbol());
        } else if (i instanceof Branch) {
          Branch b = (Branch) i;
          t.getTruthAssignment().print();
          ArrayList<MetaSentence> a = new ArrayList<>();
          //System.out.println(b.getBranches());
          b.getBranches().forEach(c -> {
            //c.print();
            if (c.keySet().size() == 1)
              c.keySet().forEach(s -> a.add(new MODELS(t.getChild(c), s, c.models(s), inferenceNum, false)));
            else if (!c.keySet().isEmpty()) {
              ArrayList<MetaSentence> conjuncts = new ArrayList<>();
              c.keySet().forEach(s -> conjuncts.add(new MODELS(t.getChild(c), s, c.models(s), inferenceNum, false)));
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
