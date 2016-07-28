package logicalreasoner.prover;

import expression.sentence.Sentence;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

/**
 * FirstOrderMain allows you to run the prover on line-separated
 * files of premises and an interest file
 */
public class FirstOrderProverMain {
  /**
   * Run the prover given premises and interests
   * <p>
   * args[1] - premises txt file (one premise per line)
   * ars[2] - interests txt file (one interest per line)
   *
   * @param args input files for reasoning
   */
  public static void main(String[] args) {
    long startTime = System.nanoTime();

    if (args.length != 2) {
      System.out.println(Arrays.asList(args));
      System.out.println(SemanticProverMain.usage(args[0]));
      return;
    }

    Set<Sentence> premises;
    Sentence interest;

    try {
      premises = SemanticProverMain.readSentences(args[0]);
    } catch (IOException ioe) {
      System.out.println("Exception while reading premises: \n");
      ioe.printStackTrace();
      return;
    }

    try {
      interest = SemanticProverMain.readSentence(args[1]);
    } catch (IOException ioe) {
      System.out.println("Exception while reading premises: \n");
      ioe.printStackTrace();
      return;
    }

    SemanticProver prover = new FirstOrderProver(premises, interest, true);
    prover.run();

    System.out.println("\nTime taken: " + ((double) (System.nanoTime() - startTime)) / 1000000000.0 + " seconds.");
  }
}
