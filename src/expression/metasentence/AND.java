package expression.metasentence;

import java.util.ArrayList;

/**
 * Created by kevin on 3/22/16.
 */
public class AND extends MetaSentence {
    public AND(ArrayList<MetaSentence> a) {
        super(new ArrayList<>(a), "AND", "AND");
    }
}
