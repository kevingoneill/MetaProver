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

  @Test
  public void prob11a() {
    HashSet<String> premises = new HashSet<>();
    premises.add("(implies (forAll x (A x)) (exists y (B y)))");
    premises.add("(forAll y (not (B y)))");
    runProver(premises, "(not (forAll z (A z)))", true);
  }

  @Test
  public void prob12a() {
    HashSet<String> premises = new HashSet<>();
    premises.add("(forAll x (implies (A x) (implies (exists y (and (B y) (D y))) (C x))))");
    premises.add("(forAll x (implies (B x) (C x)))");
    runProver(premises, "(forAll x (implies (A x) (implies (B x) (C x))))", true);
  }

  @Test
  public void prob13a() {
    HashSet<String> premises = new HashSet<>();
    premises.add("(implies (exists x (R x)) (exists x (S x)))");
    premises.add("(forAll y (implies (T y) (R y)))");
    runProver(premises, "(implies (exists x (T x)) (exists z (S z)))", true);
  }

  @Test
  public void prob14a() {
    HashSet<String> premises = new HashSet<>();
    premises.add("(exists x (forAll y (implies (A x y) (B x y))))");
    premises.add("(forAll x (exists y (not (B x y))))");
    runProver(premises, "(not (forAll x (forAll y (A x y))))", true);
  }

  @Test
  public void prob15a() {
    HashSet<String> premises = new HashSet<>();
    premises.add("(exists x (and (A x) (forAll y (implies (B y) (C x y)))))");
    premises.add("(forAll x (exists y (implies (A x) (B y))))");
    runProver(premises, "(exists x (exists y (C x y)))", true);
  }

  @Test
  public void prob16a() {
    HashSet<String> premises = new HashSet<>();
    premises.add("(or (forAll x (F x)) (forAll x (not (G x))))");
    premises.add("(not (forAll x (F x)))");
    premises.add("(forAll x (implies (D x) (G x)))");
    runProver(premises, "(exists x (not (D x)))", true);
  }

  //@Test
  public void prob17a() {
    HashSet<String> premises = new HashSet<>();
    premises.add("(not (exists x (and (A x a) (not (B x b)))))");
    premises.add("(not (exists x (and (C x c) (C b x))))");
    premises.add("(forAll x (implies (B e x) (C x f)))");
    runProver(premises, "(not (and (A e a) (C f c)))", true);
  }

  @Test
  public void prob18a() {
    HashSet<String> premises = new HashSet<>();
    premises.add("(implies (exists x (A x)) (forAll x (implies (B x) (C x))))");
    premises.add("(implies (exists x (D x)) (exists x (not (C x))))");
    premises.add("(exists y (and (A y) (D y)))");
    runProver(premises, "(exists z (not (B z)))", true);
  }

  @Test
  public void prob19a() {
    HashSet<String> premises = new HashSet<>();
    premises.add("(forAll x (exists y (or (not (F x)) (G y))))");
    runProver(premises, "(forAll x (implies (F x) (exists y (G y))))", true);
  }

  @Test
  public void prob20a() {
    HashSet<String> premises = new HashSet<>();
    premises.add("(forAll x (implies (A x) (H x)))");
    premises.add("(implies (exists x (A x)) (not (exists y (G y))))");
    runProver(premises, "(forAll x (implies (exists y (A y)) (not (G y))))", true);
  }

  @Test
  public void prob21a() {
    HashSet<String> premises = new HashSet<>();
    premises.add("(exists x (and (A x) (forAll y (implies (Q y) (L x y)))))");
    premises.add("(forAll x (implies (A x) (forAll y (implies (I y) (not (L x y))))))");
    runProver(premises, "(forAll x (implies (Q x) (not (I x))))", true);
  }

  @Test
  public void prob22a() {
    HashSet<String> premises = new HashSet<>();
    premises.add("(exists x (forAll y (F x y)))");
    premises.add("(forAll x (forAll y (implies (F y x) (G x y))))");
    runProver(premises, "(forAll y (exists x (G y x)))", true);
  }

  @Test
  public void prob23a() {
    HashSet<String> premises = new HashSet<>();
    premises.add("(implies (forAll x (forAll y (F y x))) (forAll x (exists y (G x y))))");
    premises.add("(exists x (forAll y (not (G x y))))");
    runProver(premises, "(exists x (forAll y (not (F x y))))", true);
  }

  @Test
  public void prob24a() {
    HashSet<String> premises = new HashSet<>();
    premises.add("(exists x (and (F x) (forAll y (implies (G y) (H x y)))))");
    runProver(premises, "(exists x (and (F x) (implies (G a) (H x a))))", true);
  }
}
