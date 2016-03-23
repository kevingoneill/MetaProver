package expression.metasentence;

import expression.Expression;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Subsumes is the meta-logical equivalent of implies
 */
public class Subsumes extends MetaSentence {

    public Subsumes(Expression e1, Expression e2) {
        super(new ArrayList<>(Arrays.asList(e1, e2)), "SUBSUMES", "⟹");
    }
}
