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

  @Test
  public void prob2a() {
    HashSet<String> premises = new HashSet<>();
    premises.add("(forAll x (implies (A x) (and (B x) (C x))))");
    premises.add("(forAll x (implies (A x) (not (C x))))");
    runProver(premises, "(not (A a))", true);
  }

  @Test
  public void prob3a() {
    HashSet<String> premises = new HashSet<>();
    premises.add("(forAll x (implies (A x) (forAll y (B x y))))");
    premises.add("(A b)");
    runProver(premises, "(forAll y (B b y))", true);
  }

  @Test
  public void prob4a() {
    HashSet<String> premises = new HashSet<>();
    premises.add("(forAll x (implies (B x) (C x)))");
    premises.add("(exists y (and (A y) (B y)))");
    runProver(premises, "(exists z (and (A z) (C z)))", true);
  }

  @Test
  public void prob5a() {
    HashSet<String> premises = new HashSet<>();
    premises.add("(not (exists z (F z)))");
    runProver(premises, "(implies (F a) (G a))", true);
  }

  @Test
  public void prob6a() {
    HashSet<String> premises = new HashSet<>();
    premises.add("(implies (exists x (A x)) (forAll x (implies (B x) (C x))))");
    premises.add("(and (A c) (B c))");
    runProver(premises, "(C c)", true);
  }


  @Test
  public void prob7a() {
    HashSet<String> premises = new HashSet<>();
    premises.add("(forAll x (implies (B x) (C x)))");
    premises.add("(exists y (not (C y)))");
    runProver(premises, "(exists z (not (B z)))", true);
  }

  @Test
  public void prob8a() {
    HashSet<String> premises = new HashSet<>();
    premises.add("(K a)");
    premises.add("(forAll x (implies (K x) (forAll y (H y))))");
    runProver(premises, "(forAll x (H x))", true);
  }

  @Test
  public void prob9a() {
    HashSet<String> premises = new HashSet<>();
    premises.add("(forAll x (exists y (implies (A x) (B y))))");
    runProver(premises, "(forAll x (implies (A x) (exists y (B y))))", true);
  }

  @Test
  public void prob10a() {
    HashSet<String> premises = new HashSet<>();
    premises.add("(exists x (forAll y (A x y)))");
    runProver(premises, "(exists x (A x a))", true);
  }
}
