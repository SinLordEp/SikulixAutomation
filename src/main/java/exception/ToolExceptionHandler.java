package exception;

public class ToolExceptionHandler implements ExceptionHandler {

    @Override
    public <T> T handle(ExceptionWithReturn<T> function, String className, String... text) {
        return null;
    }

    @Override
    public void handle(ExceptionWithoutReturn function, String className, String... text) {

    }
}
