package exception;

public class ConfigMissingException extends RuntimeException {
    public ConfigMissingException(String message) {
        super(message);
    }
}
