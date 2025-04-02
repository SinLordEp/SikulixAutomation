package exceptions;

public class FileIOException extends RuntimeException {
    public FileIOException(String message) {
        super(message);
    }
}
