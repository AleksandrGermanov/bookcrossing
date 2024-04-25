package exception.notfound;

import exception.BookcrossingException;

public abstract class NotFoundException extends BookcrossingException {
    public NotFoundException(String message) {
        super(message);
    }
}
