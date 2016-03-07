package logicalreasoner.prover;

import logicalreasoner.sentence.SentenceReader;
import org.junit.Assert;
import logicalreasoner.sentence.Sentence;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * Test problems from Bram's Propositional Logic Arguments sheets
 * a problems come from (some valid, some invalid) first side
 * b problems come from (some valid, some invalid), second side
 * c problems come from (all valid), first side
 * d problems come from (all valid), second side
 *      -Note, problem 20d is NOT valid
 */
public class SemanticProverTest {
    public static void runProver(Set<String> premises, Set<String> interests, boolean validArgument) {
        Set<Sentence> p = new HashSet<Sentence>() {{
            premises.forEach(premise -> this.add(SentenceReader.parse(premise)));
        }};
        Set<Sentence> i = new HashSet<Sentence>() {{
            interests.forEach(interest -> this.add(SentenceReader.parse(interest)));
        }};

        SemanticProver prover = new SemanticProver(p, i);
        prover.run();
        if (validArgument) {
            Assert.assertFalse("Prover determined a valid argument was invalid", prover.isConsistent());
        } else {
            Assert.assertTrue("Prover determined an invalid argument was valid", prover.isConsistent());
        }
    }

    @Test
    public void prob1a() {
        runProver(new HashSet<String>() {{
            add("(implies A (and B C))");
            add("(iff C B)");
            add("(not C)");
        }}, new HashSet<String>() {{ add("(not A)"); }}, true);
    }

    @Test
    public void prob2a() {
        runProver(new HashSet<String>() {{
            add("(implies K H)");
            add("(implies H L)");
            add("(implies L M)");
        }}, new HashSet<String>() {{ add("(implies K M)"); }}, true);
    }

    @Test
    public void prob3a() {
        runProver(new HashSet<String>() {{
            add("(not (iff A B))");
            add("(not A)");
            add("(not B)");
        }}, new HashSet<String>() {{ add("(and C (not C))"); }}, true);
    }

    @Test
    public void prob4a() {
        runProver(new HashSet<String>() {{
            add("(and A (or B C))");
            add("(implies (or (not C) H) (implies H (not H)))");
        }}, new HashSet<String>() {{ add("(and A B)"); }}, false);
    }

    @Test
    public void prob5a() {
        runProver(new HashSet<String>() {{
            add("(implies R Q)");
            add("(not (and T (not S)))");
            add("(or (not Q) (not S))");
        }}, new HashSet<String>() {{ add("(or (not R) (not T))"); }}, true);
    }

    @Test
    public void prob6a() {
        runProver(new HashSet<String>() {{
            add("(and A (implies B C))");
        }}, new HashSet<String>() {{ add("(or (and A B) (and A C))"); }}, false);
    }

    @Test
    public void prob7a() {
        runProver(new HashSet<String>() {{
            add("(implies (and (or C D) H) A)");
            add("D");
        }}, new HashSet<String>() {{ add("(implies H A)"); }}, true);
    }

    @Test
    public void prob8a() {
        runProver(new HashSet<String>() {{
            add("(implies (or J M) (not (and J M)))");
            add("(iff M (implies M J))");
        }}, new HashSet<String>() {{ add("(implies M J)"); }}, true);
    }

    @Test
    public void prob9a() {
        runProver(new HashSet<String>() {{
            add("(not (iff A B))");
        }}, new HashSet<String>() {{ add("(iff (not A) (not B))"); }}, false);
    }

    @Test
    public void prob10a() {
        runProver(new HashSet<String>() {{
        }}, new HashSet<String>() {{ add("(iff (implies (not P) P) P)"); }}, true);
    }

    @Test
    public void prob11a() {
        runProver(new HashSet<String>() {{
            add("(implies M (implies K B))");
            add("(implies (not K) (not M))");
            add("(and L M)");
        }}, new HashSet<String>() {{ add("B"); }}, true);
    }

    @Test
    public void prob12a() {
        runProver(new HashSet<String>() {{
            add("(implies (or (not J) K) (and L M))");
            add("(not (or (not J) K))");
        }}, new HashSet<String>() {{ add("(not (and L M))"); }}, false);
    }

    @Test
    public void prob13a() {
        runProver(new HashSet<String>() {{
            add("(not (and (not A) (not B)))");
        }}, new HashSet<String>() {{ add("(and A B)"); }}, false);
    }

    @Test
    public void prob14a() {
        runProver(new HashSet<String>() {{
            add("(or (iff M K) (not (and K D)))");
            add("(implies (not M) (not K))");
            add("(implies (not D) (not (and K D)))");
        }}, new HashSet<String>() {{ add("M"); }}, false);
    }

    @Test
    public void prob15a() {
        runProver(new HashSet<String>() {{
            add("(and B (or H Z))");
            add("(implies (not Z) K)");
            add("(implies (iff B Z) (not Z))");
            add("(not K)");
        }}, new HashSet<String>() {{ add("(and M N)"); }}, true);
    }

    @Test
    public void prob16a() {
        runProver(new HashSet<String>() {{
            add("(and (iff D (not G)) G)");
            add("implies (or G (and (implies A D) A)) (not D))");
        }}, new HashSet<String>() {{ add("(implies G (not D))"); }}, true);
    }

    @Test
    public void prob17a() {
        runProver(new HashSet<String>() {{
            add("(implies J (implies T J))");
            add("(implies T (implies J T))");
        }}, new HashSet<String>() {{ add("(and (not J) (not T))"); }}, false);
    }

    @Test
    public void prob18a() {
        runProver(new HashSet<String>() {{
            add("(and A (implies B C))");
        }}, new HashSet<String>() {{ add("(or (and A C) (and A (not B)))"); }}, true);
    }

    @Test
    public void prob19a() {
        runProver(new HashSet<String>() {{
            add("(or A (not (and B C)))");
            add("(not B)");
            add("(not (or A C))");
        }}, new HashSet<String>() {{ add("A"); }}, false);
    }

    @Test
    public void prob20a() {
        runProver(new HashSet<String>() {{
            add("(or (iff G H) (iff (not G) H))");
        }}, new HashSet<String>() {{ add("(or (iff (not G) (not H)) (not (iff G H)))"); }}, true);
    }

    @Test
    public void prob1b() {
        runProver(new HashSet<String>() {{
            add("(implies K (not K))");
        }}, new HashSet<String>() {{ add("(not K)"); }}, true);
    }


    @Test
    public void prob2b() {
        runProver(new HashSet<String>() {{
            add("(implies R R)");
        }}, new HashSet<String>() {{ add("R"); }}, false);
    }

    @Test
    public void prob3b() {
        runProver(new HashSet<String>() {{
            add("(iff P (not N))");
        }}, new HashSet<String>() {{ add("(or N P)"); }}, true);
    }

    @Test
    public void prob4b() {
        runProver(new HashSet<String>() {{
            add("(not (and G M))");
            add("(or M (not G))");
        }}, new HashSet<String>() {{ add("(not G)"); }}, true);
    }

    @Test
    public void prob5b() {
        runProver(new HashSet<String>() {{
            add("(iff K (not L))");
            add("(not (and L (not K)))");
        }}, new HashSet<String>() {{ add("(implies K L)"); }}, false);
    }

    @Test
    public void prob6b() {
        runProver(new HashSet<String>() {{
            add("Z");
        }}, new HashSet<String>() {{ add("(implies E (implies Z E))"); }}, true);
    }

    @Test
    public void prob7b() {
        runProver(new HashSet<String>() {{
            add("(not (and W (not X)))");
            add("(not (and X (not W)))");
        }}, new HashSet<String>() {{ add("(or X W)"); }}, false);
    }

    @Test
    public void prob8b() {
        runProver(new HashSet<String>() {{
            add("(iff C D)");
            add("(or E (not D))");
        }}, new HashSet<String>() {{ add("(implies E C)"); }}, false);
    }

    @Test
    public void prob9b() {
        runProver(new HashSet<String>() {{
            add("(iff A (or B C))");
            add("(or (not C) B)");
        }}, new HashSet<String>() {{ add("(implies A B)"); }}, true);
    }

    @Test
    public void prob10b() {
        runProver(new HashSet<String>() {{
            add("(implies J (implies K L))");
            add("(implies K (implies J L))");
        }}, new HashSet<String>() {{ add("(implies (or J K) L)"); }}, false);
    }

    @Test
    public void prob11b() {
        runProver(new HashSet<String>() {{
            add("(not (iff K S))");
            add("(implies S (not (or R K)))");
        }}, new HashSet<String>() {{ add("(or R (not S))"); }}, false);
    }

    @Test
    public void prob12b() {
        runProver(new HashSet<String>() {{
            add("(implies E (and F G))");
            add("(implies F (implies G H))");
        }}, new HashSet<String>() {{ add("(implies E H)"); }}, true);
    }

    @Test
    public void prob13b() {
        runProver(new HashSet<String>() {{
            add("(implies A (or N Q))");
            add("(not (or N (not A)))");
        }}, new HashSet<String>() {{ add("(implies A Q)"); }}, true);
    }

    @Test
    public void prob14b() {
        runProver(new HashSet<String>() {{
            add("(implies G H)");
            add("(iff R G)");
            add("(or (not H) G)");
        }}, new HashSet<String>() {{ add("(iff R H)"); }}, true);
    }

    @Test
    public void prob15b() {
        runProver(new HashSet<String>() {{
            add("(implies L M)");
            add("(implies M N)");
            add("(implies N L)");
        }}, new HashSet<String>() {{ add("(or L N)"); }}, false);
    }

    @Test
    public void prob16b() {
        runProver(new HashSet<String>() {{
            add("(implies S T)");
            add("(implies S (not T))");
            add("(implies (not T) S)");
        }}, new HashSet<String>() {{ add("(or S (not T))"); }}, false);
    }

    @Test
    public void prob17b() {
        runProver(new HashSet<String>() {{
            add("(implies W X)");
            add("(implies X W)");
            add("(implies X Y)");
            add("(implies Y X)");
        }}, new HashSet<String>() {{ add("(iff W Y)"); }}, true);
    }

    @Test
    public void prob18b() {
        runProver(new HashSet<String>() {{
            add("(iff K (or L M))");
            add("(implies L M)");
            add("(implies M K)");
            add("(or K L)");
        }}, new HashSet<String>() {{ add("(implies K L)"); }}, false);
    }

    @Test
    public void prob19b() {
        runProver(new HashSet<String>() {{
            add("(implies A B)");
            add("(implies (and A B) C)");
            add("(implies A (implies C D))");
        }}, new HashSet<String>() {{ add("(implies A D)"); }}, true);
    }

    @Test
    public void prob20b() {
        runProver(new HashSet<String>() {{
            add("(or (not A) R)");
            add("(not (and N (not C)))");
            add("(implies R C)");
            add("(implies C (not N))");
        }}, new HashSet<String>() {{ add("(or A C)"); }}, false);
    }

    @Test
    public void prob1c() {
        runProver(new HashSet<String>() {{
            add("(and R (and C (not F)))");
            add("(implies (or R S) (not W))");
        }}, new HashSet<String>() {{ add("(not W)"); }}, true);
    }

    @Test
    public void prob2c() {
        runProver(new HashSet<String>() {{
            add("(implies A (and B C))");
            add("(not C)");
        }}, new HashSet<String>() {{ add("(not (and A D))"); }}, true);
    }

    @Test
    public void prob3c() {
        runProver(new HashSet<String>() {{
            add("(iff A B)");
            add("(iff B C)");
        }}, new HashSet<String>() {{ add("(iff A C)"); }}, true);
    }

    @Test
    public void prob4c() {
        runProver(new HashSet<String>() {{
            add("(iff F G)");
            add("(or F G)");
        }}, new HashSet<String>() {{ add("(and F G)"); }}, true);
    }

    @Test
    public void prob5c() {
        runProver(new HashSet<String>() {{
            add("(iff (not B) Z)");
            add("(implies N B)");
            add("(and Z N)");
        }}, new HashSet<String>() {{ add("(not H)"); }}, true);
    }

    @Test
    public void prob6c() {
        runProver(new HashSet<String>() {{
            add("(iff A B)");
            add("(iff B (not C))");
        }}, new HashSet<String>() {{ add("(not (iff A C))"); }}, true);
    }

    @Test
    public void prob7c() {
        runProver(new HashSet<String>() {{
            add("(implies M I)");
            add("(and (not I) L)");
            add("(or M B)");
        }}, new HashSet<String>() {{ add("B"); }}, true);
    }

    @Test
    public void prob8c() {
        runProver(new HashSet<String>() {{
            add("(or Q (iff J D))");
            add("(not D)");
            add("J");
        }}, new HashSet<String>() {{ add("Q"); }}, true);
    }

    @Test
    public void prob9c() {
        runProver(new HashSet<String>() {{
            add("(or A B)");
            add("(or (not B) C)");
            add("(not C)");
        }}, new HashSet<String>() {{ add("A"); }}, true);
    }

    @Test
    public void prob10c() {
        runProver(new HashSet<String>() {{
            add("(or (not H) J K)");
            add("(implies K (not I))");
        }}, new HashSet<String>() {{ add("(implies (and H I) J)"); }}, true);
    }

    @Test
    public void prob11c() {
        runProver(new HashSet<String>() {{
            add("(and (or A B) (not C))");
            add("(implies (not C) (and D (not A)))");
            add("(implies B (or A E))");
        }}, new HashSet<String>() {{ add("(or E F)"); }}, true);
    }

    @Test
    public void prob12c() {
        runProver(new HashSet<String>() {{
            add("(implies G (and H (not K)))");
            add("(iff H (and L I))");
            add("(or (not I) K)");
        }}, new HashSet<String>() {{ add("(not G)"); }}, true);
    }

    @Test
    public void prob13c() {
        runProver(new HashSet<String>() {{
            add("(implies R (and (not A) T))");
            add("(or B (not S))");
            add("(or B S)");
        }}, new HashSet<String>() {{ add("(implies A B)"); }}, true);
    }

    @Test
    public void prob14c() {
        runProver(new HashSet<String>() {{
            add("(or S (and (not R) T))");
            add("(implies R (not S))");
        }}, new HashSet<String>() {{ add("(not R)"); }}, true);
    }

    @Test
    public void prob15c() {
        runProver(new HashSet<String>() {{
            add("(implies K (implies (or L M) R))");
            add("(implies (or R S) T)");
        }}, new HashSet<String>() {{ add("(implies K (implies M T))"); }}, true);
    }

    @Test
    public void prob16c() {
        runProver(new HashSet<String>() {{
            add("(and (or P R) (or P Q))");
            add("(implies (and Q R) (implies V W))");
            add("(not (implies (implies P S) (not (implies S W))))");
            add("(not W)");
        }}, new HashSet<String>() {{ add("(implies V S)"); }}, true);
    }

    @Test
    public void prob17c() {
        runProver(new HashSet<String>() {{
            add("(implies (and A B) C)");
            add("(implies (not A) C)");
            add("B");
        }}, new HashSet<String>() {{ add("C"); }}, true);
    }

    @Test
    public void prob18c() {
        runProver(new HashSet<String>() {{
            add("(or F G)");
            add("(and H (implies I F))");
            add("(implies H (not F))");
        }}, new HashSet<String>() {{ add("(and G (not I))"); }}, true);
    }

    @Test
    public void prob19c() {
        runProver(new HashSet<String>() {{
            add("(implies (implies R M) L)");
            add("(implies (or N S) (and M T))");
            add("(implies (implies P R) L)");
            add("(implies (or T K) (not N))");
        }}, new HashSet<String>() {{ add("L"); }}, true);
    }

    @Test
    public void prob20c() {
        runProver(new HashSet<String>() {{
            add("(iff N P)");
        }}, new HashSet<String>() {{ add("(iff (implies N R) (implies P R))"); }}, true);
    }

    @Test
    public void prob1d() {
        runProver(new HashSet<String>() {{
            add("(not A)");
        }}, new HashSet<String>() {{ add("(implies A B)"); }}, true);
    }

    @Test
    public void prob2d() {
        runProver(new HashSet<String>() {{
            add("A");
        }}, new HashSet<String>() {{ add("(implies B A)"); }}, true);
    }

    @Test
    public void prob3d() {
        runProver(new HashSet<String>() {{
            add("(implies A (implies B C))");
        }}, new HashSet<String>() {{ add("(implies B (implies A C))"); }}, true);
    }

    @Test
    public void prob4d() {
        runProver(new HashSet<String>() {{
            add("(implies A B)");
            add("(implies A C)");
        }}, new HashSet<String>() {{ add("(implies A (and B C))"); }}, true);
    }

    @Test
    public void prob5d() {
        runProver(new HashSet<String>() {{
            add("(implies A C)");
            add("(implies B C)");
        }}, new HashSet<String>() {{ add("(implies (or A B) C)"); }}, true);
    }

    @Test
    public void prob6d() {
        runProver(new HashSet<String>() {{
            add("(or A (and B C))");
            add("(implies A D)");
            add("(implies D C)");
        }}, new HashSet<String>() {{ add("C"); }}, true);
    }

    @Test
    public void prob7d() {
        runProver(new HashSet<String>() {{
            add("A");
            add("(iff A B)");
            add("(implies C (not B))");
        }}, new HashSet<String>() {{ add("(not C)"); }}, true);
    }

    @Test
    public void prob8d() {
        runProver(new HashSet<String>() {{
            add("(implies (or A B) (implies C D))");
            add("(implies (or (not D) E) (and A C))");
        }}, new HashSet<String>() {{ add("D"); }}, true);
    }

    @Test
    public void prob9d() {
        runProver(new HashSet<String>() {{
            add("(implies A B)");
            add("(implies A (not B))");
        }}, new HashSet<String>() {{ add("(not A)"); }}, true);
    }

    @Test
    public void prob10d() {
        runProver(new HashSet<String>() {{
            add("(or (not A) B)");
            add("(or A C)");
            add("(implies (not D) (not C))");
        }}, new HashSet<String>() {{ add("(or B D)"); }}, true);
    }

    @Test
    public void prob11d() {
        runProver(new HashSet<String>() {{
            add("(implies A (not (implies B C)))");
            add("(implies (and D B) C)");
            add("D");
        }}, new HashSet<String>() {{ add("(not A)"); }}, true);
    }

    @Test
    public void prob12d() {
        runProver(new HashSet<String>() {{
            add("(implies A B)");
            add("(implies (not B) (not C))");
            add("(not (and (not C) (not A)))");
        }}, new HashSet<String>() {{ add("B"); }}, true);
    }

    @Test
    public void prob13d() {
        runProver(new HashSet<String>() {{
            add("(implies A B)");
            add("(implies (not C) (not B))");
            add("(iff C D)");
        }}, new HashSet<String>() {{ add("(implies A D)"); }}, true);
    }

    @Test
    public void prob14d() {
        runProver(new HashSet<String>() {{
            add("(implies (or A (not A)) (not B))");
            add("(implies (and C D) B)");
        }}, new HashSet<String>() {{ add("(or (not D) (not C))"); }}, true);
    }

    @Test
    public void prob15d() {
        runProver(new HashSet<String>() {{
            add("(implies (or (not A) B) (and C D))");
            add("(not (or A E))");
            add("(implies F (not D))");
        }}, new HashSet<String>() {{ add("(not F)"); }}, true);
    }

    @Test
    public void prob16d() {
        runProver(new HashSet<String>() {{
            add("(iff (not (or A (not B))) (not C))");
            add("C");
        }}, new HashSet<String>() {{ add("(implies B (or A D))"); }}, true);
    }

    @Test
    public void prob17d() {
        runProver(new HashSet<String>() {{
            add("(and A B C)");
            add("(implies A (or D E))");
            add("(implies B (or D F))");
        }}, new HashSet<String>() {{ add("(or D (and E F))"); }}, true);
    }

    @Test
    public void prob18d() {
        runProver(new HashSet<String>() {{
            add("(implies (and A B) C)");
            add("(and (not C) B)");
            add("(implies (or (not A) D) E)");
        }}, new HashSet<String>() {{ add("E"); }}, true);
    }

    @Test
    public void prob19d() {
        runProver(new HashSet<String>() {{
            add("(implies A (and B C))");
            add("(implies (or B D) A)");
        }}, new HashSet<String>() {{ add("(iff A B)"); }}, true);
    }

    @Test
    public void prob20d() {
        runProver(new HashSet<String>() {{
            add("(implies (or (not A) B) (not (and C D)))");
            add("(implies (and A C) E)");
            add("(and A (not E))");
        }}, new HashSet<String>() {{ add("(not (or D E))"); }}, false);
    }
}
