package expression.metasentence;

import java.util.ArrayList;

/**
 * Created by kevin on 3/22/16.
 */
public class MetaConstant extends MetaSentence {
    public static final MetaConstant TAUTOLOGY = new MetaConstant(true),
            CONTRADICTION = new MetaConstant(false),
        CONTINGENCY = new MetaConstant(null);

    private Boolean value;

    private MetaConstant(Boolean b) {
        super(new ArrayList<>(), Boolean.toString(b), Boolean.toString(b));
        value = b;
    }

}
