package exceptions;

public class TargetNotFoundException extends RuntimeException {
    public TargetNotFoundException(String message) {
        super(message);
    }
}
