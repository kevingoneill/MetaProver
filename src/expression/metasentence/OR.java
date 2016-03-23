package expression.metasentence;

import java.util.ArrayList;

/**
 * Created by kevin on 3/22/16.
 */
public class OR extends MetaSentence {
    public OR(ArrayList<MetaSentence> a) {
        super(new ArrayList<>(a), "OR", "OR");
    }
}
