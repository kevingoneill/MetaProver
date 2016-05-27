package logicalreasoner.prover;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * First Order Logic Tests from Bram's Handout.
 * Problems marked 'a' are all valid, and problems
 * marked 'b' can be either valid or invalid.
 */
public class FirstOrderTests {
  public static void runProver(Set<String> premises, String interest, boolean validArgument) {
    SemanticProverTest.runProver(premises, interest, validArgument);
  }

  @Test
  public void prob1a() {
    HashSet<String> premises = new HashSet<>();
    premises.add("(forAll x (implies (A x) (B x)))");
    premises.add("(forAll x (implies (B x) (C x)))");

    runProver(premises, "(forAll x (implies (A x) (C x)))", true);
  }
}
