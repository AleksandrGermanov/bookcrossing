package exception.notfound;

import exception.BookcrossingException;

public class SqlFileExecutionException extends BookcrossingException {
    public SqlFileExecutionException(String message) {
        super(message);
    }
}
