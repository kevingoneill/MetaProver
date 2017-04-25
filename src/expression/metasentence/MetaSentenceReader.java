package expression.metasentence;

import expression.Expression;
import expression.sentence.Sentence;
import expression.sentence.SentenceReader;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * The MetaSentence Reader interface provides functionality for
 * parsing Strings into their appropriate representations as
 * meta-logical constructs
 */
public interface MetaSentenceReader {
  static Expression parse(String s) {
    return parse(tokenize(s));
  }

  static LinkedList<String> tokenize(String s) {
    LinkedList<String> tokenStack = new LinkedList<>();
    StringReader reader = new StringReader(s);
    StreamTokenizer tokenizer = new StreamTokenizer(reader);
    tokenizer.ordinaryChar('(');
    tokenizer.ordinaryChar(')');
    tokenizer.ordinaryChar('[');
    tokenizer.ordinaryChar(']');

    int token;

    try {
      token = tokenizer.nextToken();
    } catch (IOException ioe) {
      return null;
    }

    while (token != StreamTokenizer.TT_EOF) {
      if (tokenizer.ttype == StreamTokenizer.TT_NUMBER) {
        tokenStack.add(Double.toString(tokenizer.nval));
      } else if (tokenizer.ttype == StreamTokenizer.TT_WORD) {
        tokenStack.add(tokenizer.sval);
      } else {
        tokenStack.add(Character.toString((char) token));
      }

      try {
        token = tokenizer.nextToken();
      } catch (IOException ioe) {
        return null;
      }
    }

    return tokenStack;
  }

  static Expression parse(LinkedList<String> stack) {
    //System.out.println("Parse: " + stack);
    if (stack.isEmpty())
      return null;

    if (stack.peek().equals("[")) {
      stack.pop();
      String exprName = stack.pop();
      ArrayList<Expression> args = parseList(exprName, stack);
      stack.pop();
      return makeMetaSentence(exprName, args);
    } else if (stack.peek().equals("(")) {
      stack.pop();
      String exprName = stack.pop();
      return Sentence.makeSentence(new SentenceReader().parseSExpression(exprName, stack, new HashMap<>()).toSExpression());
    } else if (stack.peek().equalsIgnoreCase("TAUTOLOGY")) {
      stack.pop();
      return MetaConstant.TAUTOLOGY;
    } else if (stack.peek().equalsIgnoreCase("CONTRADICTION")) {
      stack.pop();
      return MetaConstant.CONTRADICTION;
    } else if (stack.peek().equalsIgnoreCase("CONTINGENCY")) {
      stack.pop();
      return MetaConstant.CONTINGENCY;
    } else {
      return Sentence.makeSentence(new SentenceReader().parse(stack.pop()).toSExpression());
    }
  }

  static ArrayList<Expression> parseList(String exprName, LinkedList<String> stack) {
    //System.out.println("ParseList: " + stack);
    if (stack.isEmpty())
      throw new MetaSentenceParseException("Error: missing \"]\"");
    ArrayList<Expression> list = new ArrayList<>();
    while (!stack.peek().equals("]")) {
      if (stack.peek() == null)
        throw new MetaSentenceParseException("MetaSentence: " + exprName + " has no closing bracket.");
      list.add(parse(stack));
    }

    return list;
  }

  static MetaSentence makeMetaSentence(String name, ArrayList<Expression> args) {
    if (name.isEmpty())
      throw new MetaSentenceParseException("Cannot create an Sentence from an empty string.");
    switch (name) {
      case "AND": {
        if (args.size() < 2)
          throw new MetaSentenceParseException("AND MetaSentence must have at least two arguments.");
        ArrayList<MetaSentence> metaArgs = new ArrayList<>();
        args.forEach(a -> {
          if (a instanceof MetaSentence)
            metaArgs.add((MetaSentence) a);
          else
            throw new MetaSentenceParseException("All arguments to the AND MetaSentence must be MetaSentences");
        });
        HashSet<TruthAssignmentVar> v = new HashSet<>();
        metaArgs.forEach(a -> v.addAll(a.getVars()));
        return new AND(metaArgs, v);
      }
      case "IFF": {
        if (args.size() != 2)
          throw new MetaSentenceParseException("IFF MetaSentence requires exactly two arguments.");
        ArrayList<MetaSentence> metaArgs = new ArrayList<>();
        args.forEach(a -> {
          if (a instanceof MetaSentence)
            metaArgs.add((MetaSentence) a);
          else
            throw new MetaSentenceParseException("All arguments to the IFF MetaSentence must be MetaSentences");
        });
        HashSet<TruthAssignmentVar> v = new HashSet<>();
        metaArgs.forEach(a -> v.addAll(a.getVars()));
        return new IFF(metaArgs.get(0), metaArgs.get(1), v);
      }
      case "EQUIVALENT": {
        if (args.size() != 2)
          throw new MetaSentenceParseException("EQUIVALENT MetaSentence requires exactly two arguments.");
        ArrayList<Sentence> metaArgs = new ArrayList<>();
        args.forEach(a -> {
          if (a instanceof Sentence)
            metaArgs.add((Sentence) a);
          else
            throw new MetaSentenceParseException("All arguments to the EQUIVALENT MetaSentence must be Sentences");
        });
        return new EQUIVALENT(metaArgs.get(0), metaArgs.get(1));
      }
      case "SUBSUMES": {
        if (args.size() != 2)
          throw new MetaSentenceParseException("SUBSUMES MetaSentence requires exactly two arguments.");
        ArrayList<Sentence> metaArgs = new ArrayList<>();
        args.forEach(a -> {
          if (a instanceof Sentence)
            metaArgs.add((Sentence) a);
          else
            throw new MetaSentenceParseException("All arguments to the SUBSUMES MetaSentence must be Sentences");
        });
        return new SUBSUMES(metaArgs.get(0), metaArgs.get(1));
      }
      case "CONTRADICTORY": {
        if (args.size() != 2)
          throw new MetaSentenceParseException("CONTRADICTORY MetaSentence requires exactly two arguments.");
        ArrayList<Sentence> metaArgs = new ArrayList<>();
        args.forEach(a -> {
          if (a instanceof Sentence)
            metaArgs.add((Sentence) a);
          else
            throw new MetaSentenceParseException("All arguments to the CONTRADICTORY Metasentence must be Sentences");
        });
        return new CONTRADICTORY(metaArgs.get(0), metaArgs.get(1));
      }
      case "CONTRARY": {
        if (args.size() != 2)
          throw new MetaSentenceParseException("CONTRARY MetaSentence requires exactly two arguments.");
        ArrayList<Sentence> metaArgs = new ArrayList<>();
        args.forEach(a -> {
          if (a instanceof Sentence)
            metaArgs.add((Sentence) a);
          else
            throw new MetaSentenceParseException("All arguments to the CONTRARY Metasentence must be Sentences");
        });
        return new CONTRARY(metaArgs.get(0), metaArgs.get(1));
      }
      case "SUBCONTRARY": {
        if (args.size() != 2)
          throw new MetaSentenceParseException("SUBCONTRARY MetaSentence requires exactly two arguments.");
        ArrayList<Sentence> metaArgs = new ArrayList<>();
        args.forEach(a -> {
          if (a instanceof Sentence)
            metaArgs.add((Sentence) a);
          else
            throw new MetaSentenceParseException("All arguments to the SUBCONTRARY Metasentence must be Sentences");
        });
        return new SUBCONTRARY(metaArgs.get(0), metaArgs.get(1));
      }
      case "is":
      case "IS": {
        if (args.size() != 2)
          throw new MetaSentenceParseException("IS MetaSentence requires exactly two arguments.");
        Sentence s = null;
        MetaConstant c = null;
        if (args.get(0) instanceof Sentence)
          s = (Sentence) args.get(0);
        else
          throw new MetaSentenceParseException("First argument to the IS MetaSentence must be a Sentence");
        if (args.get(1) instanceof MetaConstant)
          c = (MetaConstant) args.get(1);
        else
          throw new MetaSentenceParseException("Second arguments to the IS MetaSentence must be a MetaConstant");

        return new IS(s, c);
      }
      default:
        throw new MetaSentenceParseException("Unknown meta-logical operator: " + name);
    }
  }


  class MetaSentenceParseException extends RuntimeException {
    private MetaSentenceParseException(String message) {
      super(message);
    }
  }
}
