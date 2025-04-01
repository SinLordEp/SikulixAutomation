package exceptions;

public class UnknownElementException extends RuntimeException {
    public UnknownElementException(String message) {
        super(message);
    }
}
