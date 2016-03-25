package metareasoner;

import expression.Expression;
import expression.metasentence.MetaSentence;
import expression.metasentence.MetaSentenceReader;
import org.junit.Test;

import java.util.ArrayList;

/**
 * MetaProverTest is used to validate the functionality
 * of the MetaProver class
 */
public class MetaProverTest {
    private static void runProver(ArrayList<String> premises, String interest) {
        ArrayList<MetaSentence> p = new ArrayList<MetaSentence>() {{
            premises.forEach(premise -> {
                Expression e = MetaSentenceReader.parse(premise);
                if (e instanceof MetaSentence)
                    this.add((MetaSentence) e);
                else
                    throw new RuntimeException("Cannot input a Sentence into the MetaProver");
            });
        }};

        MetaSentence i = (MetaSentence) MetaSentenceReader.parse(interest);

        MetaProver prover = new MetaProver(p, i);
        prover.run();
        if (!prover.proofFound()) {
            throw new RuntimeException("Proof could not be found!");
        }
    }

    @Test
    public void test1() {
        ArrayList<String> premises = new ArrayList<>();
        premises.add("[EQUIVALENT phi psi]");
        runProver(premises, "[AND [SUBSUMES phi psi] [SUBSUMES psi phi]]");
    }
}
