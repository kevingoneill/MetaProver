package expression.sentence;

/**
 * A simple Exception class to deal with parse errors.
 */

public class ParserException extends RuntimeException {
    public ParserException(String message) {
        super(message);
    }
}