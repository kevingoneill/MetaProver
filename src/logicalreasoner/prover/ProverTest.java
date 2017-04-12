package logicalreasoner.prover;

import expression.sentence.DeclarationParser;
import expression.sentence.Sentence;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * Test problems from Bram's Propositional Logic Arguments sheets
 * a problems come from (some valid, some invalid) first side
 * b problems come from (some valid, some invalid), second side
 * c problems come from (all valid), first side
 * d problems come from (all valid), second side
 * -Note, problem 20d is NOT valid
 */
public class ProverTest {
  static void runProver(Set<String> declarations, Set<String> premises, String goal, boolean validArgument) {
    Set<Sentence> p = new HashSet<>();
    declarations.forEach(DeclarationParser::parseDeclaration);
    premises.forEach(premise -> p.add(Sentence.makeSentence(premise)));

    Prover prover = new Prover(p, Sentence.makeSentenceStrict(goal), true, 1);
    prover.run();
    if (validArgument)
      Assert.assertFalse("Prover determined a valid argument was invalid", prover.isConsistent());
    else
      Assert.assertTrue("Prover determined an invalid argument was valid", prover.isConsistent());
    Sentence.clearDeclarations();
  }

  @Test
  public void prob1a() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean A");
    declarations.add("Boolean B");
    declarations.add("Boolean C");
    
    premises.add("(implies A (and B C))");
    premises.add("(iff C B)");
    premises.add("(not C)");
    runProver(declarations, premises, "(not A)", true);
  }

  @Test
  public void prob2a() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean K");
    declarations.add("Boolean H");
    declarations.add("Boolean L");
    declarations.add("Boolean M");

    premises.add("(implies K H)");
    premises.add("(implies H L)");
    premises.add("(implies L M)");
    runProver(declarations, premises, "(implies K M)", true);
  }

  @Test
  public void prob3a() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean A");
    declarations.add("Boolean B");
    declarations.add("Boolean C");

    premises.add("(not (iff A B))");
    premises.add("(not A)");
    premises.add("(not B)");
    runProver(declarations, premises, "(and C (not C))", true);
  }

  @Test
  public void prob4a() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean A");
    declarations.add("Boolean B");
    declarations.add("Boolean C");
    declarations.add("Boolean H");

    premises.add("(and A (or B C))");
    premises.add("(implies (or (not C) H) (implies H (not H)))");
    runProver(declarations, premises, "(and A B)", false);
  }

  @Test
  public void prob5a() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean R");
    declarations.add("Boolean Q");
    declarations.add("Boolean T");
    declarations.add("Boolean S");

    premises.add("(implies R Q)");
    premises.add("(not (and T (not S)))");
    premises.add("(or (not Q) (not S))");
    runProver(declarations, premises, "(or (not R) (not T))", true);
  }

  @Test
  public void prob6a() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean A");
    declarations.add("Boolean B");
    declarations.add("Boolean C");

    premises.add("(and A (implies B C))");
    runProver(declarations, premises, "(or (and A B) (and A C))", false);
  }

  @Test
  public void prob7a() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean C");
    declarations.add("Boolean D");
    declarations.add("Boolean H");
    declarations.add("Boolean A");

    premises.add("(implies (and (or C D) H) A)");
    premises.add("D");
    runProver(declarations, premises, "(implies H A)", true);
  }

  @Test
  public void prob8a() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean J");
    declarations.add("Boolean M");

    premises.add("(implies (or J M) (not (and J M)))");
    premises.add("(iff M (implies M J))");
    runProver(declarations, premises, "(implies M J)", true);
  }

  @Test
  public void prob9a() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean A");
    declarations.add("Boolean B");

    premises.add("(not (iff A B))");
    runProver(declarations, premises, "(iff (not A) (not B))", false);
  }

  @Test
  public void prob10a() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean P");
    runProver(declarations, premises, "(iff (implies (not P) P) P)", true);
  }

  @Test
  public void prob11a() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean K");
    declarations.add("Boolean B");
    declarations.add("Boolean L");
    declarations.add("Boolean M");

    premises.add("(implies M (implies K B))");
    premises.add("(implies (not K) (not M))");
    premises.add("(and L M)");
    runProver(declarations, premises, "B", true);
  }

  @Test
  public void prob12a() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean K");
    declarations.add("Boolean J");
    declarations.add("Boolean L");
    declarations.add("Boolean M");

    premises.add("(implies (or (not J) K) (and L M))");
    premises.add("(not (or (not J) K))");
    runProver(declarations, premises, "(not (and L M))", false);
  }

  @Test
  public void prob13a() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean A");
    declarations.add("Boolean B");

    premises.add("(not (and (not A) (not B)))");
    runProver(declarations, premises, "(and A B)", false);
  }

  @Test
  public void prob14a() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean K");
    declarations.add("Boolean D");
    declarations.add("Boolean M");

    premises.add("(or (iff M K) (not (and K D)))");
    premises.add("(implies (not M) (not K))");
    premises.add("(implies (not D) (not (and K D)))");
    runProver(declarations, premises, "M", false);
  }

  @Test
  public void prob15a() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean B");
    declarations.add("Boolean H");
    declarations.add("Boolean Z");
    declarations.add("Boolean K");
    declarations.add("Boolean M");
    declarations.add("Boolean N");

    premises.add("(and B (or H Z))");
    premises.add("(implies (not Z) K)");
    premises.add("(implies (iff B Z) (not Z))");
    premises.add("(not K)");
    runProver(declarations, premises, "(and M N)", true);
  }

  @Test
  public void prob16a() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean D");
    declarations.add("Boolean G");
    declarations.add("Boolean A");

    premises.add("(and (iff D (not G)) G)");
    premises.add("(implies (or G (and (implies A D) A)) (not D))");
    runProver(declarations, premises, "(implies G (not D))", true);
  }

  @Test
  public void prob17a() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean T");
    declarations.add("Boolean J");

    premises.add("(implies J (implies T J))");
    premises.add("(implies T (implies J T))");

    runProver(declarations, premises, "(and (not J) (not T))", false);
  }

  @Test
  public void prob18a() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean A");
    declarations.add("Boolean C");
    declarations.add("Boolean B");

    premises.add("(and A (implies B C))");
    runProver(declarations, premises, "(or (and A C) (and A (not B)))", true);
  }

  @Test
  public void prob19a() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean A");
    declarations.add("Boolean B");
    declarations.add("Boolean C");

    premises.add("(or A (not (and B C)))");
    premises.add("(not B)");
    premises.add("(not (or A C))");
    runProver(declarations, premises, "A", false);
  }

  @Test
  public void prob20a() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean G");
    declarations.add("Boolean H");

    premises.add("(or (iff G H) (iff (not G) H))");
    runProver(declarations, premises, "(or (iff (not G) (not H)) (not (iff G H)))", true);
  }

  @Test
  public void prob1b() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean K");
    premises.add("(implies K (not K))");
    runProver(declarations, premises, "(not K)", true);
  }


  @Test
  public void prob2b() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean R");

    premises.add("(implies R R)");
    runProver(declarations, premises, "R", false);
  }

  @Test
  public void prob3b() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean N");
    declarations.add("Boolean P");

    premises.add("(iff P (not N))");
    runProver(declarations, premises, "(or N P)", true);
  }

  @Test
  public void prob4b() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean G");
    declarations.add("Boolean M");

    premises.add("(not (and G M))");
    premises.add("(or M (not G))");
    runProver(declarations, premises, "(not G)", true);
  }

  @Test
  public void prob5b() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean K");
    declarations.add("Boolean L");

    premises.add("(iff K (not L))");
    premises.add("(not (and L (not K)))");
    runProver(declarations, premises, "(implies K L)", false);
  }

  @Test
  public void prob6b() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean E");
    declarations.add("Boolean Z");

    premises.add("Z");
    runProver(declarations, premises, "(implies E (implies Z E))", true);
  }

  @Test
  public void prob7b() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean W");
    declarations.add("Boolean X");

    premises.add("(not (and W (not X)))");
    premises.add("(not (and X (not W)))");
    runProver(declarations, premises, "(or X W)", false);
  }

  @Test
  public void prob8b() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean C");
    declarations.add("Boolean D");
    declarations.add("Boolean E");

    premises.add("(iff C D)");
    premises.add("(or E (not D))");
    runProver(declarations, premises, "(implies E C)", false);
  }

  @Test
  public void prob9b() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean A");
    declarations.add("Boolean B");
    declarations.add("Boolean C");

    premises.add("(iff A (or B C))");
    premises.add("(or (not C) B)");
    runProver(declarations, premises, "(implies A B)", true);
  }

  @Test
  public void prob10b() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean K");
    declarations.add("Boolean J");
    declarations.add("Boolean L");

    premises.add("(implies J (implies K L))");
    premises.add("(implies K (implies J L))");
    runProver(declarations, premises, "(implies (or J K) L)", false);
  }

  @Test
  public void prob11b() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean K");
    declarations.add("Boolean S");
    declarations.add("Boolean R");

    premises.add("(not (iff K S))");
    premises.add("(implies S (not (or R K)))");
    runProver(declarations, premises, "(or R (not S))", false);
  }

  @Test
  public void prob12b() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean E");
    declarations.add("Boolean F");
    declarations.add("Boolean G");
    declarations.add("Boolean H");

    premises.add("(implies E (and F G))");
    premises.add("(implies F (implies G H))");
    runProver(declarations, premises, "(implies E H)", true);
  }

  @Test
  public void prob13b() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean A");
    declarations.add("Boolean N");
    declarations.add("Boolean Q");

    premises.add("(implies A (or N Q))");
    premises.add("(not (or N (not A)))");
    runProver(declarations, premises, "(implies A Q)", true);
  }

  @Test
  public void prob14b() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean G");
    declarations.add("Boolean H");
    declarations.add("Boolean R");

    premises.add("(implies G H)");
    premises.add("(iff R G)");
    premises.add("(or (not H) G)");
    runProver(declarations, premises, "(iff R H)", true);
  }

  @Test
  public void prob15b() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean L");
    declarations.add("Boolean M");
    declarations.add("Boolean N");

    premises.add("(implies L M)");
    premises.add("(implies M N)");
    premises.add("(implies N L)");
    runProver(declarations, premises, "(or L N)", false);
  }

  @Test
  public void prob16b() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean S");
    declarations.add("Boolean T");

    premises.add("(implies S T)");
    premises.add("(implies S (not T))");
    premises.add("(implies (not T) S)");
    runProver(declarations, premises, "(or S (not T))", false);
  }

  @Test
  public void prob17b() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean W");
    declarations.add("Boolean X");
    declarations.add("Boolean Y");

    premises.add("(implies W X)");
    premises.add("(implies X W)");
    premises.add("(implies X Y)");
    premises.add("(implies Y X)");
    runProver(declarations, premises, "(iff W Y)", true);
  }

  @Test
  public void prob18b() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean K");
    declarations.add("Boolean L");
    declarations.add("Boolean M");

    premises.add("(iff K (or L M))");
    premises.add("(implies L M)");
    premises.add("(implies M K)");
    premises.add("(or K L)");
    runProver(declarations, premises, "(implies K L)", false);
  }

  @Test
  public void prob19b() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean A");
    declarations.add("Boolean B");
    declarations.add("Boolean C");
    declarations.add("Boolean D");

    premises.add("(implies A B)");
    premises.add("(implies (and A B) C)");
    premises.add("(implies A (implies C D))");
    runProver(declarations, premises, "(implies A D)", true);
  }

  @Test
  public void prob20b() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean A");
    declarations.add("Boolean R");
    declarations.add("Boolean N");
    declarations.add("Boolean C");

    premises.add("(or (not A) R)");
    premises.add("(not (and N (not C)))");
    premises.add("(implies R C)");
    premises.add("(implies C (not N))");
    runProver(declarations, premises, "(or A C)", false);
  }

  @Test
  public void prob1c() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean R");
    declarations.add("Boolean C");
    declarations.add("Boolean F");
    declarations.add("Boolean W");
    declarations.add("Boolean S");

    premises.add("(and R (and C (not F)))");
    premises.add("(implies (or R S) (not W))");
    runProver(declarations, premises, "(not W)", true);
  }

  @Test
  public void prob2c() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean A");
    declarations.add("Boolean B");
    declarations.add("Boolean C");
    declarations.add("Boolean D");

    premises.add("(implies A (and B C))");
    premises.add("(not C)");
    runProver(declarations, premises, "(not (and A D))", true);
  }

  @Test
  public void prob3c() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean A");
    declarations.add("Boolean B");
    declarations.add("Boolean C");

    premises.add("(iff A B)");
    premises.add("(iff B C)");
    runProver(declarations, premises, "(iff A C)", true);
  }

  @Test
  public void prob4c() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean F");
    declarations.add("Boolean G");

    premises.add("(iff F G)");
    premises.add("(or F G)");
    runProver(declarations, premises, "(and F G)", true);
  }

  @Test
  public void prob5c() throws InterruptedException {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean B");
    declarations.add("Boolean Z");
    declarations.add("Boolean N");
    declarations.add("Boolean H");

    premises.add("(iff (not B) Z)");
    premises.add("(implies N B)");
    premises.add("(and Z N)");
    runProver(declarations, premises, "(not H)", true);
  }

  @Test
  public void prob6c() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean A");
    declarations.add("Boolean B");
    declarations.add("Boolean C");

    premises.add("(iff A B)");
    premises.add("(iff B (not C))");
    runProver(declarations, premises, "(not (iff A C))", true);
  }

  @Test
  public void prob7c() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean M");
    declarations.add("Boolean I");
    declarations.add("Boolean L");
    declarations.add("Boolean B");

    premises.add("(implies M I)");
    premises.add("(and (not I) L)");
    premises.add("(or M B)");
    runProver(declarations, premises, "B", true);
  }

  @Test
  public void prob8c() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean Q");
    declarations.add("Boolean J");
    declarations.add("Boolean D");

    premises.add("(or Q (iff J D))");
    premises.add("(not D)");
    premises.add("J");
    runProver(declarations, premises, "Q", true);
  }

  @Test
  public void prob9c() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean A");
    declarations.add("Boolean B");
    declarations.add("Boolean C");

    premises.add("(or A B)");
    premises.add("(or (not B) C)");
    premises.add("(not C)");
    runProver(declarations, premises, "A", true);
  }

  @Test
  public void prob10c() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean H");
    declarations.add("Boolean J");
    declarations.add("Boolean K");
    declarations.add("Boolean I");

    premises.add("(or (not H) J K)");
    premises.add("(implies K (not I))");
    runProver(declarations, premises, "(implies (and H I) J)", true);
  }

  @Test
  public void prob11c() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean A");
    declarations.add("Boolean B");
    declarations.add("Boolean C");
    declarations.add("Boolean D");
    declarations.add("Boolean E");
    declarations.add("Boolean F");

    premises.add("(and (or A B) (not C))");
    premises.add("(implies (not C) (and D (not A)))");
    premises.add("(implies B (or A E))");
    runProver(declarations, premises, "(or E F)", true);
  }

  @Test
  public void prob12c() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean G");
    declarations.add("Boolean H");
    declarations.add("Boolean K");
    declarations.add("Boolean I");
    declarations.add("Boolean L");

    premises.add("(implies G (and H (not K)))");
    premises.add("(iff H (and L I))");
    premises.add("(or (not I) K)");
    runProver(declarations, premises, "(not G)", true);
  }

  @Test
  public void prob13c() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean R");
    declarations.add("Boolean A");
    declarations.add("Boolean T");
    declarations.add("Boolean B");
    declarations.add("Boolean S");

    premises.add("(implies R (and (not A) T))");
    premises.add("(or B (not S))");
    premises.add("(or B S)");
    runProver(declarations, premises, "(implies A B)", true);
  }

  @Test
  public void prob14c() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean S");
    declarations.add("Boolean R");
    declarations.add("Boolean T");

    premises.add("(or S (and (not R) T))");
    premises.add("(implies R (not S))");
    runProver(declarations, premises, "(not R)", true);
  }

  @Test
  public void prob15c() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean K");
    declarations.add("Boolean L");
    declarations.add("Boolean M");
    declarations.add("Boolean R");
    declarations.add("Boolean S");
    declarations.add("Boolean T");

    premises.add("(implies K (implies (or L M) R))");
    premises.add("(implies (or R S) T)");
    runProver(declarations, premises, "(implies K (implies M T))", true);
  }

  @Test
  public void prob16c() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean P");
    declarations.add("Boolean Q");
    declarations.add("Boolean R");
    declarations.add("Boolean V");
    declarations.add("Boolean W");
    declarations.add("Boolean S");

    premises.add("(and (or P R) (or P Q))");
    premises.add("(implies (and Q R) (implies V W))");
    premises.add("(not (implies (implies P S) (not (implies S W))))");
    premises.add("(not W)");
    runProver(declarations, premises, "(implies V S)", true);
  }

  @Test
  public void prob17c() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean A");
    declarations.add("Boolean B");
    declarations.add("Boolean C");

    premises.add("(implies (and A B) C)");
    premises.add("(implies (not A) C)");
    premises.add("B");
    runProver(declarations, premises, "C", true);
  }

  @Test
  public void prob18c() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean F");
    declarations.add("Boolean G");
    declarations.add("Boolean H");
    declarations.add("Boolean I");

    premises.add("(or F G)");
    premises.add("(and H (implies I F))");
    premises.add("(implies H (not F))");
    runProver(declarations, premises, "(and G (not I))", true);
  }

  @Test
  public void prob19c() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean R");
    declarations.add("Boolean M");
    declarations.add("Boolean L");
    declarations.add("Boolean N");
    declarations.add("Boolean S");
    declarations.add("Boolean T");
    declarations.add("Boolean P");
    declarations.add("Boolean K");

    premises.add("(implies (implies R M) L)");
    premises.add("(implies (or N S) (and M T))");
    premises.add("(implies (implies P R) L)");
    premises.add("(implies (or T K) (not N))");
    runProver(declarations, premises, "L", true);
  }

  @Test
  public void prob20c() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean N");
    declarations.add("Boolean P");
    declarations.add("Boolean R");

    premises.add("(iff N P)");
    runProver(declarations, premises, "(iff (implies N R) (implies P R))", true);
  }

  @Test
  public void prob1d() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean A");
    declarations.add("Boolean B");
    premises.add("(not A)");
    runProver(declarations, premises, "(implies A B)", true);
  }

  @Test
  public void prob2d() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean A");
    declarations.add("Boolean B");

    premises.add("A");
    runProver(declarations, premises, "(implies B A)", true);
  }

  @Test
  public void prob3d() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean A");
    declarations.add("Boolean B");
    declarations.add("Boolean C");

    premises.add("(implies A (implies B C))");
    runProver(declarations, premises, "(implies B (implies A C))", true);
  }

  @Test
  public void prob4d() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean A");
    declarations.add("Boolean B");
    declarations.add("Boolean C");

    premises.add("(implies A B)");
    premises.add("(implies A C)");
    runProver(declarations, premises, "(implies A (and B C))", true);
  }

  @Test
  public void prob5d() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean A");
    declarations.add("Boolean B");
    declarations.add("Boolean C");

    premises.add("(implies A C)");
    premises.add("(implies B C)");
    runProver(declarations, premises, "(implies (or A B) C)", true);
  }

  @Test
  public void prob6d() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean A");
    declarations.add("Boolean B");
    declarations.add("Boolean C");
    declarations.add("Boolean D");

    premises.add("(or A (and B C))");
    premises.add("(implies A D)");
    premises.add("(implies D C)");
    runProver(declarations, premises, "C", true);
  }

  @Test
  public void prob7d() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean A");
    declarations.add("Boolean B");
    declarations.add("Boolean C");

    premises.add("A");
    premises.add("(iff A B)");
    premises.add("(implies C (not B))");
    runProver(declarations, premises, "(not C)", true);
  }

  @Test
  public void prob8d() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean A");
    declarations.add("Boolean B");
    declarations.add("Boolean C");
    declarations.add("Boolean D");
    declarations.add("Boolean E");

    premises.add("(implies (or A B) (implies C D))");
    premises.add("(implies (or (not D) E) (and A C))");
    runProver(declarations, premises, "D", true);
  }

  @Test
  public void prob9d() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean A");
    declarations.add("Boolean B");

    premises.add("(implies A B)");
    premises.add("(implies A (not B))");
    runProver(declarations, premises, "(not A)", true);
  }

  @Test
  public void prob10d() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean A");
    declarations.add("Boolean B");
    declarations.add("Boolean C");
    declarations.add("Boolean D");

    premises.add("(or (not A) B)");
    premises.add("(or A C)");
    premises.add("(implies (not D) (not C))");
    runProver(declarations, premises, "(or B D)", true);
  }

  @Test
  public void prob11d() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean A");
    declarations.add("Boolean B");
    declarations.add("Boolean C");
    declarations.add("Boolean D");

    premises.add("(implies A (not (implies B C)))");
    premises.add("(implies (and D B) C)");
    premises.add("D");
    runProver(declarations, premises, "(not A)", true);
  }

  @Test
  public void prob12d() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean A");
    declarations.add("Boolean B");
    declarations.add("Boolean C");

    premises.add("(implies A B)");
    premises.add("(implies (not B) (not C))");
    premises.add("(not (and (not C) (not A)))");
    runProver(declarations, premises, "B", true);
  }

  @Test
  public void prob13d() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean A");
    declarations.add("Boolean B");
    declarations.add("Boolean C");
    declarations.add("Boolean D");

    premises.add("(implies A B)");
    premises.add("(implies (not C) (not B))");
    premises.add("(iff C D)");
    runProver(declarations, premises, "(implies A D)", true);
  }

  @Test
  public void prob14d() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean A");
    declarations.add("Boolean B");
    declarations.add("Boolean C");
    declarations.add("Boolean D");

    premises.add("(implies (or A (not A)) (not B))");
    premises.add("(implies (and C D) B)");
    runProver(declarations, premises, "(or (not D) (not C))", true);
  }

  @Test
  public void prob15d() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean A");
    declarations.add("Boolean B");
    declarations.add("Boolean C");
    declarations.add("Boolean D");
    declarations.add("Boolean E");
    declarations.add("Boolean F");

    premises.add("(implies (or (not A) B) (and C D))");
    premises.add("(not (or A E))");
    premises.add("(implies F (not D))");
    runProver(declarations, premises, "(not F)", true);
  }

  @Test
  public void prob16d() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean A");
    declarations.add("Boolean B");
    declarations.add("Boolean C");
    declarations.add("Boolean D");

    premises.add("(iff (not (or A (not B))) (not C))");
    premises.add("C");
    runProver(declarations, premises, "(implies B (or A D))", true);
  }

  @Test
  public void prob17d() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean A");
    declarations.add("Boolean B");
    declarations.add("Boolean C");
    declarations.add("Boolean D");
    declarations.add("Boolean E");
    declarations.add("Boolean F");

    premises.add("(and A B C)");
    premises.add("(implies A (or D E))");
    premises.add("(implies B (or D F))");
    runProver(declarations, premises, "(or D (and E F))", true);
  }

  @Test
  public void prob18d() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean A");
    declarations.add("Boolean B");
    declarations.add("Boolean C");
    declarations.add("Boolean D");
    declarations.add("Boolean E");

    premises.add("(implies (and A B) C)");
    premises.add("(and (not C) B)");
    premises.add("(implies (or (not A) D) E)");
    runProver(declarations, premises, "E", true);
  }

  @Test
  public void prob19d() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean A");
    declarations.add("Boolean B");
    declarations.add("Boolean C");
    declarations.add("Boolean D");

    premises.add("(implies A (and B C))");
    premises.add("(implies (or B D) A)");
    runProver(declarations, premises, "(iff A B)", true);
  }

  @Test
  public void prob20d() {
    HashSet<String> declarations = new HashSet<>(),
            premises = new HashSet<>();
    declarations.add("Boolean A");
    declarations.add("Boolean B");
    declarations.add("Boolean C");
    declarations.add("Boolean D");
    declarations.add("Boolean E");

    premises.add("(implies (or (not A) B) (not (and C D)))");
    premises.add("(implies (and A C) E)");
    premises.add("(and A (not E))");
    runProver(declarations, premises, "(not (or D E))", false);
  }
}
