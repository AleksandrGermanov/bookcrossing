package exception.mismatch;

import exception.BookcrossingException;

public class OwnerMismatchException extends MismatchException {
    public OwnerMismatchException(String message) {
        super(message);
    }
}
