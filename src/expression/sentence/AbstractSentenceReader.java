package expression.sentence;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The AbstractSentenceReader class is a template class for
 * an LL(1) recursive descent parser, which transforms S-expressions
 * as Strings into their unique Sentence Objects.
 */
public abstract class AbstractSentenceReader {
  public static ArrayList<String> OPERATORS = new ArrayList<>(Arrays.asList("not", "and", "or", "implies", "iff", "="));
  public static ArrayList<String> QUANTIFIERS = new ArrayList<>(Arrays.asList("forAll", "exists"));

  public Sentence parse(String s) {
    if (s.isEmpty())
      throw new SentenceParseException("Cannot parse an empty string");
    return parse(tokenize(s), new HashMap<>());
  }

  public abstract LinkedList<String> tokenize(String s);

  public abstract Sentence parse(LinkedList<String> stack, Map<String, Variable> quantifiedVars) throws SentenceParseException;

  protected abstract Sentence parseProposition(String exprName) throws SentenceParseException;

  protected abstract Sentence parseQuantifier(String exprName, LinkedList<String> stack, Map<String, Variable> quantifiedVars) throws SentenceParseException;

  protected abstract Sentence parseVariable(LinkedList<String> stack, Map<String, Variable> quantifiedVars) throws SentenceParseException;

  protected abstract Sentence parsePredicate(String exprName, LinkedList<String> stack, Map<String, Variable> quantifiedVars) throws SentenceParseException;

  protected abstract Sentence parseTerm(LinkedList<String> stack, Map<String, Variable> quantifiedVars) throws SentenceParseException;

  protected abstract Sentence parseSExpression(String exprName, LinkedList<String> stack, Map<String, Variable> quantifiedVars) throws SentenceParseException;

  public abstract Sentence makeSentence(String name, List<Sentence> args) throws SentenceParseException;

  static String sentenceString(String name, List<Sentence> args) {
    return "(" + name + args.stream().map(a -> " " + a.toSExpression()).collect(Collectors.joining()) + ")";
  }

  static String fullSentenceString(String name, List<Sentence> args) {
    return "(" + name + args.stream().map(a -> " " + a.toFullSExpression()).collect(Collectors.joining()) + ")";
  }

  static String sentenceString(String name, Variable var, Sentence s) {
    return "(" + name + " (" + var.getSort() + " " + var.toSExpression() + ") " + s.toSExpression() + ")";
  }

  static String fullSentenceString(String name, Variable var, Sentence s) {
    return "(" + name + " " + var.toFullSExpression() + " " + s.toFullSExpression() + ")";
  }

  /**
   * The SentenceParseException is a helper class which
   */
  public static class SentenceParseException extends RuntimeException {
    public SentenceParseException(String message) {
      super(message);
    }
  }
}
