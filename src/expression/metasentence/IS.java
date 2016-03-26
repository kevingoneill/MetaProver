package expression.metasentence;

import expression.sentence.Sentence;
import logicalreasoner.truthassignment.TruthAssignment;
import metareasoner.metainference.MetaInference;
import metareasoner.proof.Proof;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by kevin on 3/23/16.
 */
public class IS extends MetaSentence {

	private Boolean value;
	
    public IS(Sentence s, MetaConstant c) {
        super(new ArrayList<>(Arrays.asList(s, c)), "IS", "is a", new HashSet<>());
        value = c.getValue();
    }

    public MetaInference reasonForwards(Proof p, int inferenceNum) {
        return reason(p, inferenceNum);
    }

    public MetaInference reasonBackwards(Proof p, int inferenceNum) {
        return reason(p, inferenceNum);
    }
    
    public MetaInference reason(Proof p, int inferenceNum) {
    	TruthAssignmentVar t = new TruthAssignmentVar(new TruthAssignment());
    	MODELS m = new MODELS(t, (Sentence)args.get(0), value, inferenceNum);
    	ArrayList<MetaSentence> a = new ArrayList<>();
        a.add(m);
    	return new MetaInference(this, a, inferenceNum);
    }
}
