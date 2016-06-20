package logicalreasoner.prover;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by kevin on 6/10/16.
 */
public class OSCARTests {

  private static void runProver(Set<String> p, String s, boolean b) {
    FirstOrderTests.runProver(p, s, b);
  }

  @Test
  public void prob21c() {
    runProver(new HashSet<>(), "(iff (implies p q) (implies (not q) (not p)))", true);
  }

  @Test
  public void prob22c() {
    runProver(new HashSet<>(), "(iff (not (not p)) p)", true);
  }

  @Test
  public void prob23c() {
    runProver(new HashSet<>(), "(implies (not (implies p q)) (implies q p))", true);
  }

  @Test
  public void prob24c() {
    runProver(new HashSet<>(), "(iff (implies (not p) q) (implies (not q) p))", true);
  }

  @Test
  public void prob25c() {
    runProver(new HashSet<>(), "(implies (implies (or p q) (or p r)) (or p (implies q r)))", true);
  }

  @Test
  public void prob26c() {
    runProver(new HashSet<>(), "(or p (not p))", true);
  }

  @Test
  public void prob27c() {
    runProver(new HashSet<>(), "(or p (not (not (not p))))", true);
  }

  @Test
  public void prob28c() {
    runProver(new HashSet<>(), "(implies (implies (implies p q) p) p)", true);
  }

  @Test
  public void prob29c() {
    runProver(new HashSet<>(), "(implies (and (or p q) (or (not p) q) (or p (not q))) (not (or (not p) (not q))))", true);
  }

  @Test
  public void prob30c() {
    Set<String> premises = new HashSet<>();
    premises.add("(implies q r)");
    premises.add("(implies r (and p q))");
    premises.add("(implies p (or q r))");
    runProver(premises, "(iff p q)", true);
  }

  @Test
  public void prob31c() {
    runProver(new HashSet<>(), "(iff p p)", true);
  }

  @Test
  public void prob32c() {
    runProver(new HashSet<>(), "(iff (iff (iff p q) r) (iff p (iff q r)))", true);
  }

  @Test
  public void prob33c() {
    runProver(new HashSet<>(), "(iff (or p (and q r)) (and (or p q) (or p r)))", true);
  }

  @Test
  public void prob34c() {
    runProver(new HashSet<>(), "(iff (iff p q) (and (or q (not p)) (or (not q) p)))", true);
  }

  @Test
  public void prob35c() {
    runProver(new HashSet<>(), "(implies (iff p q) (or (not p) q))", true);
  }

  @Test
  public void prob36c() {
    runProver(new HashSet<>(), "(or (implies p q) (implies q p))", true);
  }

  @Test
  public void prob37c() {
    runProver(new HashSet<>(), "(iff (implies (and p (implies q r)) s) (and (or (not p) q s) (or (not p) (not r) s)))", true);
  }

  @Test
  public void prob41c() {
    Set<String> premises = new HashSet<>();
    premises.add("(forAll x (F x))");
    runProver(premises, "(exists x (F x))", true);
  }

  @Test
  public void prob42c() {
    Set<String> premises = new HashSet<>();
    premises.add("(exists x (forAll y (F x y)))");
    runProver(premises, "(forAll y (exists x (F x y)))", true);
  }

  @Test
  public void prob43c() {
    Set<String> premises = new HashSet<>();
    premises.add("(forAll x (implies (P x) (not (P x))))");
    runProver(premises, "(not (P a))", true);
  }

  @Test
  public void prob44c() {
    Set<String> premises = new HashSet<>();
    premises.add("(forAll x (implies (F x) (and (H x) (not (G x)))))");
    runProver(premises, "(implies (G a) (not (F a)))", true);
  }

  @Test
  public void prob45c() {
    Set<String> premises = new HashSet<>();
    premises.add("(forAll x (implies (H x) (G x)))");
    runProver(premises, "(and (implies (H a) (G a)) (not (and (not (G b)) (H b))))", true);
  }

  @Test
  public void prob46c() {
    Set<String> premises = new HashSet<>();
    premises.add("(forAll x (iff (P x) (and (H x) (not (P x)))))");
    runProver(premises, "(forAll x (not (H x)))", true);
  }

  @Test
  public void prob47c() {
    Set<String> premises = new HashSet<>();
    premises.add("(forAll x (F x))");
    premises.add("(forAll x (implies (F x) (G x)))");
    runProver(premises, "(forAll x (G x))", true);
  }

  @Test
  public void prob48c() {
    Set<String> premises = new HashSet<>();
    premises.add("(F a)");
    runProver(premises, "(exists x (or (F x) (G x)))", true);
  }

  @Test
  public void prob49c() {
    Set<String> premises = new HashSet<>();
    premises.add("(exists x (F x))");
    runProver(premises, "(exists x (or (F x) (G x)))", true);
  }

  @Test
  public void prob50c() {
    Set<String> premises = new HashSet<>();
    premises.add("(exists x (or (F x) (G x)))");
    premises.add("(not (exists x (F x)))");
    runProver(premises, "(exists x (G x))", true);
  }

  @Test
  public void prob51c() {
    Set<String> premises = new HashSet<>();
    premises.add("(implies (exists x (F x)) (forAll y (G y)))");
    runProver(premises, "(forAll x (forAll y (implies (F x) (G y))))", true);
  }

  @Test
  public void prob52c() {
    Set<String> premises = new HashSet<>();
    premises.add("(forAll x (implies (F x) (implies (G x) (H x))))");
    runProver(premises, "(implies (forAll x (implies (F x) (G x))) (forAll x (implies (F x) (H x))))", true);
  }

  @Test
  public void prob53c() {
    Set<String> premises = new HashSet<>();
    premises.add("(forAll x (implies (F x) (exists y (and (F y) (G x y)))))");
    runProver(premises, "(forAll x (implies (F x) (exists y (exists z (and (G x y) (G y z))))))", true);
  }

  @Test
  public void prob54c() {
    Set<String> premises = new HashSet<>();
    premises.add("(forAll x (exists y (R x y)))");
    premises.add("(forAll x (forAll y (implies (R x y) (R y x))))");
    premises.add("(forAll x (forAll y (forAll z (implies (and (R x y) (R y z)) (R x z)))))");
    runProver(premises, "(forAll x (R x x))", true);
  }

  @Test
  public void prob55c() {
    runProver(new HashSet<>(), "(implies (forAll x (F x)) (exists x (F x)))", true);
  }

  @Test
  public void prob56c() {
    runProver(new HashSet<>(), "(exists x (implies (F x) (forAll y (F y))))", true);
  }

  @Test
  public void prob57c() {
    runProver(new HashSet<>(), "(implies (forAll x (forAll y (implies (R x y) (not (R y x))))) (not (exists x (R x x))))", true);
  }

  @Test
  public void prob58c() {
    runProver(new HashSet<>(), "(not (exists x (forAll y (iff (R x y) (not (R y y))))))", true);
  }

  @Test
  public void prob59c() {
    runProver(new HashSet<>(), "(not (forAll x (implies (or (F x) (not (F x))) (not (or (F x) (not (F x)))))))", true);
  }

  @Test
  public void prob60c() {
    runProver(new HashSet<>(), "(iff (exists x (or (F x) (G x))) (or (exists x (F x)) (exists x (G x))))", true);
  }

  @Test
  public void prob61c() {
    runProver(new HashSet<>(), "(iff (forAll x (and (F x) (G x))) (and (forAll x (F x)) (forAll x (G x))))", true);
  }

  @Test
  public void prob62c() {
    runProver(new HashSet<>(), "(implies (forAll x (implies (F x) (G x))) (implies (forAll x (F x)) (forAll x (G x))))", true);
  }

  @Test
  public void prob63c() {
    runProver(new HashSet<>(), "(iff (implies P (forAll x (F x))) (forAll x (implies P (F x))))", true);
  }

  @Test
  public void prob64c() {
    runProver(new HashSet<>(), "(iff (implies P (exists x (F x))) (exists x (implies P (F x))))", true);
  }

  @Test
  public void prob65c() {
    runProver(new HashSet<>(), "(iff (implies (forAll x (F x)) P) (exists x (implies (F x) P)))", true);
  }

  @Test
  public void prob66c() {
    runProver(new HashSet<>(), "(forAll x (or (F x) (not (F x))))", true);
  }

  @Test
  public void prob67c() {
    runProver(new HashSet<>(), "(exists x (or (F x) (not (F x))))", true);
  }

  @Test
  public void prob68c() {
    runProver(new HashSet<>(), "(exists y (iff (F a y) (F y y)))", true);
  }

  @Test
  public void prob69c() {
    runProver(new HashSet<>(), "(forAll x (exists y (iff (F x y) (F y y))))", true);
  }

  @Test
  public void prob70c() {
    runProver(new HashSet<>(), "(exists y (forAll x (implies (F y) (F x))))", true);
  }

  @Test
  public void prob71c() {
    runProver(new HashSet<>(), "(exists x (forAll y (forAll z (implies (implies (P y) (Q z)) (implies (P x) (Q x)) ))))", true);
  }

  //@Test
  public void prob72c() {
    try {
      Thread.sleep(10000);
    } catch (InterruptedException ie) {
    }
    runProver(new HashSet<>(),
            "(implies (forAll x (forAll y (exists z (forAll w (implies (and (P x) (Q y)) (and (R z) (S w))))))) (implies (exists v1 (exists u (and (P v1) (Q u)))) (exists s (R s))))", true);
  }

  @Test
  public void prob73c() {
    Set<String> premises = new HashSet<>();
    premises.add("(exists x (implies p (F x)))");
    premises.add("(exists x (implies (F x) p))");
    runProver(premises, "(exists x (iff p (F x)))", true);
  }

  @Test
  public void prob74c() {
    runProver(new HashSet<>(), "(implies (forAll x (iff p (F x))) (iff p (forAll y (F y))))", true);
  }

  @Test
  public void prob75c() {
    runProver(new HashSet<>(), "(iff (forAll x (or p (F x))) (or p (forAll y (F y))))", true);
  }

  @Test
  public void prob76c() {
    Set<String> premises = new HashSet<>();
    premises.add("(not (exists x (and (S x) (Q x))))");
    premises.add("(forAll x (implies (P x) (or (Q x) (R x))))");
    premises.add("(implies (not (exists x (P x))) (exists y (Q y)))");
    premises.add("(forAll x (implies (or (Q x) (R x)) (S x)))");
    runProver(premises, "(exists x (and (P x) (R x)))", true);
  }

  @Test
  public void prob77c() {
    Set<String> premises = new HashSet<>();
    premises.add("(exists x (P x))");
    premises.add("(forAll x (implies (F x) (and (not (G x)) (R x))))");
    premises.add("(forAll x (implies (P x) (and (G x) (F x))))");
    premises.add("(or (forAll x (implies (P x) (Q x))) (exists y (and (P y) (R y))))");
    runProver(premises, "(exists x (and (Q x) (P x)))", true);
  }

  @Test
  public void prob78c() {
    //try { Thread.sleep(10000); } catch (InterruptedException ie) {}
    Set<String> premises = new HashSet<>();
    premises.add("(iff (exists x (P x)) (exists y (Q y)))");
    premises.add("(forAll x (forAll y (implies (and (P x) (Q y)) (iff (R x) (S y)))))");
    runProver(premises, "(iff (forAll x (implies (P x) (R x))) (forAll y (implies (Q y) (S y))))", true);
  }

  @Test
  public void prob79c() {
    Set<String> premises = new HashSet<>();
    premises.add("(exists x (and (F x) (not (G x))))");
    premises.add("(forAll x (implies (F x) (H x)))");
    premises.add("(forAll x (implies (and (J x) (I x)) (F x)))");
    premises.add("(implies (exists x (and (H x) (not (G x)))) (forAll y (implies (I y) (not (H y)))))");
    runProver(premises, "(forAll x (implies (J x) (not (I x))))", true);
  }

  //@Test
  public void prob80c() {
    Set<String> premises = new HashSet<>();
    premises.add("(forAll x (implies (P x) (forAll y (Q y))))");
    premises.add("(implies (forAll x (or (Q x) (R x))) (exists y (and (Q y) (S y))))");
    premises.add("(forAll x (forAll y (forAll z (implies (and (R x y) (R y z)) (R x z)))))");
    runProver(premises, "(forAll x (R x x))", true);
  }


}
