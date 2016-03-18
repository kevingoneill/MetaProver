package sentence;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * SentenceReader is an interface for the parsing of logical Sentences
 */
public interface SentenceReader {

    static Sentence parse(String s) {
        return parse(tokenize(s));
    }

    static LinkedList<String> tokenize(String s) {
        LinkedList<String> tokenStack = new LinkedList<>();
        StringReader reader = new StringReader(s);
        StreamTokenizer tokenizer = new StreamTokenizer(reader);
        tokenizer.ordinaryChar('(');
        tokenizer.ordinaryChar(')');

        int token;

        try {
            token = tokenizer.nextToken();
        } catch (IOException ioe) {
            return null;
        }

        while(token != StreamTokenizer.TT_EOF) {
            if(tokenizer.ttype == StreamTokenizer.TT_NUMBER) {
                tokenStack.add(Double.toString(tokenizer.nval));
            } else if(tokenizer.ttype == StreamTokenizer.TT_WORD) {
                tokenStack.add(tokenizer.sval);
            } else {
                tokenStack.add(Character.toString((char)token));
            }

            try {
                token = tokenizer.nextToken();
            } catch (IOException ioe) {
                return null;
            }
        }

        return tokenStack;
    }

    static Sentence parse(LinkedList<String> stack) {
        //System.out.println("Parse: " + stack);
        if (stack.isEmpty())
            return null;

        if (stack.peek().equals("(")) {
            stack.pop();
            String exprName = stack.pop();
            ArrayList<Sentence> args = parseList(exprName, stack);
            stack.pop();
            return makeSentence(exprName, args);
        } else if (stack.peek().equals("true")) {
            stack.pop();
            return Constant.TRUE;
        } else if (stack.peek().equals("false")) {
            stack.pop();
            return Constant.FALSE;
        } else {
            return new Atom(stack.pop());
        }
    }

    static ArrayList<Sentence> parseList(String exprName, LinkedList<String> stack) {
        //System.out.println("ParseList: " + stack);
        ArrayList<Sentence> list = new ArrayList<>();
        while (!stack.peek().equals(")")) {
            if (stack.peek() == null)
                throw new SentenceParseException("Sentence: " + exprName + " has no closing parentheses.");
            list.add(parse(stack));
        }

        return list;
    }

    static Sentence makeSentence(String name, ArrayList<Sentence> args) {
        if (name.isEmpty())
            throw new SentenceParseException("Cannot create an Sentence from an empty string.");
        switch(name) {
            case "not": {
                if (args.size() != 1)
                    throw new SentenceParseException("Not Sentence must have exactly one argument.");
                return new Not(args.get(0));
            } case "and": {
                if (args.size() < 2)
                    throw new SentenceParseException("And Sentence must have at least two arguments.");
                return new And(args);
            } case "or": {
                if (args.size() < 2)
                    throw new SentenceParseException("Or Sentence must have at least two arguments.");
                return new Or(args);
            } case "implies": {
                if (args.size() != 2)
                    throw new SentenceParseException("Implies Sentence must have exactly two arguments.");
                return new Implies(args.get(0), args.get(1));
            } case "iff": {
                if (args.size() != 2)
                    throw new SentenceParseException("Iff Sentence must have exactly two arguments.");
                return new Iff(args.get(0), args.get(1));
            } default:
                return new Predicate(name, args);
        }
    }


    class SentenceParseException extends RuntimeException {
        private SentenceParseException(String message) { super(message); }
    }
}
