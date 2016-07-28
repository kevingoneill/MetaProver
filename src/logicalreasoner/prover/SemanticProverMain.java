package logicalreasoner.prover;

import expression.sentence.Sentence;
import expression.sentence.SentenceParser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * This is the main driver class for running the Semantic Logical Reasoner
 */
public class SemanticProverMain {

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

    if (args.length != 3) {
      System.out.println(usage(args[0]));
      return;
    }

    Set<Sentence> premises;
    Sentence interest;

    try {
      readDeclarations(args[0]);
    } catch (IOException ioe) {
      System.out.println("Exception while reading declarations: \n");
      ioe.printStackTrace();
      return;
    }

    try {
      premises = readSentences(args[1]);
    } catch (IOException ioe) {
      System.out.println("Exception while reading premises: \n");
      ioe.printStackTrace();
      return;
    }

    try {
      interest = readSentence(args[2]);
    } catch (IOException ioe) {
      System.out.println("Exception while reading interest: \n");
      ioe.printStackTrace();
      return;
    }

    SemanticProver prover = new SemanticProver(premises, interest, true);
    prover.run();

    System.out.println("\nTime taken: " + ((double) (System.nanoTime() - startTime)) / 1000000000.0 + " seconds.");
  }

  public static String usage(String arg0) {
    return "usage: java " + arg0 + " <declarationsFile> <premisesFile> <interestsFile>";
  }

  public static void readDeclarations(String fileName) throws IOException {
    BufferedReader reader = new BufferedReader(new FileReader(fileName));
    String line;

    while ((line = reader.readLine()) != null) {
      if (!SentenceParser.ParseDeclaration(line))
        throw new RuntimeException("Failed to parse declaration: " + line);
    }
  }

  public static Set<Sentence> readSentences(String fileName) throws IOException {
    BufferedReader reader = new BufferedReader(new FileReader(fileName));
    String line;

    Set<Sentence> sentences = new HashSet<>();

    while ((line = reader.readLine()) != null) {
      sentences.add(Sentence.makeSentence(line));
    }
    return sentences;
  }

  public static Sentence readSentence(String fileName) throws IOException {
    BufferedReader reader = new BufferedReader(new FileReader(fileName));
    String line;

    if ((line = reader.readLine()) != null) {
      return Sentence.makeSentence(line);
    }

    return null;
  }
}
