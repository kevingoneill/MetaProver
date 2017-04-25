package metareasoner;

import expression.Expression;
import expression.metasentence.MetaSentence;
import expression.metasentence.MetaSentenceReader;
import expression.sentence.DeclarationParser;
import org.junit.Test;

import java.util.ArrayList;

/**
 * MetaProverTest is used to validate the functionality
 * of the MetaProver class
 */
public class MetaProverTest {
  private static void runProver(ArrayList<String> premises, String goal) {
    ArrayList<String> declarations = new ArrayList<>();
    declarations.add("Boolean φ");
    declarations.add("Boolean ψ");
    runProver(declarations, premises, goal);
  }

  private static void runProver(ArrayList<String> declarations, ArrayList<String> premises, String goal) {
    declarations.forEach(DeclarationParser::parseDeclaration);

    ArrayList<MetaSentence> p = new ArrayList<MetaSentence>() {{
      premises.forEach(premise -> {
        Expression e = MetaSentenceReader.parse(premise);
        if (e instanceof MetaSentence)
          this.add((MetaSentence) e);
        else
          throw new RuntimeException("Cannot input a Sentence into the MetaProver");
      });
    }};

    MetaSentence i = (MetaSentence) MetaSentenceReader.parse(goal);

    MetaProver prover = new MetaProver(p, i);
    prover.run();
    if (!prover.proofFound()) {
      throw new RuntimeException("Proof could not be found!");
    }
  }

  @Test
  public void test1() {
    ArrayList<String> premises = new ArrayList<>();
    premises.add("[EQUIVALENT φ ψ]");

    runProver(premises, "[AND [SUBSUMES φ ψ] [SUBSUMES ψ φ]]");
  }

  @Test
  public void test2() {
    ArrayList<String> premises = new ArrayList<>();
    premises.add("[IS φ TAUTOLOGY]");
    runProver(premises, "[IS (not φ) CONTRADICTION]");
  }

  @Test
  public void test3() {
    ArrayList<String> premises = new ArrayList<>();
    premises.add("[IS φ CONTRADICTION]");
    runProver(premises, "[IS (not φ) TAUTOLOGY]");
  }

  @Test
  public void test4() {
    ArrayList<String> premises = new ArrayList<>();
    premises.add("[IS φ CONTINGENCY]");
    runProver(premises, "[IS (not φ) CONTINGENCY]");
  }

  @Test
  public void test5() {
    ArrayList<String> premises = new ArrayList<>();
    premises.add("[IS ψ TAUTOLOGY]");
    runProver(premises, "[SUBSUMES true ψ]");
  }

  @Test
  public void test6() {
    ArrayList<String> premises = new ArrayList<>();
    premises.add("[IS φ CONTRADICTION]");
    runProver(premises, "[SUBSUMES φ false]");
  }

  @Test
  public void test7() {
    ArrayList<String> premises = new ArrayList<>();
    premises.add("[IS (implies φ ψ) TAUTOLOGY]");
    runProver(premises, "[SUBSUMES φ ψ]");
  }

  @Test
  public void test8() {
    ArrayList<String> premises = new ArrayList<>();
    premises.add("[CONTRADICTORY φ ψ]");
    runProver(premises, "[AND [CONTRARY φ ψ] [SUBCONTRARY φ ψ]]");
  }

  @Test
  public void test9() {
    ArrayList<String> premises = new ArrayList<>();
    premises.add("[EQUIVALENT φ ψ]");
    runProver(premises, "[CONTRADICTORY φ (not ψ)]");
  }


  @Test
  public void test10() {
    ArrayList<String> premises = new ArrayList<>();
    premises.add("[SUBSUMES φ ψ]");
    runProver(premises, "[SUBSUMES (not ψ) (not φ)]");
  }
}
