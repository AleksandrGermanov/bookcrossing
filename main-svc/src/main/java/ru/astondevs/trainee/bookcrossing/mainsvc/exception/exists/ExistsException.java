package ru.astondevs.trainee.bookcrossing.mainsvc.exception.exists;

import ru.astondevs.trainee.bookcrossing.mainsvc.exception.BookcrossingException;

public class ExistsException extends BookcrossingException {
    public ExistsException(String message) {
        super(message);
    }
}
