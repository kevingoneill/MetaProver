package logicalreasoner.prover;

import expression.sentence.DeclarationParser;
import expression.sentence.Sentence;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

/**
 * First Order Logic Tests from Bram's Handout.
 * Problems marked 'a' are all valid, and problems
 * marked 'b' can be either valid or invalid.
 * <p>
 * problems marked 'c' come from OSCAR's Combined-problems
 * test suite
 */
public class FOLTests {
  public static void runProver(List<String> declarations, Set<String> premises, String goal, boolean validArgument) {
    declarations.forEach(DeclarationParser::parseDeclaration);
    Set<Sentence> p = new HashSet<>();
    premises.forEach(premise -> p.add(Sentence.makeSentenceStrict(premise)));

    Prover prover = new FOLProver(p, Collections.singleton(Sentence.makeSentence(goal)), true);
    prover.run();
    if (!prover.finishedProof())
      throw new RuntimeException("Prover could not finish proof in the given amount of time.");
    if (validArgument)
      Assert.assertFalse("Prover determined a valid argument was invalid", prover.isConsistent());
    else
      Assert.assertTrue("Prover determined an invalid argument was valid", prover.isConsistent());
    Sentence.clearDeclarations();
  }

  @Test
  public void prob1a() {
    List<String> declarations = new ArrayList<>();
    Set<String> premises = new HashSet<>();
    declarations.add("Boolean A Object");
    declarations.add("Boolean B Object");
    declarations.add("Boolean C Object");

    premises.add("(forAll x (implies (A x) (B x)))");
    premises.add("(forAll x (implies (B x) (C x)))");
    runProver(declarations, premises, "(forAll x (implies (A x) (C x)))", true);
  }

  @Test
  public void prob2a() {
    List<String> declarations = new ArrayList<>();
    Set<String> premises = new HashSet<>();
    declarations.add("Boolean A Object");
    declarations.add("Boolean B Object");
    declarations.add("Boolean C Object");
    declarations.add("Object a");

    premises.add("(forAll x (implies (A x) (and (B x) (C x))))");
    premises.add("(forAll x (implies (A x) (not (C x))))");
    runProver(declarations, premises, "(not (A a))", true);
  }

  @Test
  public void prob3a() {
    List<String> declarations = new ArrayList<>();
    Set<String> premises = new HashSet<>();
    declarations.add("Boolean A Object");
    declarations.add("Boolean B Object Object");
    declarations.add("Object b");

    premises.add("(forAll x (implies (A x) (forAll y (B x y))))");
    premises.add("(A b)");
    runProver(declarations, premises, "(forAll y (B b y))", true);
  }

  @Test
  public void prob4a() {
    List<String> declarations = new ArrayList<>();
    Set<String> premises = new HashSet<>();
    declarations.add("Boolean A Object");
    declarations.add("Boolean B Object");
    declarations.add("Boolean C Object");

    premises.add("(forAll x (implies (B x) (C x)))");
    premises.add("(exists y (and (A y) (B y)))");
    runProver(declarations, premises, "(exists z (and (A z) (C z)))", true);
  }

  @Test
  public void prob5a() {
    List<String> declarations = new ArrayList<>();
    Set<String> premises = new HashSet<>();
    declarations.add("Boolean F Object");
    declarations.add("Boolean G Object");
    declarations.add("Object a");

    premises.add("(not (exists z (F z)))");
    runProver(declarations, premises, "(implies (F a) (G a))", true);
  }

  @Test
  public void prob6a() {
    List<String> declarations = new ArrayList<>();
    Set<String> premises = new HashSet<>();
    declarations.add("Boolean A Object");
    declarations.add("Boolean B Object");
    declarations.add("Boolean C Object");
    declarations.add("Object c");

    premises.add("(implies (exists x (A x)) (forAll x (implies (B x) (C x))))");
    premises.add("(and (A c) (B c))");
    runProver(declarations, premises, "(C c)", true);
  }


  @Test
  public void prob7a() {
    List<String> declarations = new ArrayList<>();
    Set<String> premises = new HashSet<>();
    declarations.add("Boolean B Object");
    declarations.add("Boolean C Object");

    premises.add("(forAll x (implies (B x) (C x)))");
    premises.add("(exists y (not (C y)))");
    runProver(declarations, premises, "(exists z (not (B z)))", true);
  }

  @Test
  public void prob8a() {
    List<String> declarations = new ArrayList<>();
    Set<String> premises = new HashSet<>();
    declarations.add("Boolean K Object");
    declarations.add("Boolean H Object");
    declarations.add("Object a");

    premises.add("(K a)");
    premises.add("(forAll x (implies (K x) (forAll y (H y))))");
    runProver(declarations, premises, "(forAll x (H x))", true);
  }

  @Test
  public void prob9a() {
    List<String> declarations = new ArrayList<>();
    Set<String> premises = new HashSet<>();
    declarations.add("Boolean A Object");
    declarations.add("Boolean B Object");

    premises.add("(forAll x (exists y (implies (A x) (B y))))");
    runProver(declarations, premises, "(forAll x (implies (A x) (exists y (B y))))", true);
  }

  @Test
  public void prob10a() {
    List<String> declarations = new ArrayList<>();
    Set<String> premises = new HashSet<>();
    declarations.add("Boolean A Object Object");
    declarations.add("Object a");

    premises.add("(exists x (forAll y (A x y)))");
    runProver(declarations, premises, "(exists x (A x a))", true);
  }

  @Test
  public void prob11a() {
    List<String> declarations = new ArrayList<>();
    Set<String> premises = new HashSet<>();
    declarations.add("Boolean A Object");
    declarations.add("Boolean B Object");

    premises.add("(implies (forAll x (A x)) (exists y (B y)))");
    premises.add("(forAll y (not (B y)))");
    runProver(declarations, premises, "(not (forAll z (A z)))", true);
  }

  @Test
  public void prob12a() {
    List<String> declarations = new ArrayList<>();
    Set<String> premises = new HashSet<>();
    declarations.add("Boolean A Object");
    declarations.add("Boolean B Object");
    declarations.add("Boolean C Object");
    declarations.add("Boolean D Object");

    premises.add("(forAll x (implies (A x) (implies (exists y (and (B y) (D y))) (C x))))");
    premises.add("(forAll x (implies (B x) (C x)))");
    runProver(declarations, premises, "(forAll x (implies (A x) (implies (B x) (C x))))", true);
  }

  @Test
  public void prob13a() {
    List<String> declarations = new ArrayList<>();
    Set<String> premises = new HashSet<>();
    declarations.add("Boolean R Object");
    declarations.add("Boolean S Object");
    declarations.add("Boolean T Object");

    premises.add("(implies (exists x (R x)) (exists x (S x)))");
    premises.add("(forAll y (implies (T y) (R y)))");
    runProver(declarations, premises, "(implies (exists x (T x)) (exists z (S z)))", true);
  }

  @Test
  public void prob14a() {
    List<String> declarations = new ArrayList<>();
    Set<String> premises = new HashSet<>();
    declarations.add("Boolean A Object Object");
    declarations.add("Boolean B Object Object");

    premises.add("(exists x (forAll y (implies (A x y) (B x y))))");
    premises.add("(forAll x (exists y (not (B x y))))");
    runProver(declarations, premises, "(not (forAll x (forAll y (A x y))))", true);
  }

  @Test
  public void prob15a() {
    List<String> declarations = new ArrayList<>();
    Set<String> premises = new HashSet<>();
    declarations.add("Boolean A Object");
    declarations.add("Boolean B Object");
    declarations.add("Boolean C Object Object");

    premises.add("(exists x (and (A x) (forAll y (implies (B y) (C x y)))))");
    premises.add("(forAll x (exists y (implies (A x) (B y))))");
    runProver(declarations, premises, "(exists x (exists y (C x y)))", true);
  }

  @Test
  public void prob16a() {
    List<String> declarations = new ArrayList<>();
    Set<String> premises = new HashSet<>();
    declarations.add("Boolean F Object");
    declarations.add("Boolean G Object");
    declarations.add("Boolean D Object");

    premises.add("(or (forAll x (F x)) (forAll x (not (G x))))");
    premises.add("(not (forAll x (F x)))");
    premises.add("(forAll x (implies (D x) (G x)))");
    runProver(declarations, premises, "(exists x (not (D x)))", true);
  }

  @Test
  public void prob17a() {
    //try { Thread.sleep(10000); } catch (InterruptedException ie) {}
    List<String> declarations = new ArrayList<>();
    Set<String> premises = new HashSet<>();
    declarations.add("Boolean A Object Object");
    declarations.add("Boolean B Object Object");
    declarations.add("Boolean C Object Object");
    declarations.add("Boolean D Object Object");
    declarations.add("Object a");
    declarations.add("Object b");
    declarations.add("Object c");
    declarations.add("Object e");
    declarations.add("Object f");

    premises.add("(not (exists x (and (A x a) (not (B x b)))))");
    premises.add("(not (exists x (and (C x c) (C b x))))");
    premises.add("(forAll x (implies (B e x) (C x f)))");
    runProver(declarations, premises, "(not (and (A e a) (C f c)))", true);
  }

  @Test
  public void prob18a() {
    List<String> declarations = new ArrayList<>();
    Set<String> premises = new HashSet<>();
    declarations.add("Boolean A Object");
    declarations.add("Boolean B Object");
    declarations.add("Boolean C Object");
    declarations.add("Boolean D Object");

    premises.add("(implies (exists x (A x)) (forAll x (implies (B x) (C x))))");
    premises.add("(implies (exists x (D x)) (exists x (not (C x))))");
    premises.add("(exists y (and (A y) (D y)))");
    runProver(declarations, premises, "(exists z (not (B z)))", true);
  }

  @Test
  public void prob19a() {
    List<String> declarations = new ArrayList<>();
    Set<String> premises = new HashSet<>();
    declarations.add("Boolean F Object");
    declarations.add("Boolean G Object");

    premises.add("(forAll x (exists y (or (not (F x)) (G y))))");
    runProver(declarations, premises, "(forAll x (implies (F x) (exists y (G y))))", true);
  }

  @Test
  public void prob20a() {
    List<String> declarations = new ArrayList<>();
    Set<String> premises = new HashSet<>();
    declarations.add("Boolean A Object");
    declarations.add("Boolean H Object");
    declarations.add("Boolean G Object");

    premises.add("(forAll x (implies (A x) (H x)))");
    premises.add("(implies (exists x (A x)) (not (exists y (G y))))");
    runProver(declarations, premises, "(forAll x (implies (exists y (A y)) (not (G x))))", true);
  }

  @Test
  public void prob21a() {
    List<String> declarations = new ArrayList<>();
    Set<String> premises = new HashSet<>();
    declarations.add("Boolean A Object");
    declarations.add("Boolean Q Object");
    declarations.add("Boolean L Object Object");
    declarations.add("Boolean I Object");

    premises.add("(exists x (and (A x) (forAll y (implies (Q y) (L x y)))))");
    premises.add("(forAll x (implies (A x) (forAll y (implies (I y) (not (L x y))))))");
    runProver(declarations, premises, "(forAll x (implies (Q x) (not (I x))))", true);
  }

  @Test
  public void prob22a() {
    List<String> declarations = new ArrayList<>();
    Set<String> premises = new HashSet<>();
    declarations.add("Boolean F Object Object");
    declarations.add("Boolean G Object Object");

    premises.add("(exists x (forAll y (F x y)))");
    premises.add("(forAll x (forAll y (implies (F y x) (G x y))))");
    runProver(declarations, premises, "(forAll y (exists x (G y x)))", true);
  }

  @Test
  public void prob23a() {
    //try { Thread.sleep(10000); } catch (InterruptedException ie) {}
    List<String> declarations = new ArrayList<>();
    Set<String> premises = new HashSet<>();
    declarations.add("Boolean F Object Object");
    declarations.add("Boolean G Object Object");

    premises.add("(implies (forAll x (exists y (F y x))) (forAll x (exists y (G x y))))");
    premises.add("(exists x (forAll y (not (G x y))))");
    runProver(declarations, premises, "(exists y (forAll x (not (F x y))))", true);
  }

  @Test
  public void prob24a() {
    List<String> declarations = new ArrayList<>();
    Set<String> premises = new HashSet<>();
    declarations.add("Boolean F Object");
    declarations.add("Boolean G Object");
    declarations.add("Boolean H Object Object");
    declarations.add("Object a");

    premises.add("(exists x (and (F x) (forAll y (implies (G y) (H x y)))))");
    runProver(declarations, premises, "(exists x (and (F x) (implies (G a) (H x a))))", true);
  }

  @Test
  public void prob1b() {
    List<String> declarations = new ArrayList<>();
    Set<String> premises = new HashSet<>();
    declarations.add("Boolean F Object");
    declarations.add("Boolean G Object");

    premises.add("(forAll x (implies (F x) (G x)))");
    premises.add("(exists x (not (G x)))");
    runProver(declarations, premises, "(exists x (not (F x)))", true);
  }

  @Test
  public void prob2b() {
    List<String> declarations = new ArrayList<>();
    Set<String> premises = new HashSet<>();
    declarations.add("Boolean F Object");
    declarations.add("Boolean G Object");
    declarations.add("Object a");

    premises.add("(forAll x (implies (F x) (forAll y (G y))))");
    premises.add("(F a)");
    runProver(declarations, premises, "(forAll x (G x))", true);
  }

  @Test
  public void prob3b() {
    List<String> declarations = new ArrayList<>();
    Set<String> premises = new HashSet<>();
    declarations.add("Boolean A Object");
    declarations.add("Boolean B Object");
    declarations.add("Boolean C Object");

    premises.add("(forAll x (implies (A x) (B x)))");
    premises.add("(forAll x (implies (not (A x)) (C x)))");
    runProver(declarations, premises, "(forAll x (implies (not (B x)) (not (C x))))", false);
  }

  @Test
  public void prob4b() {
    List<String> declarations = new ArrayList<>();
    Set<String> premises = new HashSet<>();
    declarations.add("Boolean A Object");
    declarations.add("Boolean B Object");
    declarations.add("Boolean C Object");
    declarations.add("Boolean D Object");

    premises.add("(exists x (and (A x) (not (B x))))");
    premises.add("(exists x (and (A x) (not (C x))))");
    premises.add("(exists x (and (not (B x)) (D x)))");
    runProver(declarations, premises, "(exists x (and (A x) (not (B x)) (D x)))", false);
  }

  @Test
  public void prob5b() {
    List<String> declarations = new ArrayList<>();
    Set<String> premises = new HashSet<>();
    declarations.add("Boolean A Object");
    declarations.add("Boolean F Object");
    declarations.add("Boolean G Object");

    premises.add("(forAll x (implies (A x) (F x)))");
    premises.add("(implies (exists x (F x)) (not (exists y (G y))))");
    runProver(declarations, premises, "(forAll x (implies (exists y (A y)) (not (G x))))", true);
  }

  @Test
  public void prob6b() {
    List<String> declarations = new ArrayList<>();
    Set<String> premises = new HashSet<>();
    declarations.add("Boolean A Object");
    declarations.add("Boolean B Object");
    declarations.add("Boolean C Object");

    premises.add("(exists x (or (A x) (not (B x))))");
    premises.add("(forAll x (implies (and (A x) (not (B x))) (C x)))");
    runProver(declarations, premises, "(exists x (C x))", false);
  }

  @Test
  public void prob7b() {
    List<String> declarations = new ArrayList<>();
    Set<String> premises = new HashSet<>();
    declarations.add("Boolean F Object Object");
    declarations.add("Boolean G Object");
    declarations.add("Object a");

    premises.add("(forAll x (not (F x x)))");
    premises.add("(implies (not (forAll x (G x))) (exists y (F y a)))");
    runProver(declarations, premises, "(exists z (and (G z) (F z z)))", false);
  }

  //@Test
  // THIS PROBLEM CREATES AN INFINITE TREE
  public void prob8b() {
    List<String> declarations = new ArrayList<>();
    Set<String> premises = new HashSet<>();
    declarations.add("Boolean F Object");
    declarations.add("Boolean G Object Object");

    premises.add("(forAll x (exists y (and (F x) (G x y))))");
    runProver(declarations, premises, "(exists y (forAll x (and (F x) (G x y))))", false);
  }

  @Test
  public void prob9b() {
    //try {Thread.sleep(10000);} catch (InterruptedException ie) {}
    List<String> declarations = new ArrayList<>();
    Set<String> premises = new HashSet<>();
    declarations.add("Boolean F Object");
    declarations.add("Boolean G Object");
    declarations.add("Boolean L Object Object");
    declarations.add("Boolean M Object");

    premises.add("(exists x (and (F x) (forAll y (implies (G y) (L x y)))))");
    premises.add("(forAll x (implies (F x) (forAll y (implies (M y) (not (L x y))))))");
    runProver(declarations, premises, "(forAll x (implies (G x) (not (M x))))", true);
  }

  @Test
  public void prob10b() {
    List<String> declarations = new ArrayList<>();
    Set<String> premises = new HashSet<>();
    declarations.add("Boolean F Object");
    declarations.add("Boolean G Object Object");
    declarations.add("Object a");
    declarations.add("Object b");

    premises.add("(or (F a) (exists y (G y a)))");
    premises.add("(or (F b) (exists y (not (G y b))))");
    runProver(declarations, premises, "(exists y (G y a))", false);
  }

  @Test
  public void prob11b() {
    List<String> declarations = new ArrayList<>();
    Set<String> premises = new HashSet<>();
    declarations.add("Boolean J Object");
    declarations.add("Boolean H Object Object");
    declarations.add("Boolean R Object Object");
    declarations.add("Object b");

    premises.add("(forAll x (not (J x)))");
    premises.add("(implies (exists y (or (H b y) (R y y))) (exists x (J x)))");
    runProver(declarations, premises, "(forAll y (not (or (H b y) (R y y))))", true);
  }

  @Test
  public void prob12b() {
    List<String> declarations = new ArrayList<>();
    Set<String> premises = new HashSet<>();
    declarations.add("Boolean L Object");
    declarations.add("Boolean H Object");
    declarations.add("Boolean B Object");
    declarations.add("Object b");

    premises.add("(forAll z (iff (L z) (H z)))");
    premises.add("(forAll x (not (or (H x) (not (B x)))))");
    runProver(declarations, premises, "(not (L b))", true);
  }

  @Test
  public void prob13b() {
    List<String> declarations = new ArrayList<>();
    Set<String> premises = new HashSet<>();
    declarations.add("Boolean K Object Object");
    declarations.add("Boolean H Object Object");

    premises.add("(or (not (forAll x (K x x))) (forAll y (H y y)))");
    runProver(declarations, premises, "(exists z (implies (not (H z z)) (not (K z z))))", true);
  }

  @Test
  public void prob14b() {
    List<String> declarations = new ArrayList<>();
    Set<String> premises = new HashSet<>();
    declarations.add("Boolean F Object");
    declarations.add("Boolean G Object Object");

    premises.add("(forAll x (forAll y (or (F x) (G x y))))");
    premises.add("(exists x (F x))");
    runProver(declarations, premises, "(exists x (exists y (G x y)))", false);
  }

  @Test
  public void prob15b() {
    List<String> declarations = new ArrayList<>();
    Set<String> premises = new HashSet<>();
    declarations.add("Boolean L Object Object");

    premises.add("(forAll x (forAll y (forAll z (implies (and (L x y) (L y z)) (L x z)))))");
    premises.add("(forAll x (forAll y (implies (L x y) (L y x))))");
    runProver(declarations, premises, "(forAll x (L x x))", false);
  }

  @Test
  public void prob16b() {
    //try {Thread.sleep(10000);} catch (InterruptedException ie) {}
    List<String> declarations = new ArrayList<>();
    Set<String> premises = new HashSet<>();
    declarations.add("Boolean S Object");
    declarations.add("Boolean B Object Object");

    premises.add("(forAll x (implies (S x) (exists y (and (S y) (forAll z (iff (B z y) (and (B z x) (B z z))))))))");
    premises.add("(forAll x (not (B x x)))");
    premises.add("(exists x (S x))");
    runProver(declarations, premises, "(exists x (and (S x) (forAll y (not (B y x)))))", true);
  }

  @Test
  public void prob17b() {
    //try { Thread.sleep(10000); } catch (InterruptedException ie) {}
    List<String> declarations = new ArrayList<>();
    Set<String> premises = new HashSet<>();
    declarations.add("Boolean A Object");
    declarations.add("Boolean B Object");
    declarations.add("Boolean C Object Object");
    declarations.add("Boolean H Object");
    declarations.add("Boolean F Object");

    premises.add("(forAll x (forAll y (implies (and (A x) (B y)) (C x y))))");
    premises.add("(exists y (and (F y) (forAll z (implies (H z) (C y z)))))");
    premises.add("(forAll x (forAll y (forAll z (implies (and (C x y) (C y z)) (C x z)))))");
    premises.add("(forAll x (implies (F x) (B x)))");
    runProver(declarations, premises, "(forAll z (forAll y (implies (and (A z) (H y)) (C z y))))", true);
  }

  @Test
  public void prob18b() {
    List<String> declarations = new ArrayList<>();
    Set<String> premises = new HashSet<>();
    declarations.add("Boolean A Object");
    declarations.add("Boolean B Object Object");
    declarations.add("Boolean C Object");
    declarations.add("Boolean D Object");
    declarations.add("Boolean F Object");
    declarations.add("Boolean G Object");

    premises.add("(forAll x (implies (exists y (and (A y) (B x y))) (C x)))");
    premises.add("(exists y (and (D y) (exists x (and (F x) (G x) (B y x)))))");
    premises.add("(forAll x (implies (F x) (A x)))");
    premises.add("(implies (exists x (and (C x) (D x))) (implies (exists y (and (D y) (exists z (B y z)))) (forAll x (F x))))");
    runProver(declarations, premises, "(forAll x (A x))", true);
  }
}
