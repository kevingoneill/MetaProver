package logicalreasoner.prover;

import expression.sentence.SentenceReader;
import org.junit.Assert;
import expression.sentence.Sentence;
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
    private static void runProver(Set<String> premises, Set<String> interests, boolean validArgument) {
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
        HashSet<String> premises = new HashSet<>();
        premises.add("(implies A (and B C))");
        premises.add("(iff C B)");
        premises.add("(not C)");
        HashSet<String> interests = new HashSet<>();
        interests.add("(not A)");

        runProver(premises, interests, true);
    }

    @Test
    public void prob2a() {
        HashSet<String> premises = new HashSet<>();
        premises.add("(implies K H)");
        premises.add("(implies H L)");
        premises.add("(implies L M)");
        HashSet<String> interests = new HashSet<>();
        interests.add("(implies K M)");
        runProver(premises, interests, true);
    }

    @Test
    public void prob3a() {
        HashSet<String> premises = new HashSet<>();
        premises.add("(not (iff A B))");
        premises.add("(not A)");
        premises.add("(not B)");
        HashSet<String> interests = new HashSet<>();
        interests.add("(and C (not C))");
        runProver(premises, interests, true);
    }

    @Test
    public void prob4a() {
        HashSet<String> premises = new HashSet<>();
        premises.add("(and A (or B C))");
        premises.add("(implies (or (not C) H) (implies H (not H)))");
        HashSet<String> interests = new HashSet<>();
        interests.add("(and A B)");
        runProver(premises, interests, false);
    }

    @Test
    public void prob5a() {
        HashSet<String> premises = new HashSet<>();
        premises.add("(implies R Q)");
        premises.add("(not (and T (not S)))");
        premises.add("(or (not Q) (not S))");
        HashSet<String> interests = new HashSet<>();
        interests.add("(or (not R) (not T))");
        runProver(premises,  interests, true);
    }

    @Test
    public void prob6a() {
        HashSet<String> premises = new HashSet<>();
        premises.add("(and A (implies B C))");
        HashSet<String> interests = new HashSet<>();
        interests.add("(or (and A B) (and A C))");
        runProver(premises , interests, false);
    }

    @Test
    public void prob7a() {
        HashSet<String> premises = new HashSet<>();
        premises.add("(implies (and (or C D) H) A)");
        premises.add("D");
        HashSet<String> interests = new HashSet<>();
        interests.add("(implies H A)");
        runProver(premises, interests, true);
    }

    @Test
    public void prob8a() {
        HashSet<String> premises = new HashSet<>();
        premises.add("(implies (or J M) (not (and J M)))");
        premises.add("(iff M (implies M J))");
        HashSet<String> interests = new HashSet<>();
        interests.add("(implies M J)");
        runProver(premises, interests, true);
    }

    @Test
    public void prob9a() {
        HashSet<String> premises = new HashSet<>();
        premises.add("(not (iff A B))");
        HashSet<String> interests = new HashSet<>();
        interests.add("(iff (not A) (not B))");
        runProver(premises, interests, false);
    }

    @Test
    public void prob10a() {
        HashSet<String> premises = new HashSet<>();
        HashSet<String> interests = new HashSet<>();
        interests.add("(iff (implies (not P) P) P)");
        runProver(premises, interests, true);
    }

    @Test
    public void prob11a() {
        HashSet<String> premises = new HashSet<>();
        premises.add("(implies M (implies K B))");
        premises.add("(implies (not K) (not M))");
        premises.add("(and L M)");
        HashSet<String> interests = new HashSet<>();
        interests.add("B");
        runProver(premises, interests, true);
    }

    @Test
    public void prob12a() {
        HashSet<String> premises = new HashSet<>();
        premises.add("(implies (or (not J) K) (and L M))");
        premises.add("(not (or (not J) K))");
        HashSet<String> interests = new HashSet<>();
        interests.add("(not (and L M))");
        runProver(premises, interests, false);
    }

    @Test
    public void prob13a() {
        HashSet<String> premises = new HashSet<>();
        premises.add("(not (and (not A) (not B)))");
        HashSet<String> interests = new HashSet<>();
        interests.add("(and A B)");
        runProver(premises, interests, false);
    }

    @Test
    public void prob14a() {
        HashSet<String> premises = new HashSet<>();
        premises.add("(or (iff M K) (not (and K D)))");
        premises.add("(implies (not M) (not K))");
        premises.add("(implies (not D) (not (and K D)))");
        HashSet<String> interests = new HashSet<>();
        interests.add("M");
        runProver(premises, interests, false);
    }

    @Test
    public void prob15a() {
        HashSet<String> premises = new HashSet<>();
        premises.add("(and B (or H Z))");
        premises.add("(implies (not Z) K)");
        premises.add("(implies (iff B Z) (not Z))");
        premises.add("(not K)");
        HashSet<String> interests = new HashSet<>();
        interests.add("(and M N)");
        runProver(premises, interests, true);
    }

    @Test
    public void prob16a() {
        HashSet<String> premises = new HashSet<>();
        premises.add("(and (iff D (not G)) G)");
        premises.add("implies (or G (and (implies A D) A)) (not D))");
        HashSet<String> interests = new HashSet<>();
        interests.add("(implies G (not D))");
        runProver(premises, interests, true);
    }

    @Test
    public void prob17a() {
        HashSet<String> premises = new HashSet<>();
        premises.add("(implies J (implies T J))");
        premises.add("(implies T (implies J T))");
        HashSet<String> interests = new HashSet<>();
        interests.add("(and (not J) (not T))");
        runProver(premises, interests, false);
    }

    @Test
    public void prob18a() {
        HashSet<String> premises = new HashSet<>();
        premises.add("(and A (implies B C))");
        HashSet<String> interests = new HashSet<>();
        interests.add("(or (and A C) (and A (not B)))");
        runProver(premises, interests, true);
    }

    @Test
    public void prob19a() {
        HashSet<String> premises = new HashSet<>();
        premises.add("(or A (not (and B C)))");
        premises.add("(not B)");
        premises.add("(not (or A C))");
        HashSet<String> interests = new HashSet<>();
        interests.add("A");
        runProver(premises, interests, false);
    }

    @Test
    public void prob20a() {
        HashSet<String> premises = new HashSet<>();
        premises.add("(or (iff G H) (iff (not G) H))");
        HashSet<String> interests = new HashSet<>();
        interests.add("(or (iff (not G) (not H)) (not (iff G H)))");
        runProver(premises, interests, true);
    }

    @Test
    public void prob1b() {
        HashSet<String> premises = new HashSet<>();
        premises.add("(implies K (not K))");
        HashSet<String> interests = new HashSet<>();
        interests.add("(not K)");
        runProver(premises, interests, true);
    }


    @Test
    public void prob2b() {
        HashSet<String> premises = new HashSet<>();
        premises.add("(implies R R)");
        HashSet<String> interests = new HashSet<>();
        interests.add("R");
        runProver(premises, interests, false);
    }

    @Test
    public void prob3b() {
        HashSet<String> premises = new HashSet<>();
        premises.add("(iff P (not N))");
        HashSet<String> interests = new HashSet<>();
        interests.add("(or N P)");
        runProver(premises, interests, true);
    }

    @Test
    public void prob4b() {
        HashSet<String> premises = new HashSet<>();
        premises.add("(not (and G M))");
        premises.add("(or M (not G))");
        HashSet<String> interests = new HashSet<>();
        interests.add("(not G)");
        runProver(premises, interests, true);
    }

    @Test
    public void prob5b() {
        HashSet<String> premises = new HashSet<>();
        premises.add("(iff K (not L))");
        premises.add("(not (and L (not K)))");
        HashSet<String> interests = new HashSet<>();
        interests.add("(implies K L)");
        runProver(premises, interests, false);
    }

    @Test
    public void prob6b() {
        HashSet<String> premises = new HashSet<>();
        premises.add("Z");
        HashSet<String> interests = new HashSet<>();
        interests.add("(implies E (implies Z E))");
        runProver(premises, interests, true);
    }

    @Test
    public void prob7b() {
        HashSet<String> premises = new HashSet<>();
        premises.add("(not (and W (not X)))");
        premises.add("(not (and X (not W)))");
        HashSet<String> interests = new HashSet<>();
        interests.add("(or X W)");
        runProver(premises, interests, false);
    }

    @Test
    public void prob8b() {
        HashSet<String> premises = new HashSet<>();
        premises.add("(iff C D)");
        premises.add("(or E (not D))");
        HashSet<String> interests = new HashSet<>();
        interests.add("(implies E C)");
        runProver(premises, interests, false);
    }

    @Test
    public void prob9b() {
        HashSet<String> premises = new HashSet<>();
        premises.add("(iff A (or B C))");
        premises.add("(or (not C) B)");
        HashSet<String> interests = new HashSet<>();
        interests.add("(implies A B)");
        runProver(premises, interests, true);
    }

    @Test
    public void prob10b() {
        HashSet<String> premises = new HashSet<>();
        premises.add("(implies J (implies K L))");
        premises.add("(implies K (implies J L))");
        HashSet<String> interests = new HashSet<>();
        interests.add("(implies (or J K) L)");
        runProver(premises, interests, false);
    }

    @Test
    public void prob11b() {
        HashSet<String> premises = new HashSet<>();
        premises.add("(not (iff K S))");
        premises.add("(implies S (not (or R K)))");
        HashSet<String> interests = new HashSet<>();
        interests.add("(or R (not S))");
        runProver(premises, interests, false);
    }

    @Test
    public void prob12b() {
        HashSet<String> premises = new HashSet<>();
        premises.add("(implies E (and F G))");
        premises.add("(implies F (implies G H))");
        HashSet<String> interests = new HashSet<>();
        interests.add("(implies E H)");
        runProver(premises, interests, true);
    }

    @Test
    public void prob13b() {
        HashSet<String> premises = new HashSet<>();
        premises.add("(implies A (or N Q))");
        premises.add("(not (or N (not A)))");
        HashSet<String> interests = new HashSet<>();
        interests.add("(implies A Q)");
        runProver(premises, interests, true);
    }

    @Test
    public void prob14b() {
        HashSet<String> premises = new HashSet<>();
        premises.add("(implies G H)");
        premises.add("(iff R G)");
        premises.add("(or (not H) G)");
        HashSet<String> interests = new HashSet<>();
        interests.add("(iff R H)");
        runProver(premises, interests, true);
    }

    @Test
    public void prob15b() {
        HashSet<String> premises = new HashSet<>();
        premises.add("(implies L M)");
        premises.add("(implies M N)");
        premises.add("(implies N L)");
        HashSet<String> interests = new HashSet<>();
        interests.add("(or L N)");
        runProver(premises, interests, false);
    }

    @Test
    public void prob16b() {
        HashSet<String> premises = new HashSet<>();
        premises.add("(implies S T)");
        premises.add("(implies S (not T))");
        premises.add("(implies (not T) S)");
        HashSet<String> interests = new HashSet<>();
        interests.add("(or S (not T))");
        runProver(premises, interests, false);
    }

    @Test
    public void prob17b() {
        HashSet<String> premises = new HashSet<>();
        premises.add("(implies W X)");
        premises.add("(implies X W)");
        premises.add("(implies X Y)");
        premises.add("(implies Y X)");
        HashSet<String> interests = new HashSet<>();
        interests.add("(iff W Y)");
        runProver(premises, interests, true);
    }

    @Test
    public void prob18b() {
        HashSet<String> premises = new HashSet<>();
        premises.add("(iff K (or L M))");
        premises.add("(implies L M)");
        premises.add("(implies M K)");
        premises.add("(or K L)");
        HashSet<String> interests = new HashSet<>();
        interests.add("(implies K L)");
        runProver(premises, interests, false);
    }

    @Test
    public void prob19b() {
        HashSet<String> premises = new HashSet<>();
        premises.add("(implies A B)");
        premises.add("(implies (and A B) C)");
        premises.add("(implies A (implies C D))");
        HashSet<String> interests = new HashSet<>();
        interests.add("(implies A D)");
        runProver(premises, interests, true);
    }

    @Test
    public void prob20b() {
        HashSet<String> premises = new HashSet<>();
        premises.add("(or (not A) R)");
        premises.add("(not (and N (not C)))");
        premises.add("(implies R C)");
        premises.add("(implies C (not N))");
        HashSet<String> interests = new HashSet<>();
        interests.add("(or A C)");
        runProver(premises, interests, false);
    }

    @Test
    public void prob1c() {
        HashSet<String> premises = new HashSet<>();
        premises.add("(and R (and C (not F)))");
        premises.add("(implies (or R S) (not W))");
        HashSet<String> interests = new HashSet<>();
        interests.add("(not W)");
        runProver(premises, interests, true);
    }

    @Test
    public void prob2c() {
        HashSet<String> premises = new HashSet<>();
        premises.add("(implies A (and B C))");
        premises.add("(not C)");
        HashSet<String> interests = new HashSet<>();
        interests.add("(not (and A D))");
        runProver(premises, interests, true);
    }

    @Test
    public void prob3c() {
        HashSet<String> premises = new HashSet<>();
        premises.add("(iff A B)");
        premises.add("(iff B C)");
        HashSet<String> interests = new HashSet<>();
        interests.add("(iff A C)");
        runProver(premises, interests, true);
    }

    @Test
    public void prob4c() {
        HashSet<String> premises = new HashSet<>();
        premises.add("(iff F G)");
        premises.add("(or F G)");
        HashSet<String> interests = new HashSet<>();
        interests.add("(and F G)");
        runProver(premises, interests, true);
    }

    @Test
    public void prob5c() {
        HashSet<String> premises = new HashSet<>();
        premises.add("(iff (not B) Z)");
        premises.add("(implies N B)");
        premises.add("(and Z N)");
        HashSet<String> interests = new HashSet<>();
        interests.add("(not H)");
        runProver(premises, interests, true);
    }

    @Test
    public void prob6c() {
        HashSet<String> premises = new HashSet<>();
        premises.add("(iff A B)");
        premises.add("(iff B (not C))");
        HashSet<String> interests = new HashSet<>();
        interests.add("(not (iff A C))");
        runProver(premises, interests, true);
    }

    @Test
    public void prob7c() {
        HashSet<String> premises = new HashSet<>();
        premises.add("(implies M I)");
        premises.add("(and (not I) L)");
        premises.add("(or M B)");
        HashSet<String> interests = new HashSet<>();
        interests.add("B");
        runProver(premises, interests, true);
    }

    @Test
    public void prob8c() {
        HashSet<String> premises = new HashSet<>();
        premises.add("(or Q (iff J D))");
        premises.add("(not D)");
        premises.add("J");
        HashSet<String> interests = new HashSet<>();
        interests.add("Q");
        runProver(premises, interests, true);
    }

    @Test
    public void prob9c() {
        HashSet<String> premises = new HashSet<>();
        premises.add("(or A B)");
        premises.add("(or (not B) C)");
        premises.add("(not C)");
        HashSet<String> interests = new HashSet<>();
        interests.add("A");
        runProver(premises, interests, true);
    }

    @Test
    public void prob10c() {
        HashSet<String> premises = new HashSet<>();
        premises.add("(or (not H) J K)");
        premises.add("(implies K (not I))");
        HashSet<String> interests = new HashSet<>();
        interests.add("(implies (and H I) J)");
        runProver(premises, interests, true);
    }

    @Test
    public void prob11c() {
        HashSet<String> premises = new HashSet<>();
        premises.add("(and (or A B) (not C))");
        premises.add("(implies (not C) (and D (not A)))");
        premises.add("(implies B (or A E))");
        HashSet<String> interests = new HashSet<>();
        interests.add("(or E F)");
        runProver(premises, interests, true);
    }

    @Test
    public void prob12c() {
        HashSet<String> premises = new HashSet<>();
        premises.add("(implies G (and H (not K)))");
        premises.add("(iff H (and L I))");
        premises.add("(or (not I) K)");
        HashSet<String> interests = new HashSet<>();
        interests.add("(not G)");
        runProver(premises, interests, true);
    }

    @Test
    public void prob13c() {
        HashSet<String> premises = new HashSet<>();
        premises.add("(implies R (and (not A) T))");
        premises.add("(or B (not S))");
        premises.add("(or B S)");
        HashSet<String> interests = new HashSet<>();
        interests.add("(implies A B)");
        runProver(premises, interests, true);
    }

    @Test
    public void prob14c() {
        HashSet<String> premises = new HashSet<>();
        premises.add("(or S (and (not R) T))");
        premises.add("(implies R (not S))");
        HashSet<String> interests = new HashSet<>();
        interests.add("(not R)");
        runProver(premises, interests, true);
    }

    @Test
    public void prob15c() {
        HashSet<String> premises = new HashSet<>();
        premises.add("(implies K (implies (or L M) R))");
        premises.add("(implies (or R S) T)");
        HashSet<String> interests = new HashSet<>();
        interests.add("(implies K (implies M T))");
        runProver(premises, interests, true);
    }

    @Test
    public void prob16c() {
        HashSet<String> premises = new HashSet<>();
        premises.add("(and (or P R) (or P Q))");
        premises.add("(implies (and Q R) (implies V W))");
        premises.add("(not (implies (implies P S) (not (implies S W))))");
        premises.add("(not W)");
        HashSet<String> interests = new HashSet<>();
        interests.add("(implies V S)");
        runProver(premises, interests, true);
    }

    @Test
    public void prob17c() {
        HashSet<String> premises = new HashSet<>();
        premises.add("(implies (and A B) C)");
        premises.add("(implies (not A) C)");
        premises.add("B");
        HashSet<String> interests = new HashSet<>();
        interests.add("C");
        runProver(premises, interests, true);
    }

    @Test
    public void prob18c() {
        HashSet<String> premises = new HashSet<>();
        premises.add("(or F G)");
        premises.add("(and H (implies I F))");
        premises.add("(implies H (not F))");
        HashSet<String> interests = new HashSet<>();
        interests.add("(and G (not I))");
        runProver(premises, interests, true);
    }

    @Test
    public void prob19c() {
        HashSet<String> premises = new HashSet<>();
        premises.add("(implies (implies R M) L)");
        premises.add("(implies (or N S) (and M T))");
        premises.add("(implies (implies P R) L)");
        premises.add("(implies (or T K) (not N))");
        HashSet<String> interests = new HashSet<>();
        interests.add("L");
        runProver(premises, interests, true);
    }

    @Test
    public void prob20c() {
        HashSet<String> premises = new HashSet<>();
        premises.add("(iff N P)");
        HashSet<String> interests = new HashSet<>();
        interests.add("(iff (implies N R) (implies P R))");
        runProver(premises, interests, true);
    }

    @Test
    public void prob1d() {
        HashSet<String> premises = new HashSet<>();
        premises.add("(not A)");
        HashSet<String> interests = new HashSet<>();
        interests.add("(implies A B)");
        runProver(premises, interests, true);
    }

    @Test
    public void prob2d() {
        HashSet<String> premises = new HashSet<>();
        premises.add("A");
        HashSet<String> interests = new HashSet<>();
        interests.add("(implies B A)");
        runProver(premises, interests, true);
    }

    @Test
    public void prob3d() {
        HashSet<String> premises = new HashSet<>();
        premises.add("(implies A (implies B C))");
        HashSet<String> interests = new HashSet<>();
        interests.add("(implies B (implies A C))");
        runProver(premises, interests, true);
    }

    @Test
    public void prob4d() {
        HashSet<String> premises = new HashSet<>();
        premises.add("(implies A B)");
        premises.add("(implies A C)");
        HashSet<String> interests = new HashSet<>();
        interests.add("(implies A (and B C))");
        runProver(premises, interests, true);
    }

    @Test
    public void prob5d() {
        HashSet<String> premises = new HashSet<>();
        premises.add("(implies A C)");
        premises.add("(implies B C)");
        HashSet<String> interests = new HashSet<>();
        interests.add("(implies (or A B) C)");
        runProver(premises, interests, true);
    }

    @Test
    public void prob6d() {
        HashSet<String> premises = new HashSet<>();
        premises.add("(or A (and B C))");
        premises.add("(implies A D)");
        premises.add("(implies D C)");
        HashSet<String> interests = new HashSet<>();
        interests.add("C");
        runProver(premises, interests, true);
    }

    @Test
    public void prob7d() {
        HashSet<String> premises = new HashSet<>();
        premises.add("A");
        premises.add("(iff A B)");
        premises.add("(implies C (not B))");
        HashSet<String> interests = new HashSet<>();
        interests.add("(not C)");
        runProver(premises, interests, true);
    }

    @Test
    public void prob8d() {
        HashSet<String> premises = new HashSet<>();
        premises.add("(implies (or A B) (implies C D))");
        premises.add("(implies (or (not D) E) (and A C))");
        HashSet<String> interests = new HashSet<>();
        interests.add("D");
        runProver(premises, interests, true);
    }

    @Test
    public void prob9d() {
        HashSet<String> premises = new HashSet<>();
        premises.add("(implies A B)");
        premises.add("(implies A (not B))");
        HashSet<String> interests = new HashSet<>();
        interests.add("(not A)");
        runProver(premises, interests, true);
    }

    @Test
    public void prob10d() {
        HashSet<String> premises = new HashSet<>();
        premises.add("(or (not A) B)");
        premises.add("(or A C)");
        premises.add("(implies (not D) (not C))");
        HashSet<String> interests = new HashSet<>();
        interests.add("(or B D)");
        runProver(premises, interests, true);
    }

    @Test
    public void prob11d() {
        HashSet<String> premises = new HashSet<>();
        premises.add("(implies A (not (implies B C)))");
        premises.add("(implies (and D B) C)");
        premises.add("D");
        HashSet<String> interests = new HashSet<>();
        interests.add("(not A)");
        runProver(premises, interests, true);
    }

    @Test
    public void prob12d() {
        HashSet<String> premises = new HashSet<>();
        premises.add("(implies A B)");
        premises.add("(implies (not B) (not C))");
        premises.add("(not (and (not C) (not A)))");
        HashSet<String> interests = new HashSet<>();
        interests.add("B");
        runProver(premises, interests, true);
    }

    @Test
    public void prob13d() {
        HashSet<String> premises = new HashSet<>();
        premises.add("(implies A B)");
        premises.add("(implies (not C) (not B))");
        premises.add("(iff C D)");
        HashSet<String> interests = new HashSet<>();
        interests.add("(implies A D)");
        runProver(premises, interests, true);
    }

    @Test
    public void prob14d() {
        HashSet<String> premises = new HashSet<>();
        premises.add("(implies (or A (not A)) (not B))");
        premises.add("(implies (and C D) B)");
        HashSet<String> interests = new HashSet<>();
        interests.add("(or (not D) (not C))");
        runProver(premises, interests, true);
    }

    @Test
    public void prob15d() {
        HashSet<String> premises = new HashSet<>();
        premises.add("(implies (or (not A) B) (and C D))");
        premises.add("(not (or A E))");
        premises.add("(implies F (not D))");
        HashSet<String> interests = new HashSet<>();
        interests.add("(not F)");
        runProver(premises, interests, true);
    }

    @Test
    public void prob16d() {
        HashSet<String> premises = new HashSet<>();
        premises.add("(iff (not (or A (not B))) (not C))");
        premises.add("C");
        HashSet<String> interests = new HashSet<>();
        interests.add("(implies B (or A D))");
        runProver(premises, interests, true);
    }

    @Test
    public void prob17d() {
        HashSet<String> premises = new HashSet<>();
        premises.add("(and A B C)");
        premises.add("(implies A (or D E))");
        premises.add("(implies B (or D F))");
        HashSet<String> interests = new HashSet<>();
        interests.add("(or D (and E F))");
        runProver(premises, interests, true);
    }

    @Test
    public void prob18d() {
        HashSet<String> premises = new HashSet<>();
        premises.add("(implies (and A B) C)");
        premises.add("(and (not C) B)");
        premises.add("(implies (or (not A) D) E)");
        HashSet<String> interests = new HashSet<>();
        interests.add("E");
        runProver(premises, interests, true);
    }

    @Test
    public void prob19d() {
        HashSet<String> premises = new HashSet<>();
        premises.add("(implies A (and B C))");
        premises.add("(implies (or B D) A)");
        HashSet<String> interests = new HashSet<>();
        interests.add("(iff A B)");
        runProver(premises, interests, true);
    }

    @Test
    public void prob20d() {
        HashSet<String> premises = new HashSet<>();
        premises.add("(implies (or (not A) B) (not (and C D)))");
        premises.add("(implies (and A C) E)");
        premises.add("(and A (not E))");
        HashSet<String> interests = new HashSet<>();
        interests.add("(not (or D E))");
        runProver(premises, interests, false);
    }
}
