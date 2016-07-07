package expression.sentence;

import expression.Sort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;

/**
 * SentenceReader is an interface for the parsing of logical Sentences
 */
public class SentenceReader extends AbstractSentenceReader {

  protected LinkedList<String> tokenize(String s) {
    String[] arr = s.split("(?<=[(])|(?=[)])|(?=[)])|(?>=[(])|\\s");
    LinkedList<String> l = new LinkedList<>(Arrays.asList(arr));
    l.removeIf(string -> string.replaceAll("\\s", "").isEmpty());
    //System.out.println(l);
    return l;
  }

  protected Sentence parse(LinkedList<String> stack, Map<String, Variable> quantifiedVars) {
    if (stack.isEmpty())
      return null;

    if (stack.peek().equals("(")) {
      stack.pop();
      String exprName = stack.pop();
      if (QUANTIFIERS.contains(exprName))   // Parse the quantifier
        return parseQuantifier(exprName, stack, quantifiedVars);
      else if (PROPOSITIONS.contains(exprName))  // Parse the proposition
        return parseSExpression(exprName, stack, quantifiedVars);
      return parsePredicate(exprName, stack, quantifiedVars); // The sentence must now be a Predicate
    } else if (stack.peek().equalsIgnoreCase("true") || stack.peek().equals("⊤")) {
      stack.pop();
      return BooleanSentence.TRUE;
    } else if (stack.peek().equalsIgnoreCase("false") || stack.peek().equals("⊥")) {
      stack.pop();
      return BooleanSentence.FALSE;
    }

    return parseProposition(stack.pop());
  }

  protected Sentence parseProposition(String exprName) {
    Sentence s = Sentence.instances.get(exprName);
    if (s != null)
      return s;

    Proposition p = new Proposition(exprName);
    Sentence.instances.putIfAbsent(exprName, p);
    return p;
  }

  protected Sentence parseQuantifier(String exprName, LinkedList<String> stack, Map<String, Variable> quantifiedVars) {
    ArrayList<Sentence> args = new ArrayList<>();

    if (quantifiedVars.containsKey(stack.peek()))
      throw new AbstractSentenceReader.SentenceParseException("Variable: " + stack.peek() + " quantified over twice.");

    Sentence s = parseVariable(stack, quantifiedVars);
    args.add(s);
    quantifiedVars.put(s.toSExpression(), (Variable) s);

    args.add(parse(stack, quantifiedVars));
    if (stack.peek() == null || !stack.peek().equals(")"))
      throw new AbstractSentenceReader.SentenceParseException("Missing parentheses after quantifier: " + exprName + " " + args.get(0) + ".");
    stack.pop();
    quantifiedVars.remove(s.toSExpression());
    return makeSentence(exprName, args);
  }

  protected Sentence parseVariable(LinkedList<String> stack, Map<String, Variable> quantifiedVars) {
    String exprName = stack.pop();
    String sort = null;

    if (stack.peek().equals(":")) {
      stack.pop();
      sort = stack.pop();
    }

    if (quantifiedVars.containsKey(exprName)) {
      Variable v = quantifiedVars.get(exprName);
      if (sort != null && !v.getSort().isSuperSort(Sort.getSort(sort)))
        throw new AbstractSentenceReader.SentenceParseException("Cannot create a variable of multiple sorts");
      return v;
    }
    if (sort == null)
      sort = "Object";
    if (Sentence.instances.containsKey(exprName)) {
      if (!(Sentence.instances.get(exprName) instanceof Variable))
        throw new AbstractSentenceReader.SentenceParseException("Cannot create a variable with a pre-existing name");
      return Sentence.instances.get(exprName);
    }

    Variable v = new Variable(exprName, Sort.getSort(sort));
    Sentence.instances.put(exprName, v);
    return v;
  }

  protected Sentence parsePredicate(String exprName, LinkedList<String> stack, Map<String, Variable> quantifiedVars) {
    ArrayList<Sentence> list = new ArrayList<>();
    while (!stack.peek().equals(")")) {
      if (stack.peek() == null)
        throw new AbstractSentenceReader.SentenceParseException("Sentence: " + exprName + " has no closing parentheses.");
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

  protected Sentence parseTerm(LinkedList<String> stack, Map<String, Variable> quantifiedVars) {
    String exprName = stack.pop();
    String sort = null;

    if (stack.peek().equals(":")) {
      stack.pop();
      sort = stack.pop();
    }

    if (quantifiedVars.containsKey(exprName)) {
      Variable v = quantifiedVars.get(exprName);
      if (sort != null && !v.getSort().isSuperSort(Sort.getSort(sort)))
        throw new AbstractSentenceReader.SentenceParseException("Cannot create a variable of multiple sorts");
      return v;
    }

    if (Sentence.instances.containsKey(exprName))
      return Sentence.instances.get(exprName);

    if (sort == null)
      sort = "Object";
    return Constant.getConstant(exprName, Sort.getSort(sort));
  }

  public Sentence parseSExpression(String exprName, LinkedList<String> stack, Map<String, Variable> quantifiedVars) {
    ArrayList<Sentence> list = new ArrayList<>();
    while (stack.peek() != null && !stack.peek().equals(")")) {
      list.add(parse(stack, quantifiedVars));
    }
    if (stack.peek() == null)
      throw new AbstractSentenceReader.SentenceParseException("Sentence: " + exprName + " has no closing parentheses.");
    stack.pop();
    return makeSentence(exprName, list);
  }

  protected Sentence makeSentence(String name, ArrayList<Sentence> args) {
    if (name.isEmpty())
      throw new AbstractSentenceReader.SentenceParseException("Cannot create an Sentence from an empty string.");

    //Check if this Sentence has already been created
    Sentence s = Sentence.instances.get(sentenceString(name, args));
    if (s != null)
      return s;

    switch (name) {
      case "not": {
        if (args.size() != 1)
          throw new AbstractSentenceReader.SentenceParseException("Not Sentence must have exactly one argument.\n" + args);
        return new Not(args.get(0));
      }
      case "and": {
        if (args.size() < 1)
          throw new AbstractSentenceReader.SentenceParseException("And Sentence must have at least one argument.\n" + args);
        return new And(args);
      }
      case "or": {
        if (args.size() < 1)
          throw new AbstractSentenceReader.SentenceParseException("Or Sentence must have at least one argument.\n" + args);
        return new Or(args);
      }
      case "implies": {
        if (args.size() != 2)
          throw new AbstractSentenceReader.SentenceParseException("Implies Sentence must have exactly two arguments.\n" + args);
        return new Implies(args.get(0), args.get(1));
      }
      case "iff": {
        if (args.size() != 2)
          throw new AbstractSentenceReader.SentenceParseException("Iff Sentence must have exactly two arguments.\n" + args);
        return new Iff(args.get(0), args.get(1));
      }
      case "forAll": {
        if (args.size() != 2)
          throw new AbstractSentenceReader.SentenceParseException("ForAll Sentence must have exactly two arguments.\n" + args);
        return new ForAll((Variable) args.get(0), args.get(1));
      }
      case "exists": {
        if (args.size() != 2)
          throw new AbstractSentenceReader.SentenceParseException("Exists Sentence must have exactly two arguments.\n" + args);
        return new Exists((Variable) args.get(0), args.get(1));
      }
      default:
        return null;
    }
  }
}
