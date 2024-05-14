package exception.mismatch;

import exception.BookcrossingException;

public abstract class MismatchException extends BookcrossingException {

    public MismatchException(String message) {
        super(message);
    }
}
