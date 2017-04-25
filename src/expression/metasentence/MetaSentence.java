package expression.metasentence;

import expression.Expression;
import expression.sentence.Sentence;
import metareasoner.metainference.MetaInference;
import metareasoner.proof.Proof;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

/**
 * The MetaSentence represents a metalogical expression
 */
public abstract class MetaSentence extends Expression {
  protected ArrayList<Expression> args;
  protected HashSet<TruthAssignmentVar> vars; // All of the nested TruthAssignmentVars which need to be printed at this level as a quantifier

  protected MetaSentence(ArrayList<Expression> a, String n, String s, HashSet<TruthAssignmentVar> v) {
    super(n, s);
    args = a;
    vars = v;
  }

  public abstract MetaInference reason(Proof p, int inferenceNum);

  public MetaInference reasonContained(Proof p, int inferenceNum) {
    return reason(p, inferenceNum);
  }

  public int hashCode() {
    return toString().hashCode();
  }

  public Set<TruthAssignmentVar> getVars() {
    return vars;
  }

  public boolean equals(Object o) {
    if (o instanceof MetaSentence) {
      MetaSentence s = (MetaSentence) o;
      if (!s.name.equals(name) || s.args.size() != args.size())
        return false;

      for (int i = 0; i < args.size(); ++i) {
        if (!args.get(i).equals(s.args.get(i)))
          return false;
      }

      return true;
    }
    return false;
  }

  public String toString() {
    return toSExpression();
  }

  public String toSExpression() {
    if (!args.isEmpty()) {
      StringBuilder builder = new StringBuilder();

      vars.forEach(v -> builder.append("FORALL ").append(v.toSExpression()));
      if (!vars.isEmpty())
        builder.append(" ");

      builder.append("[");
      for (int i = 0; i < args.size() - 1; ++i) {
        if (args.get(i) instanceof Sentence)
          builder.append(args.get(i)).append(" ").append(symbol).append(" ");
        else
          builder.append(args.get(i).toSExpression()).append(" ").append(symbol).append(" ");
      }

      if (args.get(args.size() - 1) instanceof Sentence)
        builder.append(args.get(args.size() - 1)).append("]");
      else
        builder.append(args.get(args.size() - 1).toSExpression()).append("]");

      return builder.toString();
    }
    return symbol;
  }

  public abstract MetaSentence toplevelCopy(HashSet<TruthAssignmentVar> vars);
}
