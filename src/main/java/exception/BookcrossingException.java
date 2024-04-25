package exception;

public abstract class BookcrossingException extends RuntimeException {
    public BookcrossingException(String message) {
        super(message);
    }
}
