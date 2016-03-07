package logicalreasoner.prover;

import logicalreasoner.sentence.SentenceReader;
import org.junit.Assert;
import logicalreasoner.sentence.Sentence;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * Test problems from first side of Bram's Propositional Logic Arguments (some valid, some invalid) sheet
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
    public void prob1() {
        runProver(new HashSet<String>() {{
            add("(implies A (and B C))");
            add("(iff C B)");
            add("(not C)");
        }}, new HashSet<String>() {{ add("(not A)"); }}, true);
    }

    @Test
    public void prob2() {
        runProver(new HashSet<String>() {{
            add("(implies K H)");
            add("(implies H L)");
            add("(implies L M)");
        }}, new HashSet<String>() {{ add("(implies K M)"); }}, true);
    }

    @Test
    public void prob3() {
        runProver(new HashSet<String>() {{
            add("(not (iff A B))");
            add("(not A)");
            add("(not B)");
        }}, new HashSet<String>() {{ add("(and C (not C))"); }}, true);
    }

    @Test
    public void prob4() {
        runProver(new HashSet<String>() {{
            add("(and A (or B C))");
            add("(implies (or (not C) H) (implies H (not H)))");
        }}, new HashSet<String>() {{ add("(and A B)"); }}, false);
    }

    @Test
    public void prob5() {
        runProver(new HashSet<String>() {{
            add("(implies R Q)");
            add("(not (and T (not S)))");
            add("(or (not Q) (not S))");
        }}, new HashSet<String>() {{ add("(or (not R) (not T))"); }}, true);
    }

    @Test
    public void prob6() {
        runProver(new HashSet<String>() {{
            add("(and A (implies B C))");
        }}, new HashSet<String>() {{ add("(or (and A B) (and A C))"); }}, false);
    }

    @Test
    public void prob7() {
        runProver(new HashSet<String>() {{
            add("(implies (and (or C D) H) A)");
            add("D");
        }}, new HashSet<String>() {{ add("(implies H A)"); }}, true);
    }

    @Test
    public void prob8() {
        runProver(new HashSet<String>() {{
            add("(implies (or J M) (not (and J M)))");
            add("(iff M (implies M J))");
        }}, new HashSet<String>() {{ add("(implies M J)"); }}, true);
    }

    @Test
    public void prob9() {
        runProver(new HashSet<String>() {{
            add("(not (iff A B))");
        }}, new HashSet<String>() {{ add("(iff (not A) (not B))"); }}, false);
    }

    @Test
    public void prob10() {
        runProver(new HashSet<String>() {{
        }}, new HashSet<String>() {{ add("(iff (implies (not P) P) P)"); }}, true);
    }

    @Test
    public void prob11() {
        runProver(new HashSet<String>() {{
            add("(implies M (implies K B))");
            add("(implies (not K) (not M))");
            add("(and L M)");
        }}, new HashSet<String>() {{ add("B"); }}, true);
    }

    @Test
    public void prob12() {
        runProver(new HashSet<String>() {{
            add("(implies (or (not J) K) (and L M))");
            add("(not (or (not J) K))");
        }}, new HashSet<String>() {{ add("(not (and L M))"); }}, false);
    }

    @Test
    public void prob13() {
        runProver(new HashSet<String>() {{
            add("(not (and (not A) (not B)))");
        }}, new HashSet<String>() {{ add("(and A B)"); }}, false);
    }

    @Test
    public void prob14() {
        runProver(new HashSet<String>() {{
            add("(or (iff M K) (not (and K D)))");
            add("(implies (not M) (not K))");
            add("(implies (not D) (not (and K D)))");
        }}, new HashSet<String>() {{ add("M"); }}, false);
    }

    @Test
    public void prob15() {
        runProver(new HashSet<String>() {{
            add("(and B (or H Z))");
            add("(implies (not Z) K)");
            add("(implies (iff B Z) (not Z))");
            add("(not K)");
        }}, new HashSet<String>() {{ add("(and M N)"); }}, true);
    }

    @Test
    public void prob16() {
        runProver(new HashSet<String>() {{
            add("(and (iff D (not G)) G)");
            add("implies (or G (and (implies A D) A)) (not D))");
        }}, new HashSet<String>() {{ add("(implies G (not D))"); }}, true);
    }

    @Test
    public void prob17() {
        runProver(new HashSet<String>() {{
            add("(implies J (implies T J))");
            add("(implies T (implies J T))");
        }}, new HashSet<String>() {{ add("(and (not J) (not T))"); }}, false);
    }

    @Test
    public void prob18() {
        runProver(new HashSet<String>() {{
            add("(and A (implies B C))");
        }}, new HashSet<String>() {{ add("(or (and A C) (and A (not B)))"); }}, true);
    }

    @Test
    public void prob19() {
        runProver(new HashSet<String>() {{
            add("(or A (not (and B C)))");
            add("(not B)");
            add("(not (or A C))");
        }}, new HashSet<String>() {{ add("A"); }}, false);
    }

    @Test
    public void prob20() {
        runProver(new HashSet<String>() {{
            add("(or (iff G H) (iff (not G) H))");
        }}, new HashSet<String>() {{ add("(or (iff (not G) (not H)) (not (iff G H)))"); }}, true);
    }
}
