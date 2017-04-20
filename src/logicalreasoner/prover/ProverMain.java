package logicalreasoner.prover;

import expression.sentence.DeclarationParser;
import expression.sentence.ParserException;
import expression.sentence.Sentence;
import expression.sentence.SentenceReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * This is the main driver class for running the Semantic Logical Reasoner
 */
public class ProverMain {

  /**
   * Run the prover given premises and goal provided
   * by the input file
   * <p>
   * args[1] - the input file to read formulae from,
   * using the lisp-macro-like expressions
   *
   * (declarations [Sort/Function Declarations]*)
   * (premises [Propositional Sentences]*)
   * (goal [Propositional Sentence])
   *
   * Syntax of Sort/Function/Constant declarations is given
   * by DeclarationParser. Syntax for propositional sentences
   * is given by SentenceReader. All commands follow standard
   * S-Expression syntax.
   *
   * @param args input file for reasoning
   */
  public static void main(String[] args) {
    long startTime = System.nanoTime();
    if (args.length == 0)
      throw new IllegalArgumentException("Please provide an input file to read problems from. ");
    if (args.length != 1) {
      System.out.println(usage());
      return;
    }

    Set<Sentence> premises = new HashSet<>();
    Sentence goal;

    try {
      goal = readInputFile(args[0], premises);
    } catch (IOException ioe) {
      System.out.println("File not found: " + args[0] + " \n");
      ioe.printStackTrace();
      return;
    }

    Prover prover = new Prover(premises, goal, true);
    prover.run();
    System.out.println("\nTime taken: " + ((double) (System.nanoTime() - startTime)) / 1000000000.0 + " seconds.");
  }

  public static String usage() {
    return "usage: java ProverMain <inputFile>\n";
  }

  public static Sentence readInputFile(String fileName, Set<Sentence> premises) throws FileNotFoundException {
    return readInputFile(new File(fileName), premises);
  }

  public static Sentence readInputFile(File inputFile, Set<Sentence> premises) throws FileNotFoundException {
    Sentence goal;
    Scanner scanner = new Scanner(inputFile).useDelimiter("\\Z");
    String file = scanner.next().replaceAll("[;].*?\\n", "");
    scanner.close();

    SentenceReader reader = new SentenceReader();
    LinkedList<String> stack = reader.tokenize(file);

    if (stack.isEmpty())
      throw new ParserException("Input file is empty.\n");
    if (!stack.pop().equals("(") || stack.isEmpty() || !stack.pop().equals("declarations"))
      throw new ParserException("Missing declarations command.\n");
    parseDeclarations(stack);

    if (stack.isEmpty() || !stack.pop().equals("(") || stack.isEmpty() || !stack.pop().equals("premises"))
      throw new ParserException("Missing premises command.\n");

    // parse premises
    while (!stack.peek().equals(")")) {
      if (stack.isEmpty())
        throw new ParserException("Missing closing parenthesis after premises. \n");
      premises.add(reader.parse(stack, new HashMap<>()));
    }
    stack.pop();

    if (stack.isEmpty() || !stack.pop().equals("(") || stack.isEmpty() || !stack.pop().equals("goal"))
      throw new ParserException("Missing goal command.\n");
    goal = reader.parse(stack, new HashMap<>());
    stack.pop();

    if (!stack.isEmpty())
      throw new ParserException("Unparsed text after goal.");
    return goal;
  }

  public static void parseDeclarations(LinkedList<String> stack) {
    StringBuilder decl = new StringBuilder();
    while (!stack.peek().equals(")")) {  // While not at end of macro
      if (stack.isEmpty())
        throw new ParserException("Missing closing parenthesis after declarations. \n");

      //loop for every definition in the macro
      while (stack.peek().equals("(")) {
        stack.pop();
        while (!stack.peek().equals(")"))
          decl.append(stack.pop()).append(" ");
        if (!stack.peek().equals(")"))
          throw new ParserException("Missing closing parenthesis after declaration: " + decl + " \n");
        stack.pop();
        DeclarationParser.parseDeclaration(decl.toString());
        decl = new StringBuilder();
      }
    }
    stack.pop();
  }

}
