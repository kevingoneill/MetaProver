package expression.metasentence;

import expression.sentence.Sentence;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by kevin on 3/22/16.
 */
public class Models extends MetaSentence {

    public Models(TruthAssignmentVar t, Sentence s) {
        super(new ArrayList<>(Arrays.asList(t, s)), "models", "⊨");
    }

    public String toSymbol() {
        return "∀" + args.get(0).toSymbol() + "[" + args.get(0).toSymbol() + " " + symbol + args.get(1).toSymbol() + "]";
    }
}
