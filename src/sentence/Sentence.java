package sentence;

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

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("(").append(name);
        args.forEach(arg -> builder.append(" ").append(arg));
        builder.append(")");
        return builder.toString();
    }

    public int hashCode() { return toString().hashCode(); }
}
