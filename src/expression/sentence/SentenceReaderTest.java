package expression.sentence;


import org.junit.Test;

/**
 * This class tests the functionality of the SentenceReader,
 * an interface for parsing logical Sentences.
 */
public class SentenceReaderTest {

  @Test
  public void constantTest() {
    System.out.println(new SentenceReader().parse("true"));
    System.out.println(new SentenceReader().parse("false"));
  }

  @Test
  public void atomTest() {
    System.out.println(new SentenceReader().parse("atom"));
    System.out.println(new SentenceReader().parse("a"));
    System.out.println(new SentenceReader().parse("b"));
  }

  @Test
  public void notTest() {
    System.out.println(new SentenceReader().parse("(not atom)"));
    System.out.println(new SentenceReader().parse("(not a)"));
    System.out.println(new SentenceReader().parse("(not b)"));
  }

  @Test
  public void andTest() {
    System.out.println(new SentenceReader().parse("(and atom1 atom2)"));
    System.out.println(new SentenceReader().parse("(and a b c)"));
    System.out.println(new SentenceReader().parse("(and a1 a2 a3 a4 a5)"));
  }

  @Test
  public void orTest() {
    System.out.println(new SentenceReader().parse("(or atom1 atom2)"));
    System.out.println(new SentenceReader().parse("(or a b c)"));
    System.out.println(new SentenceReader().parse("(or a1 a2 a3 a4 a5)"));
  }

  @Test
  public void impliesTest() {
    System.out.println(new SentenceReader().parse("(implies atom1 atom2)"));
    System.out.println(new SentenceReader().parse("(implies a b)"));
    System.out.println(new SentenceReader().parse("(implies a1 a2)"));
  }

  @Test
  public void iffTest() {
    System.out.println(new SentenceReader().parse("(iff atom1 atom2)"));
    System.out.println(new SentenceReader().parse("(iff a b)"));
    System.out.println(new SentenceReader().parse("(iff a1 a2)"));
  }

  @Test
  public void compoundSentenceTest() {
    System.out.println(new SentenceReader().parse("(and (or atom1 atom2) (not atom3))"));
    System.out.println(new SentenceReader().parse("(and true (iff false true) c)"));
    System.out.println(new SentenceReader().parse("(implies (and true false) (or true false))"));
  }

  @Test
  public void predicateTest() {
    System.out.println(new SentenceReader().parse("(P atom1 atom2)"));
    System.out.println(new SentenceReader().parse("(Q a b)"));
    System.out.println(new SentenceReader().parse("(Predicate a1 a2)"));
  }
}
