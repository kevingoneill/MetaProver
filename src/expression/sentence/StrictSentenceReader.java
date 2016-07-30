package expression.sentence;

import java.util.LinkedList;
import java.util.Map;

/**
 * StrictSentenceReader is a SentenceReader which enforces
 * basic notation of logical statements-
 * <p>
 * Constants must begin with a lowercase letter (or numeral)
 * Variables must begin with a lowercase letter
 * Propositions and Predicates must begin with an uppercase letter
 */
public class StrictSentenceReader extends SentenceReader {

  /**
   * Parse a propositional variable, ensuring that it begins
   * with an uppercase letter
   *
   * @param exprName the Proposition string to parse
   * @return a corresponding Proposition Object
   * @throws SentenceParseException if the first character in exprName is not an uppercase letter
   */
  protected Sentence parseProposition(String exprName) throws SentenceParseException {
    if (Character.isLetter(exprName.charAt(0)) && Character.isUpperCase(exprName.charAt(0)))
      return super.parseProposition(exprName);

    throw new AbstractSentenceReader.SentenceParseException("Proposition: " + exprName + " must begin with an uppercase character");
  }

  protected Sentence parsePredicate(String exprName, LinkedList<String> stack, Map<String, Variable> quantifiedVars)
          throws SentenceParseException {
    if (Character.isLetter(exprName.charAt(0)) && Character.isUpperCase(exprName.charAt(0)))
      return super.parsePredicate(exprName, stack, quantifiedVars);

    throw new AbstractSentenceReader.SentenceParseException("Predicate: " + exprName + " must begin with an uppercase character.");
  }

  protected Sentence parseTerm(LinkedList<String> stack, Map<String, Variable> quantifiedVars) throws SentenceParseException {
    String exprName = stack.peek();

    if (Character.isLowerCase(exprName.charAt(0)) || !Character.isLetter(exprName.charAt(0)))
      return super.parseTerm(stack, quantifiedVars);

    throw new AbstractSentenceReader.SentenceParseException("Term: " + exprName + " must begin with a lowercase character.");
  }
}
