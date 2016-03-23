package expression.metasentence;

import expression.Expression;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by kevin on 3/22/16.
 */
public class Equivalent extends MetaSentence {
    public Equivalent(Expression e1, Expression e2) {
        super(new ArrayList<>(Arrays.asList(e1, e2)),  "EQUIVALENT","⟺");
    }
}
