package expression.sentence;

import expression.Sort;

import java.util.*;
import java.util.stream.Collectors;

/**
 * SentenceReader is an interface for the parsing of logical Sentences
 */
public interface SentenceReader {

  ArrayList<String> PROPOSITIONS = new ArrayList<>(Arrays.asList("not", "and", "or", "implies", "iff"));
  ArrayList<String> QUANTIFIERS = new ArrayList<>(Arrays.asList("forAll", "exists"));

  static Sentence parse(String s) {
    return parse(tokenize(s), new HashMap<>());
  }

  static LinkedList<String> tokenize(String s) {
    String[] arr = s.split("(?<=[(])|(?=[)])|(?=[)])|(?>=[(])|\\s");
    LinkedList<String> l = new LinkedList<>(Arrays.asList(arr));
    l.removeIf(string -> string.replaceAll("\\s", "").isEmpty());
    //System.out.println(l);
    return l;
  }

  static Sentence parse(LinkedList<String> stack, Map<String, Variable> quantifiedVars) {
    //System.out.println("Parse: " + stack);
    if (stack.isEmpty())
      return null;

    if (stack.peek().equals("(")) {
      stack.pop();
      String exprName = stack.pop();
      if (QUANTIFIERS.contains(exprName))   // Parse the quantifier
        return parseQuantifier(exprName, stack, quantifiedVars);
      else if (PROPOSITIONS.contains(exprName))  // Parse the proposition
        return parseProposition(exprName, stack, quantifiedVars);
      return parsePredicate(exprName, stack, quantifiedVars); // The sentence must now be a Predicate
    } else if (stack.peek().equalsIgnoreCase("true")) {
      stack.pop();
      return BooleanSentence.TRUE;
    } else if (stack.peek().equalsIgnoreCase("false")) {
      stack.pop();
      return BooleanSentence.FALSE;
    } //else if (stack.peek().charAt(0) == stack.peek().toUpperCase().charAt(0)) {

    String s = stack.pop().toUpperCase();
    if (Sentence.instances.containsKey(s))
      return Sentence.instances.get(s);

    Proposition p = new Proposition(s);
    Sentence.instances.putIfAbsent(s, p);
    return p;

    //return new Proposition(stack.pop());
    //}
    //throw new SentenceParseException("Proposition: " + stack.peek() + " must begin with an uppercase character");
  }

  static Sentence parseQuantifier(String exprName, LinkedList<String> stack, Map<String, Variable> quantifiedVars) {
    ArrayList<Sentence> args = new ArrayList<>();

    if (quantifiedVars.containsKey(stack.peek()))
      throw new SentenceParseException("Variable: " + stack.peek() + " quantified over twice.");
    Sentence s = parseVariable(stack, quantifiedVars);
    args.add(s);
    quantifiedVars.put(s.toSExpression(), (Variable) s);

    args.add(parse(stack, quantifiedVars));
    if (stack.peek() == null || !stack.peek().equals(")"))
      throw new SentenceParseException("Missing parentheses after quantifier: " + exprName + " " + args.get(0) + ".");
    stack.pop();
    quantifiedVars.remove(s.toSExpression());
    return makeSentence(exprName, args);
  }

  static Sentence parseVariable(LinkedList<String> stack, Map<String, Variable> quantifiedVars) {
    String exprName = stack.pop();
    String sort = null;
    if (exprName.charAt(0) != exprName.toLowerCase().charAt(0))
      throw new SentenceParseException("Term: " + exprName +
              " must begin with a lowercase character.");
    if (stack.peek().equals(":")) {
      stack.pop();
      sort = stack.pop();
    }
    //System.out.println("ParseVariable: " + stack);
    if (quantifiedVars.containsKey(exprName)) {
      Variable v = quantifiedVars.get(exprName);
      if (sort != null && !v.getSort().isSuperSort(Sort.getSort(sort)))
        throw new SentenceParseException("Cannot create a variable of multiple sorts");
      return v;
    }
    if (sort == null)
      sort = "OBJECT";
    if (Sentence.instances.containsKey(exprName))
      return Sentence.instances.get(exprName);

    Variable v = new Variable(exprName, Sort.getSort(sort));
    Sentence.instances.put(exprName, v);
    return v;
  }

  static Sentence parsePredicate(String exprName, LinkedList<String> stack, Map<String, Variable> quantifiedVars) {
    if (exprName.charAt(0) != exprName.toUpperCase().charAt(0))
      throw new SentenceParseException("Predicate: " + exprName
              + " must begin with an uppercase character.");

    //System.out.println("ParsePredicate: " + stack);
    ArrayList<Sentence> list = new ArrayList<>();
    while (!stack.peek().equals(")")) {
      if (stack.peek() == null)
        throw new SentenceParseException("Sentence: " + exprName + " has no closing parentheses.");
      list.add(parseTerm(stack, quantifiedVars));
    }
    stack.pop();
    String s = sentenceString(exprName, list);
    if (Sentence.instances.containsKey(s))
      return Sentence.instances.get(s);

    Predicate p = new Predicate(exprName, list);
    Sentence.instances.put(p.toSExpression(), p);
    return p;
  }

  static Sentence parseTerm(LinkedList<String> stack, Map<String, Variable> quantifiedVars) {
    String exprName = stack.pop();
    String sort = null;

    if (exprName.charAt(0) != exprName.toLowerCase().charAt(0))
      throw new SentenceParseException("Term: " + exprName
              + " must begin with a lowercase character.");

    if (stack.peek().equals(":")) {
      stack.pop();
      sort = stack.pop();
    }

    if (quantifiedVars.containsKey(exprName)) {
      Variable v = quantifiedVars.get(exprName);
      if (sort != null && !v.getSort().isSuperSort(Sort.getSort(sort)))
        throw new SentenceParseException("Cannot create a variable of multiple sorts");
      return v;
    }

    if (Sentence.instances.containsKey(exprName))
      return Sentence.instances.get(exprName);

    if (sort == null)
      sort = "OBJECT";
    return Constant.getConstant(exprName, Sort.getSort(sort));
  }

  static Sentence parseProposition(String exprName, LinkedList<String> stack, Map<String, Variable> quantifiedVars) {
    //System.out.println("ParseProposition: " + stack);
    ArrayList<Sentence> list = new ArrayList<>();
    while (stack.peek() != null && !stack.peek().equals(")")) {
      list.add(parse(stack, quantifiedVars));
    }
    if (stack.peek() == null)
      throw new SentenceParseException("Sentence: " + exprName + " has no closing parentheses.");
    stack.pop();
    return makeSentence(exprName, list);
  }

  static Sentence makeSentence(String name, ArrayList<Sentence> args) {
    if (name.isEmpty())
      throw new SentenceParseException("Cannot create an Sentence from an empty string.");

    //Check if this Sentence has already been created
    Sentence s = Sentence.instances.get(sentenceString(name, args));
    if (s != null)
      return s;

    switch (name) {
      case "not": {
        if (args.size() != 1)
          throw new SentenceParseException("Not Sentence must have exactly one argument.\n" + args);
        return new Not(args.get(0));
      }
      case "and": {
        if (args.size() < 2)
          throw new SentenceParseException("And Sentence must have at least two arguments.\n" + args);
        return new And(args);
      }
      case "or": {
        if (args.size() < 2)
          throw new SentenceParseException("Or Sentence must have at least two arguments.\n" + args);
        return new Or(args);
      }
      case "implies": {
        if (args.size() != 2)
          throw new SentenceParseException("Implies Sentence must have exactly two arguments.\n" + args);
        return new Implies(args.get(0), args.get(1));
      }
      case "iff": {
        if (args.size() != 2)
          throw new SentenceParseException("Iff Sentence must have exactly two arguments.\n" + args);
        return new Iff(args.get(0), args.get(1));
      }
      case "forAll": {
        return new ForAll((Variable) args.get(0), args.get(1));
      }
      case "exists": {
        return new Exists((Variable) args.get(0), args.get(1));
      }
      default:
        return null;
    }
  }

  static String sentenceString(String name, List<Sentence> args) {
    return "(" + name + args.stream().map(a -> " " + a.toSExpression()).collect(Collectors.joining()) + ")";
  }

  static String sentenceString(String name, Variable var, Sentence s) {
    return "(" + name + " " + var.toSExpression() + " " + s.toSExpression() + ")";
  }


  class SentenceParseException extends RuntimeException {
    private SentenceParseException(String message) {
      super(message);
    }
  }
}
