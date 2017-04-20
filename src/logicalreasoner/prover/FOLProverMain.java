package logicalreasoner.prover;

import expression.sentence.Sentence;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * FirstOrderMain allows you to run the prover on line-separated
 * files of premises and an goal file
 */
public class FOLProverMain {
  /**
   * Run the prover given premises and goals
   * <p>
   * args[1] - premises txt file (one premise per line)
   * ars[2] - goals txt file (one goal per line)
   *
   * @param args input files for reasoning
   */
  public static void main(String[] args) {
    long startTime = System.nanoTime();

    if (args.length != 1) {
      System.out.println(ProverMain.usage());
      return;
    }

    Set<Sentence> premises = new HashSet<>();
    Sentence goal;

    try {
      goal = ProverMain.readInputFile(args[0], premises);
    } catch (IOException ioe) {
      System.out.println("File not found: " + args[0] + " \n");
      ioe.printStackTrace();
      return;
    }

    Prover prover = new FOLProver(premises, goal, true);
    prover.run();
    System.out.println("\nTime taken: " + ((double) (System.nanoTime() - startTime)) / 1000000000.0 + " seconds.");
  }
}
