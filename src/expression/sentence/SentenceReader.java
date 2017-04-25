package expression.sentence;

import expression.Sort;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * SentenceReader is an interface for the parsing of logical Sentences
 */
public class SentenceReader extends AbstractSentenceReader {

  public LinkedList<String> tokenize(String s) {
    String[] arr = s.split("(?<=[(])|(?=[)])|(?>=[(])|\\s");
    LinkedList<String> l = new LinkedList<>(Arrays.asList(arr));
    l = l.stream().map(String::trim).collect(Collectors.toCollection(LinkedList::new));
    l.removeIf(String::isEmpty);
    return l;
  }

  public Sentence parse(LinkedList<String> stack, Map<String, Variable> quantifiedVars) {
    if (stack.isEmpty())
      return null;

    if (stack.peek().equals("(")) {
      stack.pop();
      String exprName = stack.pop();
      if (QUANTIFIERS.contains(exprName))   // Parse the quantifier
        return parseQuantifier(exprName, stack, quantifiedVars);
      else if (OPERATORS.contains(exprName))  // Parse the proposition
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

    Constant c = Constant.getConstant(exprName);
    if (c == null)
      throw new SentenceParseException("Proposition: " + exprName + " has not been declared.");
    if (c.getSort() != Sort.BOOLEAN)
      throw new SentenceParseException("Cannot create a proposition named " + exprName + ": a conflicting declaration exists.");

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
    String s = stack.pop();
    String exprName, sort;

    if (s.equals("(")) {
      sort = stack.pop();
      exprName = stack.pop();
      stack.pop();
      if (sort == null || exprName == null)
        throw new SentenceParseException("Missing Sort or variable on quantifier.");
    } else {
      exprName = s;
      sort = Sort.OBJECT.getName();
    }

    if (quantifiedVars.containsKey(exprName)) {
      Variable var = quantifiedVars.get(exprName);
      if (var.getSort() != Sort.getSort(sort))
        throw new AbstractSentenceReader.SentenceParseException("Cannot quantify over an existing variable of conflicting sort");
      return var;
    }

    if (Sentence.instances.containsKey(exprName)) {
      Sentence v = Sentence.instances.get(exprName);
      if (!(v instanceof Variable) || v.getSort() != Sort.getSort(sort))
        throw new AbstractSentenceReader.SentenceParseException("Cannot create a variable with a pre-existing name");
      return Sentence.instances.get(exprName);
    }

    if (!Sort.isSort(sort))
      throw new SentenceParseException("Sort " + sort + " does not exist");

    return new Variable(exprName, Sort.getSort(sort));

  }

  protected Sentence parsePredicate(String exprName, LinkedList<String> stack, Map<String, Variable> quantifiedVars) {
    ArrayList<Sentence> list = new ArrayList<>();
    while (!stack.peek().equals(")")) {
      if (stack.peek() == null)
        throw new AbstractSentenceReader.SentenceParseException("Predicate " + exprName + " has no closing parentheses.");
      list.add(parseTerm(stack, quantifiedVars));
    }
    stack.pop();

    if (!Function.functionDeclarations.containsKey(exprName))
      throw new SentenceParseException("Predicate " + exprName + " has not been declared.");

    List<Sort> originalSorts = Function.getDeclaration(exprName),
      sorts = null;
    if (originalSorts != null)
      sorts = new ArrayList<>(originalSorts);
    if (sorts == null || sorts.size() < 1)
      throw new SentenceParseException("Predicate " + exprName + " has not been declared.");
    if (sorts.remove(0) != Sort.BOOLEAN)
      throw new SentenceParseException("Cannot create Predicate " + exprName + " returning a non-Boolean Sort.");
    if (sorts.size() != list.size())
      throw new SentenceParseException("Predicate " + exprName + " has arity " + sorts.size() + ", but given arity " + list.size() + ".");

    for (int i = 0; i < sorts.size(); ++i) {
      if (!list.get(i).getSort().isSubSort(sorts.get(i)))
        throw new SentenceParseException("Argument " + list.get(i).toSExpression() + " in predicate " + exprName
                + " is of Sort " + list.get(i).getSort() + ", but argument of Sort " + sorts.get(i) + " is expected.");
    }

    String s = fullSentenceString(exprName, list);
    if (Sentence.instances.containsKey(s))
      return Sentence.instances.get(s);


    Predicate p = new Predicate(exprName, list);
    Sentence.instances.put(p.toFullSExpression(), p);
    return p;
  }

  protected Sentence parseFunction(String exprName, LinkedList<String> stack, Map<String, Variable> quantifiedVars) {
    ArrayList<Sentence> list = new ArrayList<>();
    while (!stack.peek().equals(")")) {
      if (stack.peek() == null)
        throw new AbstractSentenceReader.SentenceParseException("Function: " + exprName + " has no closing parentheses.");
      list.add(parseTerm(stack, quantifiedVars));
    }
    stack.pop();

    List<Sort> sorts = Function.getDeclaration(exprName);
    if (sorts == null || sorts.size() < 1)
      throw new SentenceParseException("Function: " + exprName + " has not been declared.");

    String s = sentenceString(exprName, list);
    if (Sentence.instances.containsKey(s))
      return Sentence.instances.get(s);
    Sort returnSort = sorts.remove(0);

    if (sorts.size() != list.size())
      throw new SentenceParseException("Function: " + exprName + " has arity " + sorts.size() + ", but given arity " + list.size() + ".");
    IntStream.range(0, sorts.size()).forEach(i -> {
      if (!list.get(i).getSort().isSubSort(sorts.get(i)))
        throw new SentenceParseException("Argument: " + list.get(i).toSExpression() + " to function " + exprName
                + " is of Sort: " + list.get(i).getSort() + ", but argument of Sort " + sorts.get(i) + " is expected.");
    });
    Function f = new Function(exprName, returnSort, list);
    Sentence.instances.put(f.toSExpression(), f);
    return f;
  }

  protected Sentence parseTerm(LinkedList<String> stack, Map<String, Variable> quantifiedVars) {
    String exprName = stack.peek();
    if (exprName.equals("(")) {
      // Parse the term as a function of other terms
      stack.pop();
      exprName = stack.pop();
      return parseFunction(exprName, stack, quantifiedVars);
    }

    if (quantifiedVars.containsKey(exprName)) {
      Variable v = quantifiedVars.get(stack.pop());
      return v;
    }
    if (Sentence.instances.containsKey(exprName))
      return Sentence.instances.get(stack.pop());

    if (exprName.equals(Variable.EMPTY_VAR.getName())) {
      stack.pop();
      return Variable.EMPTY_VAR;
    }

    if (Constant.constantExists(exprName))
      return Constant.getConstant(stack.pop());

    return parseVariable(stack, quantifiedVars);
  }

  public Sentence parseSExpression(String exprName, LinkedList<String> stack, Map<String, Variable> quantifiedVars) {
    ArrayList<Sentence> list = new ArrayList<>();
    while (stack.peek() != null && !stack.peek().equals(")")) {
      if (exprName.equals(Identity.SYMBOL))
        list.add(parseTerm(stack, quantifiedVars));
      else
        list.add(parse(stack, quantifiedVars));
    }
    if (stack.peek() == null)
      throw new AbstractSentenceReader.SentenceParseException("Sentence: " + exprName + " has no closing parentheses.");
    stack.pop();
    return makeSentence(exprName, list);
  }

  public Sentence makeSentence(String name, List<Sentence> args) {
    if (name.isEmpty())
      throw new AbstractSentenceReader.SentenceParseException("Cannot create an Sentence from an empty string.");

    //Check if this Sentence has already been created
    Sentence s = Sentence.instances.get(fullSentenceString(name, args));
    if (s != null)
      return s;

    switch (name) {
      case "not": {
        if (args.size() != 1)
          throw new AbstractSentenceReader.SentenceParseException("Not Sentence must have exactly one argument.\n" + args);
        s = new Not(args.get(0));
        break;
      }
      case "and": {
        if (args.size() < 1)
          throw new AbstractSentenceReader.SentenceParseException("And Sentence must have at least one argument.\n" + args);
        s = new And(args);
        break;
      }
      case "or": {
        if (args.size() < 1)
          throw new AbstractSentenceReader.SentenceParseException("Or Sentence must have at least one argument.\n" + args);
        s = new Or(args);
        break;
      }
      case "implies": {
        if (args.size() != 2)
          throw new AbstractSentenceReader.SentenceParseException("Implies Sentence must have exactly two arguments.\n" + args);
        s = new Implies(args.get(0), args.get(1));
        break;
      }
      case "iff": {
        if (args.size() != 2)
          throw new AbstractSentenceReader.SentenceParseException("Iff Sentence must have exactly two arguments.\n" + args);
        s = new Iff(args.get(0), args.get(1));
        break;
      }
      case "=": {
        if (args.size() != 2)
          throw new SentenceParseException("Identity Sentence must have exactly two arguments.\n" + args);
        s = new Identity(args.get(0), args.get(1));
        break;
      }
      case "forAll": {
        if (args.size() != 2)
          throw new AbstractSentenceReader.SentenceParseException("ForAll Sentence must have exactly two arguments.\n" + args);
        s = new ForAll((Variable) args.get(0), args.get(1));
        break;
      }
      case "exists": {
        if (args.size() != 2)
          throw new AbstractSentenceReader.SentenceParseException("Exists Sentence must have exactly two arguments.\n" + args);
        s = new Exists((Variable) args.get(0), args.get(1));
        break;
      }
      default: {
        List<Sort> l = Function.getDeclaration(name);
        if (l == null || l.isEmpty())
          return null;
        Sort sort = l.remove(0);
        if (l.size() == 1 && args.isEmpty()) {
          if (sort == Sort.BOOLEAN)
            s = new Predicate(name, args);
          else
            s = new Function(name, sort, args);
        } else {
          if (l.size() == args.size() && IntStream.range(0, args.size()).allMatch(i -> args.get(i).getSort().isSubSort(l.get(i)))) {
            if (sort == Sort.BOOLEAN)
              s = new Predicate(name, args);
            else
              s = new Function(name, sort, args);
            break;
          }
          System.out.println(name + " " + args);
          throw new SentenceParseException("Cannot parse Sentence named " + name + ": no matching declaration exists.\n");
        }
      }
    }

    Sentence.instances.putIfAbsent(s.toFullSExpression(), s);
    return s;
  }
}
