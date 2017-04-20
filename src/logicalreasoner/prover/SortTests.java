package logicalreasoner.prover;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * These tests are meant to test the functionality of many-sorted
 * first order logic. Test problems were created manually.
 */
public class SortTests {

  private static void runProver(List<String> d, Set<String> p, String s, boolean b) {
    FOLTests.runProver(d, p, s, b);
  }

  @Test
  public void test1() {
    List<String> declarations = new ArrayList<>();
    declarations.add("declare-sort Man Object");
    declarations.add("Man Socrates");
    declarations.add("Boolean Mortal Object");
    Set<String> premises = new HashSet<>();
    premises.add("(forAll (Man x) (Mortal x))");
    runProver(declarations, premises, "(Mortal Socrates)", true);

    declarations.add("declare-sort Woman Object");
    declarations.add("Woman SocratesWife");
    runProver(declarations, premises, "(Mortal SocratesWife)", false);
  }

  @Test
  public void test2() {
    List<String> declarations = new ArrayList<>();
    declarations.add("Object Zoey");
    declarations.add("Object Mel");
    declarations.add("Boolean TellsTruth Object");
    declarations.add("declare-sort Knight Object");
    declarations.add("declare-sort Knave Object");

    Set<String> premises = new HashSet<>();
    premises.add("(forAll (Knight x) (TellsTruth x))");
    premises.add("(forAll (Knave x) (not (TellsTruth x)))");


    runProver(declarations, premises, "(TellsTruth Zoey)", false);
  }
}
