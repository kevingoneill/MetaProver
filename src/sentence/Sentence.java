package sentence;

import inference.Inference;
import truthfunction.TruthFunction;

import java.util.ArrayList;

/**
 * Created by kevin on 3/2/16.
 */
public abstract class Sentence {
    protected ArrayList<Sentence> args;
    protected String name;

    protected Sentence(ArrayList<Sentence> a, String n) {
        args = new ArrayList<>(a);
        name = n;
    }

    public abstract Boolean eval(TruthFunction h);

    public abstract Inference reasonForwards(TruthFunction h);
    public abstract Inference reasonBackwards(TruthFunction h);

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("(").append(name);
        args.forEach(arg -> builder.append(" ").append(arg));
        builder.append(")");
        return builder.toString();
    }

    public int hashCode() { return toString().hashCode(); }

    public boolean equals(Object o) {
        if (o instanceof Sentence) {
            Sentence s = (Sentence)o;
            if (!s.name.equals(name) || s.args.size() != args.size())
                return false;

            for (int i = 0; i < args.size(); ++i) {
                if (!args.get(i).equals(s.args.get(i)))
                    return false;
            }

            return true;
        }
        return false;
    }
}
