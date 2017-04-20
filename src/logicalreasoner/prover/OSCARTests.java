package logicalreasoner.prover;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by kevin on 6/10/16.
 */
public class OSCARTests {

  private static void runProver(List<String> d, Set<String> p, String s, boolean b) {
    FOLTests.runProver(d, p, s, b);
  }

  @Test
  public void prob21c() {
    List<String> declarations = new ArrayList<>();
    declarations.add("Boolean P");
    declarations.add("Boolean Q");
    runProver(declarations, new HashSet<>(), "(iff (implies P Q) (implies (not Q) (not P)))", true);
  }

  @Test
  public void prob22c() {
    List<String> declarations = new ArrayList<>();
    declarations.add("Boolean P");
    runProver(declarations, new HashSet<>(), "(iff (not (not P)) P)", true);
  }

  @Test
  public void prob23c() {
    List<String> declarations = new ArrayList<>();
    declarations.add("Boolean P");
    declarations.add("Boolean Q");
    runProver(declarations, new HashSet<>(), "(implies (not (implies P Q)) (implies Q P))", true);
  }

  @Test
  public void prob24c() {
    List<String> declarations = new ArrayList<>();
    declarations.add("Boolean P");
    declarations.add("Boolean Q");
    runProver(declarations, new HashSet<>(), "(iff (implies (not P) Q) (implies (not Q) P))", true);
  }

  @Test
  public void prob25c() {
    List<String> declarations = new ArrayList<>();
    declarations.add("Boolean P");
    declarations.add("Boolean Q");
    declarations.add("Boolean R");

    runProver(declarations, new HashSet<>(), "(implies (implies (or P Q) (or P R)) (or P (implies Q R)))", true);
  }

  @Test
  public void prob26c() {
    List<String> declarations = new ArrayList<>();
    declarations.add("Boolean P");
    runProver(declarations, new HashSet<>(), "(or P (not P))", true);
  }

  @Test
  public void prob27c() {
    List<String> declarations = new ArrayList<>();
    declarations.add("Boolean P");
    runProver(declarations, new HashSet<>(), "(or P (not (not (not P))))", true);
  }

  @Test
  public void prob28c() {
    List<String> declarations = new ArrayList<>();
    declarations.add("Boolean P");
    declarations.add("Boolean Q");
    runProver(declarations, new HashSet<>(), "(implies (implies (implies P Q) P) P)", true);
  }

  @Test
  public void prob29c() {
    List<String> declarations = new ArrayList<>();
    declarations.add("Boolean P");
    declarations.add("Boolean Q");
    runProver(declarations, new HashSet<>(), "(implies (and (or P Q) (or (not P) Q) (or P (not Q))) (not (or (not P) (not Q))))", true);
  }

  @Test
  public void prob30c() {
    List<String> declarations = new ArrayList<>();
    Set<String> premises = new HashSet<>();
    declarations.add("Boolean P");
    declarations.add("Boolean Q");
    declarations.add("Boolean R");

    premises.add("(implies Q R)");
    premises.add("(implies R (and P Q))");
    premises.add("(implies P (or Q R))");
    runProver(declarations, premises, "(iff P Q)", true);
  }

  @Test
  public void prob31c() {
    List<String> declarations = new ArrayList<>();
    declarations.add("Boolean P");
    runProver(declarations, new HashSet<>(), "(iff P P)", true);
  }

  @Test
  public void prob32c() {
    List<String> declarations = new ArrayList<>();
    declarations.add("Boolean P");
    declarations.add("Boolean Q");
    declarations.add("Boolean R");
    runProver(declarations, new HashSet<>(), "(iff (iff (iff P Q) R) (iff P (iff Q R)))", true);
  }

  @Test
  public void prob33c() {
    List<String> declarations = new ArrayList<>();
    declarations.add("Boolean P");
    declarations.add("Boolean Q");
    declarations.add("Boolean R");
    runProver(declarations, new HashSet<>(), "(iff (or P (and Q R)) (and (or P Q) (or P R)))", true);
  }

  @Test
  public void prob34c() {
    List<String> declarations = new ArrayList<>();
    declarations.add("Boolean P");
    declarations.add("Boolean Q");
    runProver(declarations, new HashSet<>(), "(iff (iff P Q) (and (or Q (not P)) (or (not Q) P)))", true);
  }

  @Test
  public void prob35c() {
    List<String> declarations = new ArrayList<>();
    declarations.add("Boolean P");
    declarations.add("Boolean Q");
    runProver(declarations, new HashSet<>(), "(implies (iff P Q) (or (not P) Q))", true);
  }

  @Test
  public void prob36c() {
    List<String> declarations = new ArrayList<>();
    declarations.add("Boolean P");
    declarations.add("Boolean Q");
    runProver(declarations, new HashSet<>(), "(or (implies P Q) (implies Q P))", true);
  }

  @Test
  public void prob37c() {
    List<String> declarations = new ArrayList<>();
    declarations.add("Boolean P");
    declarations.add("Boolean Q");
    declarations.add("Boolean R");
    declarations.add("Boolean S");
    runProver(declarations, new HashSet<>(), "(iff (implies (and P (implies Q R)) S) (and (or (not P) Q S) (or (not P) (not R) S)))", true);
  }

  @Test
  public void prob41c() {
    List<String> declarations = new ArrayList<>();
    Set<String> premises = new HashSet<>();
    declarations.add("Boolean F Object");

    premises.add("(forAll x (F x))");
    runProver(declarations, premises, "(exists x (F x))", true);
  }

  @Test
  public void prob42c() {
    List<String> declarations = new ArrayList<>();
    Set<String> premises = new HashSet<>();
    declarations.add("Boolean F Object Object");
    premises.add("(exists x (forAll y (F x y)))");
    runProver(declarations, premises, "(forAll y (exists x (F x y)))", true);
  }

  @Test
  public void prob43c() {
    List<String> declarations = new ArrayList<>();
    Set<String> premises = new HashSet<>();
    declarations.add("Boolean P Object");
    declarations.add("Object a");
    premises.add("(forAll x (implies (P x) (not (P x))))");
    runProver(declarations, premises, "(not (P a))", true);
  }

  @Test
  public void prob44c() {
    List<String> declarations = new ArrayList<>();
    Set<String> premises = new HashSet<>();
    declarations.add("Boolean F Object");
    declarations.add("Boolean G Object");
    declarations.add("Boolean H Object");
    declarations.add("Object a");
    premises.add("(forAll x (implies (F x) (and (H x) (not (G x)))))");
    runProver(declarations, premises, "(implies (G a) (not (F a)))", true);
  }

  @Test
  public void prob45c() {
    List<String> declarations = new ArrayList<>();
    Set<String> premises = new HashSet<>();
    declarations.add("Boolean G Object");
    declarations.add("Boolean H Object");
    declarations.add("Object a");
    declarations.add("Object b");

    premises.add("(forAll x (implies (H x) (G x)))");
    runProver(declarations, premises, "(and (implies (H a) (G a)) (not (and (not (G b)) (H b))))", true);
  }

  @Test
  public void prob46c() {
    List<String> declarations = new ArrayList<>();
    Set<String> premises = new HashSet<>();
    declarations.add("Boolean P Object");
    declarations.add("Boolean H Object");

    premises.add("(forAll x (iff (P x) (and (H x) (not (P x)))))");
    runProver(declarations, premises, "(forAll x (not (H x)))", true);
  }

  @Test
  public void prob47c() {
    List<String> declarations = new ArrayList<>();
    Set<String> premises = new HashSet<>();
    declarations.add("Boolean F Object");
    declarations.add("Boolean G Object");

    premises.add("(forAll x (F x))");
    premises.add("(forAll x (implies (F x) (G x)))");
    runProver(declarations, premises, "(forAll x (G x))", true);
  }

  @Test
  public void prob48c() {
    List<String> declarations = new ArrayList<>();
    Set<String> premises = new HashSet<>();
    declarations.add("Boolean F Object");
    declarations.add("Boolean G Object");
    declarations.add("Object a");

    premises.add("(F a)");
    runProver(declarations, premises, "(exists x (or (F x) (G x)))", true);
  }

  @Test
  public void prob49c() {
    List<String> declarations = new ArrayList<>();
    Set<String> premises = new HashSet<>();
    declarations.add("Boolean F Object");
    declarations.add("Boolean G Object");

    premises.add("(exists x (F x))");
    runProver(declarations, premises, "(exists x (or (F x) (G x)))", true);
  }

  @Test
  public void prob50c() {
    List<String> declarations = new ArrayList<>();
    Set<String> premises = new HashSet<>();
    declarations.add("Boolean F Object");
    declarations.add("Boolean G Object");

    premises.add("(exists x (or (F x) (G x)))");
    premises.add("(not (exists x (F x)))");
    runProver(declarations, premises, "(exists x (G x))", true);
  }

  @Test
  public void prob51c() {
    List<String> declarations = new ArrayList<>();
    Set<String> premises = new HashSet<>();
    declarations.add("Boolean F Object");
    declarations.add("Boolean G Object");

    premises.add("(implies (exists x (F x)) (forAll y (G y)))");
    runProver(declarations, premises, "(forAll x (forAll y (implies (F x) (G y))))", true);
  }

  @Test
  public void prob52c() {
    List<String> declarations = new ArrayList<>();
    Set<String> premises = new HashSet<>();
    declarations.add("Boolean F Object");
    declarations.add("Boolean G Object");
    declarations.add("Boolean H Object");

    premises.add("(forAll x (implies (F x) (implies (G x) (H x))))");
    runProver(declarations, premises, "(implies (forAll x (implies (F x) (G x))) (forAll x (implies (F x) (H x))))", true);
  }

  @Test
  public void prob53c() {
    List<String> declarations = new ArrayList<>();
    Set<String> premises = new HashSet<>();
    declarations.add("Boolean F Object");
    declarations.add("Boolean G Object Object");

    premises.add("(forAll x (implies (F x) (exists y (and (F y) (G x y)))))");
    runProver(declarations, premises, "(forAll x (implies (F x) (exists y (exists z (and (G x y) (G y z))))))", true);
  }

  @Test
  public void prob54c() {
    List<String> declarations = new ArrayList<>();
    Set<String> premises = new HashSet<>();
    declarations.add("Boolean R Object Object");

    premises.add("(forAll x (exists y (R x y)))");
    premises.add("(forAll x (forAll y (implies (R x y) (R y x))))");
    premises.add("(forAll x (forAll y (forAll z (implies (and (R x y) (R y z)) (R x z)))))");
    runProver(declarations, premises, "(forAll x (R x x))", true);
  }

  @Test
  public void prob55c() {
    List<String> declarations = new ArrayList<>();
    declarations.add("Boolean F Object");
    runProver(declarations, new HashSet<>(), "(implies (forAll x (F x)) (exists x (F x)))", true);
  }

  @Test
  public void prob56c() {
    List<String> declarations = new ArrayList<>();
    declarations.add("Boolean F Object");
    runProver(declarations, new HashSet<>(), "(exists x (implies (F x) (forAll y (F y))))", true);
  }

  @Test
  public void prob57c() {
    List<String> declarations = new ArrayList<>();
    declarations.add("Boolean R Object Object");
    runProver(declarations, new HashSet<>(), "(implies (forAll x (forAll y (implies (R x y) (not (R y x))))) (not (exists x (R x x))))", true);
  }

  @Test
  public void prob58c() {
    List<String> declarations = new ArrayList<>();
    declarations.add("Boolean R Object Object");
    runProver(declarations, new HashSet<>(), "(not (exists x (forAll y (iff (R x y) (not (R y y))))))", true);
  }

  @Test
  public void prob59c() {
    List<String> declarations = new ArrayList<>();
    declarations.add("Boolean F Object");
    runProver(declarations, new HashSet<>(), "(not (forAll x (implies (or (F x) (not (F x))) (not (or (F x) (not (F x)))))))", true);
  }

  @Test
  public void prob60c() {
    List<String> declarations = new ArrayList<>();
    declarations.add("Boolean F Object");
    declarations.add("Boolean G Object");
    runProver(declarations, new HashSet<>(), "(iff (exists x (or (F x) (G x))) (or (exists x (F x)) (exists x (G x))))", true);
  }

  @Test
  public void prob61c() {
    List<String> declarations = new ArrayList<>();
    declarations.add("Boolean F Object");
    declarations.add("Boolean G Object");
    runProver(declarations, new HashSet<>(), "(iff (forAll x (and (F x) (G x))) (and (forAll x (F x)) (forAll x (G x))))", true);
  }

  @Test
  public void prob62c() {
    List<String> declarations = new ArrayList<>();
    declarations.add("Boolean F Object");
    declarations.add("Boolean G Object");
    runProver(declarations, new HashSet<>(), "(implies (forAll x (implies (F x) (G x))) (implies (forAll x (F x)) (forAll x (G x))))", true);
  }

  @Test
  public void prob63c() {
    List<String> declarations = new ArrayList<>();
    declarations.add("Boolean F Object");
    declarations.add("Boolean P");
    runProver(declarations, new HashSet<>(), "(iff (implies P (forAll x (F x))) (forAll x (implies P (F x))))", true);
  }

  @Test
  public void prob64c() {
    List<String> declarations = new ArrayList<>();
    declarations.add("Boolean F Object");
    declarations.add("Boolean P");
    runProver(declarations, new HashSet<>(), "(iff (implies P (exists x (F x))) (exists x (implies P (F x))))", true);
  }

  @Test
  public void prob65c() {
    List<String> declarations = new ArrayList<>();
    declarations.add("Boolean F Object");
    declarations.add("Boolean P");
    runProver(declarations, new HashSet<>(), "(iff (implies (forAll x (F x)) P) (exists x (implies (F x) P)))", true);
  }

  @Test
  public void prob66c() {
    List<String> declarations = new ArrayList<>();
    declarations.add("Boolean F Object");
    runProver(declarations, new HashSet<>(), "(forAll x (or (F x) (not (F x))))", true);
  }

  @Test
  public void prob67c() {
    List<String> declarations = new ArrayList<>();
    declarations.add("Boolean F Object");
    runProver(declarations, new HashSet<>(), "(exists x (or (F x) (not (F x))))", true);
  }

  @Test
  public void prob68c() {
    List<String> declarations = new ArrayList<>();
    declarations.add("Boolean F Object Object");
    declarations.add("Object a");
    runProver(declarations, new HashSet<>(), "(exists y (iff (F a y) (F y y)))", true);
  }

  @Test
  public void prob69c() {
    List<String> declarations = new ArrayList<>();
    declarations.add("Boolean F Object Object");
    runProver(declarations, new HashSet<>(), "(forAll x (exists y (iff (F x y) (F y y))))", true);
  }

  @Test
  public void prob70c() {
    List<String> declarations = new ArrayList<>();
    declarations.add("Boolean F Object");
    runProver(declarations, new HashSet<>(), "(exists y (forAll x (implies (F y) (F x))))", true);
  }

  @Test
  public void prob71c() {
    List<String> declarations = new ArrayList<>();
    declarations.add("Boolean P Object");
    declarations.add("Boolean Q Object");
    runProver(declarations, new HashSet<>(), "(exists x (forAll y (forAll z (implies (implies (P y) (Q z)) (implies (P x) (Q x)) ))))", true);
  }

  //@Test
  public void prob72c() {
    try { Thread.sleep(10000); } catch (InterruptedException ie) {}
    List<String> declarations = new ArrayList<>();
    declarations.add("Boolean P Object");
    declarations.add("Boolean Q Object");
    declarations.add("Boolean R Object");
    declarations.add("Boolean S Object");

    runProver(declarations, new HashSet<>(),
            "(implies (forAll x (forAll y (exists z (forAll w (implies (and (P x) (Q y)) (and (R z) (S w)))))))" +
                    " (implies (exists v1 (exists u (and (P v1) (Q u)))) (exists s (R s))))", true);
  }

  @Test
  public void prob73c() {
    List<String> declarations = new ArrayList<>();
    Set<String> premises = new HashSet<>();
    declarations.add("Boolean F Object");
    declarations.add("Boolean P");

    premises.add("(exists x (implies P (F x)))");
    premises.add("(exists x (implies (F x) P))");
    runProver(declarations, premises, "(exists x (iff P (F x)))", true);
  }

  @Test
  public void prob74c() {
    List<String> declarations = new ArrayList<>();
    declarations.add("Boolean F Object");
    declarations.add("Boolean P");
    runProver(declarations, new HashSet<>(), "(implies (forAll x (iff P (F x))) (iff P (forAll y (F y))))", true);
  }

  @Test
  public void prob75c() {
    List<String> declarations = new ArrayList<>();
    declarations.add("Boolean F Object");
    declarations.add("Boolean P");
    runProver(declarations, new HashSet<>(), "(iff (forAll x (or P (F x))) (or P (forAll y (F y))))", true);
  }

  @Test
  public void prob76c() {
    List<String> declarations = new ArrayList<>();
    Set<String> premises = new HashSet<>();
    declarations.add("Boolean S Object");
    declarations.add("Boolean Q Object");
    declarations.add("Boolean R Object");
    declarations.add("Boolean P Object");

    premises.add("(not (exists x (and (S x) (Q x))))");
    premises.add("(forAll x (implies (P x) (or (Q x) (R x))))");
    premises.add("(implies (not (exists x (P x))) (exists y (Q y)))");
    premises.add("(forAll x (implies (or (Q x) (R x)) (S x)))");
    runProver(declarations, premises, "(exists x (and (P x) (R x)))", true);
  }

  @Test
  public void prob77c() {
    List<String> declarations = new ArrayList<>();
    Set<String> premises = new HashSet<>();
    declarations.add("Boolean P Object");
    declarations.add("Boolean F Object");
    declarations.add("Boolean G Object");
    declarations.add("Boolean R Object");
    declarations.add("Boolean Q Object");

    premises.add("(exists x (P x))");
    premises.add("(forAll x (implies (F x) (and (not (G x)) (R x))))");
    premises.add("(forAll x (implies (P x) (and (G x) (F x))))");
    premises.add("(or (forAll x (implies (P x) (Q x))) (exists y (and (P y) (R y))))");
    runProver(declarations, premises, "(exists x (and (Q x) (P x)))", true);
  }

  @Test
  public void prob78c() {
    //try { Thread.sleep(10000); } catch (InterruptedException ie) {}
    List<String> declarations = new ArrayList<>();
    Set<String> premises = new HashSet<>();
    declarations.add("Boolean P Object");
    declarations.add("Boolean Q Object");
    declarations.add("Boolean R Object");
    declarations.add("Boolean S Object");

    premises.add("(iff (exists x (P x)) (exists y (Q y)))");
    premises.add("(forAll x (forAll y (implies (and (P x) (Q y)) (iff (R x) (S y)))))");
    runProver(declarations, premises, "(iff (forAll x (implies (P x) (R x))) (forAll y (implies (Q y) (S y))))", true);
  }

  @Test
  public void prob79c() {
    List<String> declarations = new ArrayList<>();
    Set<String> premises = new HashSet<>();
    declarations.add("Boolean F Object");
    declarations.add("Boolean G Object");
    declarations.add("Boolean H Object");
    declarations.add("Boolean I Object");
    declarations.add("Boolean J Object");

    premises.add("(exists x (and (F x) (not (G x))))");
    premises.add("(forAll x (implies (F x) (H x)))");
    premises.add("(forAll x (implies (and (J x) (I x)) (F x)))");
    premises.add("(implies (exists x (and (H x) (not (G x)))) (forAll y (implies (I y) (not (H y)))))");
    runProver(declarations, premises, "(forAll x (implies (J x) (not (I x))))", true);
  }

  @Test
  public void prob80c() {
    List<String> declarations = new ArrayList<>();
    Set<String> premises = new HashSet<>();
    declarations.add("Boolean P Object");
    declarations.add("Boolean Q Object");
    declarations.add("Boolean R Object");
    declarations.add("Boolean S Object");
    declarations.add("Boolean F Object");
    declarations.add("Boolean G Object");

    premises.add("(forAll x (implies (P x) (forAll y (Q y))))");
    premises.add("(implies (forAll x (or (Q x) (R x))) (exists y (and (Q y) (S y))))");
    premises.add("(implies (exists x (S x)) (forAll x (implies (F x) (G x))))");
    runProver(declarations, premises, "(forAll x (implies (and (P x) (F x)) (G x)))", true);
  }

  @Test
  public void prob81c() {
    List<String> declarations = new ArrayList<>();
    Set<String> premises = new HashSet<>();
    declarations.add("Boolean F Object");
    declarations.add("Boolean G Object");
    declarations.add("Boolean H Object");
    declarations.add("Boolean J Object");

    premises.add("(and (exists x (F x)) (exists y (G y)))");
    runProver(declarations, premises, "(iff (and (forAll x (implies (F x) (H x))) (forAll y (implies (G y) (J y)))) (forAll z (forAll w (implies (and (F z) (G w)) (and (H z) (J w))))))", true);
  }

  @Test
  public void prob82c() {
    List<String> declarations = new ArrayList<>();
    Set<String> premises = new HashSet<>();
    declarations.add("Boolean F Object");
    declarations.add("Boolean G Object");
    declarations.add("Boolean H Object");
    declarations.add("Boolean I Object");

    premises.add("(forAll x (implies (or (F x) (G x)) (not (H x))))");
    premises.add("(forAll x (implies (implies (G x) (not (I x))) (and (F x) (H x))))");
    runProver(declarations, premises, "(forAll x (I x))", true);
  }

  @Test
  public void prob83c() {
    List<String> declarations = new ArrayList<>();
    Set<String> premises = new HashSet<>();
    declarations.add("Boolean F Object");
    declarations.add("Boolean G Object");
    declarations.add("Boolean H Object");
    declarations.add("Boolean I Object");
    declarations.add("Boolean J Object");

    premises.add("(not (exists x (and (F x) (or (G x) (H x)))))");
    premises.add("(exists x (and (I x) (F x)))");
    premises.add("(forAll x (implies (not (H x)) (J x)))");
    runProver(declarations, premises, "(exists x (and (I x) (J x)))", true);
  }

  @Test
  public void prob84c() {
    List<String> declarations = new ArrayList<>();
    Set<String> premises = new HashSet<>();
    declarations.add("Boolean F Object");
    declarations.add("Boolean G Object");
    declarations.add("Boolean H Object");
    declarations.add("Boolean I Object");
    declarations.add("Boolean J Object");
    declarations.add("Boolean K Object");

    premises.add("(forAll x (implies (and (F x) (or (G x) (H x))) (I x)))");
    premises.add("(forAll x (implies (and (I x) (H x)) (J x)))");
    premises.add("(forAll x (implies (K x) (H x)))");
    runProver(declarations, premises, "(forAll x (implies (and (F x) (K x)) (J x)))", true);
  }

  @Test
  public void prob85c() {
    List<String> declarations = new ArrayList<>();
    declarations.add("Boolean P Object");
    declarations.add("Boolean Q Object");

    runProver(declarations, new HashSet<>(), "(implies (iff (exists x (forAll y (iff (P x) (P y)))) (iff (exists z (Q z)) (forAll w (Q w)))) " +
            "(iff (exists u (forAll v1 (iff (Q u) (Q v1)))) (iff (exists r (P r)) (forAll s (P s)))))", true);
  }

  @Test
  public void prob86c() {
    List<String> declarations = new ArrayList<>();
    declarations.add("Boolean P Object");
    declarations.add("Boolean Q Object");

    runProver(declarations, new HashSet<>(), "(implies (iff (exists x (forAll y (iff (P x) (P y)))) (iff (exists z (Q z)) (forAll w (Q w)))) " +
            "(iff (exists u (forAll v1 (iff (Q u) (Q v1)))) (iff (exists r (P r)) (forAll s (P s)))))", true);
  }

  @Test
  public void prob87c() {
    List<String> declarations = new ArrayList<>();
    declarations.add("Boolean P Object Object");

    runProver(declarations, new HashSet<>(), "(exists u (exists v1 (implies (P u v1) (forAll x (forAll y (P x y))))))", true);
  }

  //@Test
  public void prob88c() throws InterruptedException {
    //Thread.sleep(10000);
    List<String> declarations = new ArrayList<>();
    Set<String> premises = new HashSet<>();
    declarations.add("Boolean F Object Object");
    declarations.add("Boolean G Object Object");
    declarations.add("Boolean H Object Object");

    premises.add("(forAll x (exists y (F x y)))");
    premises.add("(forAll x (exists z (G x z)))");
    premises.add("(forAll x (forAll y (implies (or (F x y) (G x y)) (forAll z (implies (or (F y z) (G y z)) (H x z))))))");
    runProver(declarations, premises, "(forAll x (exists y (H x y)))", true);
  }

  //@Test
  public void prob89c() {
    List<String> declarations = new ArrayList<>();
    Set<String> premises = new HashSet<>();
    declarations.add("Boolean P Object Object");
    declarations.add("Boolean Q Object Object");
    declarations.add("Boolean R Object Object");

    premises.add("(forAll z (exists w (forAll x (exists y (and (implies (P x z) (P y w)) (P y z) (implies (P y w) (exists u (Q u w))))))))");
    premises.add("(forAll x (forAll z (implies (not (P x z)) (exists v1 (Q v1 z)))))");
    premises.add("(implies (exists y (exists s (Q y s))) (forAll x (R x x)))");
    runProver(declarations, premises, "(forAll x (exists y (R x y)))", true);
  }

  @Test
  public void prob90c() {
    List<String> declarations = new ArrayList<>();
    declarations.add("Boolean P Object");
    declarations.add("Boolean R Object Object");
    declarations.add("Object a");

    runProver(declarations, new HashSet<>(), "(iff (forAll x (implies (and (P a) (implies (P x) (exists y (and (P y) (R x y))))) (exists z (exists w (and (P z) (R x w) (R w z)))))) (forAll x (and (or (not (P a)) (P x) (exists z (exists w (and (P z) (R x w) (R w z))))) (or (not (P a)) (not (exists y (and (P y) (R x y)))) (exists z (exists w (and (P z) (R x w) (R w z))))))))", true);
  }

  @Test
  public void prob91c() {
    List<String> declarations = new ArrayList<>();
    declarations.add("Boolean F Object Object");

    runProver(declarations, new HashSet<>(), "(not (exists x (forAll y (iff (F y x) (not (F y y))))))", true);
  }

  @Test
  public void prob92c() {
    List<String> declarations = new ArrayList<>();
    declarations.add("Boolean F Object Object");

    runProver(declarations, new HashSet<>(), "(implies (exists y (forAll x (iff (F x y) (F x x)))) (not (forAll z (exists w (forAll v1 (iff (F v1 w) (not (F v1 z))))))))", true);
  }

  @Test
  public void prob93c() {
    List<String> declarations = new ArrayList<>();
    Set<String> premises = new HashSet<>();
    declarations.add("Boolean F Object Object");

    premises.add("(forAll z (exists y (forAll x (iff (F x y) (and (F x z) (not (F x x)))))))");
    runProver(declarations, premises, "(not (exists z (forAll x (F x z))))", true);
  }

  @Test
  public void prob94c() {
    List<String> declarations = new ArrayList<>();
    declarations.add("Boolean F Object Object");
    runProver(declarations, new HashSet<>(), "(not (exists y (forAll x (iff (F x y) (not (exists z (and (F x z) (F z x))))))))", true);
  }

  @Test
  public void prob95c() {
    List<String> declarations = new ArrayList<>();
    Set<String> premises = new HashSet<>();
    declarations.add("Boolean Q Object Object");
    declarations.add("Boolean F Object Object");
    premises.add("(forAll x (forAll y (iff (Q x y) (forAll z (iff (F z x) (F z y))))))");
    runProver(declarations, premises, "(forAll x (forAll y (iff (Q x y) (Q y x))))", true);
  }

  @Test
  public void prob96c() {
    List<String> declarations = new ArrayList<>();
    Set<String> premises = new HashSet<>();
    declarations.add("Boolean F Object");
    declarations.add("Boolean G Object");
    declarations.add("Boolean H Object Object");
    declarations.add("Boolean J Object");

    premises.add("(forAll x (and (implies (F x) (exists y (and (G y) (H x y)))) (exists y (and (G y) (not (H x y))))))");
    premises.add("(exists x (and (J x) (forAll y (implies (G y) (H x y)))))");
    runProver(declarations, premises, "(exists x (and (J x) (not (F x))))", true);
  }

  @Test
  public void prob97c() {
    List<String> declarations = new ArrayList<>();
    Set<String> premises = new HashSet<>();
    declarations.add("Boolean F Object");
    declarations.add("Boolean G Object");
    declarations.add("Boolean H Object Object");
    declarations.add("Boolean J Object Object");
    declarations.add("Boolean K Object");
    declarations.add("Boolean L Object");

    premises.add("(forAll x (implies (and (F x) (forAll y (implies (and (G y) (H x y)) (J x y)))) (forAll y (implies (and (G y) (H x y)) (K y)))))");
    premises.add("(not (exists y (and (L y) (K y))))");
    premises.add("(exists x (and (F x) (forAll y (implies (H x y) (L y))) (forAll y (implies (and (G y) (H x y)) (J x y)))))");
    runProver(declarations, premises, "(exists x (and (F x) (not (exists y (and (G y) (H x y))))))", true);
  }

  @Test
  public void prob98c() {
    List<String> declarations = new ArrayList<>();
    Set<String> premises = new HashSet<>();
    declarations.add("Boolean F Object");
    declarations.add("Boolean G Object");
    declarations.add("Boolean H Object Object");
    declarations.add("Boolean J Object Object");

    premises.add("(forAll x (implies (and (F x) (forAll y (implies (and (F y) (H y x)) (G y)))) (G x)))");
    premises.add("(implies (exists x (and (F x) (not (G x)))) (exists x (and (F x) (not (G x)) (forAll y (implies (and (F y) (not (G y))) (J x y))))))");
    premises.add("(forAll x (forAll y (implies (and (F x) (F y) (H x y)) (not (J y x)))))");
    runProver(declarations, premises, "(forAll x (implies (F x) (G x)))", true);
  }

  //@Test
  public void prob99c() throws InterruptedException {
    Thread.sleep(10000);
    List<String> declarations = new ArrayList<>();
    Set<String> premises = new HashSet<>();
    premises.add("(forAll x (implies (W x) (A x)))");
    premises.add("(forAll x (implies (F x) (A x)))");
    premises.add("(forAll x (implies (B x) (A x)))");
    premises.add("(forAll x (implies (C x) (A x)))");
    premises.add("(forAll x (implies (S x) (A x)))");
    premises.add("(exists w0 (W w0))");
    premises.add("(exists f0 (F f0))");
    premises.add("(exists b0 (B b0))");
    premises.add("(exists c0 (C c0))");
    premises.add("(exists s0 (S s0))");
    premises.add("(exists g0 (G g0))");
    premises.add("(forAll x (implies (G x) (P x)))");
    premises.add("(forAll x (implies (A x) (or (forAll w (implies (P w) (E x w))) (forAll y (implies (and (A y) (M y x) (exists z (and (P z) (E y z)))) (E x y))))))");
    premises.add("(forAll x (forAll y (implies (and (C x) (B y)) (M x y))))");
    premises.add("(forAll x (forAll y (implies (and (S x) (B y)) (M x y))))");
    premises.add("(forAll x (forAll y (implies (and (B x) (F y)) (M x y))))");
    premises.add("(forAll x (forAll y (implies (and (F x) (W y)) (M x y))))");
    premises.add("(forAll x (forAll y (implies (and (W x) (F y)) (not (E x y)))))");
    premises.add("(forAll x (forAll y (implies (and (W x) (G y)) (not (E x y)))))");
    premises.add("(forAll x (forAll y (implies (and (B x) (C y)) (E x y))))");
    premises.add("(forAll x (forAll y (implies (and (B x) (S y)) (not (E x y)))))");
    premises.add("(forAll x (implies (C x) (exists y (and (P y) (E x y)))))");
    premises.add("(forAll x (implies (S x) (exists y (and (P y) (E x y)))))");
    runProver(declarations, premises, "(exists x (exists y (and (A x) (A y) (exists z (and (E x y) (G z) (E y z))))))", true);
  }

  //@Test
  public void schubertSteamrollerSorts() throws InterruptedException {
    //Thread.sleep(10000);
    List<String> declarations = new ArrayList<>();
    Set<String> premises = new HashSet<>();

    declarations.add("declare-sort Animal Object");
    declarations.add("declare-sort Wolf Animal");
    declarations.add("Wolf wolf");
    declarations.add("declare-sort Fox Animal");
    declarations.add("Fox fox");
    declarations.add("declare-sort Bird Animal");
    declarations.add("Bird bird");
    declarations.add("declare-sort Caterpillar Animal");
    declarations.add("Caterpillar caterpillar");
    declarations.add("declare-sort Snail Animal");
    declarations.add("Snail snail");
    declarations.add("declare-sort Plant Object");
    declarations.add("declare-sort Grain Plant");
    declarations.add("Grain grain");

    declarations.add("Boolean E Animal Object");
    declarations.add("Boolean M Animal Animal");

    premises.add("(forAll (Animal x) (or (forAll (Plant w) (E x w)) (forAll (Animal y) (implies (and (M y x) (exists (Plant z) (E y z))) (E x y)))))");
    premises.add("(forAll (Caterpillar x) (forAll (Bird y) (M x y)))");
    premises.add("(forAll (Snail x) (forAll (Bird y) (M x y)))");
    premises.add("(forAll (Bird x) (forAll (Fox y) (M x y)))");
    premises.add("(forAll (Fox x) (forAll (Wolf y) (M x y)))");
    premises.add("(forAll (Wolf x) (forAll (Fox y) (not (E x y))))");
    premises.add("(forAll (Wolf x) (forAll (Grain y) (not (E x y))))");
    premises.add("(forAll (Bird x) (forAll (Caterpillar y) (E x y)))");
    premises.add("(forAll (Bird x) (forAll (Snail y) (not (E x y))))");
    premises.add("(forAll (Caterpillar x) (exists (Plant y) (E x y)))");
    premises.add("(forAll (Snail x) (exists (Plant y) (E x y)))");
    runProver(declarations, premises, "(exists (Animal x) (exists (Animal y) (exists (Grain z) (and (E x y) (E y z)))))", true);
  }


}
