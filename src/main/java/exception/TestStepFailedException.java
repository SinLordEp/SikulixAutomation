package exception;

public class TestStepFailedException extends RuntimeException {
    public TestStepFailedException(String message) {
        super(message);
    }
}
