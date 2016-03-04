package truthfunction;

import sentence.Sentence;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Created by kevin on 3/2/16.
 */
public class TruthFunction {
    private Map<String, Boolean> map;

    public TruthFunction() {
        map = new HashMap<>();
    }

    public TruthFunction(TruthFunction th) {
        this.map = new HashMap<>(th.map);
    }

    public void setTrue(Sentence e) {
        map.put(e.toString(), true);
    }

    public void setFalse(Sentence e) {
        map.put(e.toString(), false);
    }

    public void set(Sentence e, boolean b) { map.put(e.toString(), b); }

    public Boolean models(Sentence e) {
        return map.get(e.toString());
    }

    public boolean isMapped(Sentence e) {
        return map.containsKey(e);
    }

    public Stream<String> getModelledSentences() {
        return map.keySet().stream().filter(sentence -> map.get(sentence));
    }
}
