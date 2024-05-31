package ru.astondevs.trainee.bookcrossing.mainsvc.exception.mismatch;

import ru.astondevs.trainee.bookcrossing.mainsvc.exception.BookcrossingException;

public abstract class MismatchException extends BookcrossingException {

    public MismatchException(String message) {
        super(message);
    }
}
