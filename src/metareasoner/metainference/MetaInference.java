package metareasoner.metainference;

import expression.metasentence.MetaSentence;
import metareasoner.proof.Proof;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by kevin on 3/23/16.
 */
public class MetaInference {

  private MetaSentence origin;
  private ArrayList<MetaSentence> inferences;
  private int uid;
  private boolean isSemantic;
  private String symbol;

  public MetaInference(MetaSentence s, ArrayList<MetaSentence> inferences, int i, boolean b, String sym) {
    origin = s;
    this.inferences = inferences;
    uid = i;
    isSemantic = b;
    symbol = sym;
  }

  public int getInferenceNum() {
    return uid;
  }

  public MetaSentence getOrigin() {
    return origin;
  }

  public void infer(Proof p, boolean isForwardsInference) {
    if (isForwardsInference)
      inferences.forEach(i -> p.addForwardsInference(i, this));
    else
      inferences.forEach(i -> p.addBackwardsInference(i, this));
  }

  public ArrayList<MetaSentence> getInferences() {
    return inferences;
  }

  public int hashCode() {
    return origin.hashCode();
  }

  public List<MetaInference> reverse() {
    return inferences.stream().map(i ->
            new MetaInference(i, new ArrayList<MetaSentence>(Collections.singletonList(origin)),
                    uid, isSemantic, symbol))
            .collect(Collectors.toList());
  }

  public boolean equals(Object o) {
    if (o instanceof MetaInference) {
      MetaInference i = (MetaInference) o;
      return i.origin.equals(origin);
    }
    return false;
  }

  public String toString() {
    return "Inference " + uid + " over origin: " + origin.toSExpression() + " inferences: { "
            + inferences.stream().map(s -> s.toSExpression() + " ").collect(Collectors.joining()) + "}";
  }

  public String getReason() {
    if (isSemantic)
      return "(sem. of " + symbol + ")";
    return "(def. of " + symbol + ")";
  }

  public String getSymbol() {
    return symbol;
  }
}
