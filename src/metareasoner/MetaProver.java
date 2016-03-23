package metareasoner;

import expression.metasentence.MetaSentence;

import java.util.Set;

/**
 * Created by kevin on 3/22/16.
 */
public class MetaProver {

    private Set<MetaSentence> premises;
    private MetaSentence interest;

    public MetaProver(Set<MetaSentence> p, MetaSentence i) {
        premises = p;
        interest = i;
    }


}
