package exception;

/**
 * @author SIN
 */
public interface ExceptionHandler {

    @FunctionalInterface
    interface ExceptionWithReturn<T> {
        T run() throws Exception;
    }
    @FunctionalInterface
    interface ExceptionWithoutReturn {
        void run() throws Exception;
    }

    <T> T run(ExceptionWithReturn<T> function, String className, String... text);
    void run(ExceptionWithoutReturn function, String className, String... text);
    void handleException(Exception e, String className, String... text);
}