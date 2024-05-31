package ru.astondevs.trainee.bookcrossing.mainsvc.exception.notfound;

import ru.astondevs.trainee.bookcrossing.mainsvc.exception.BookcrossingException;

public abstract class NotFoundException extends BookcrossingException {
    public NotFoundException(String message) {
        super(message);
    }
}
