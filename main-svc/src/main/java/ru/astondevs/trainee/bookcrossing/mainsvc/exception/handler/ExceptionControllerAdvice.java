package ru.astondevs.trainee.bookcrossing.mainsvc.exception.handler;


import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.astondevs.trainee.bookcrossing.mainsvc.exception.exists.ExistsException;
import ru.astondevs.trainee.bookcrossing.mainsvc.exception.mismatch.MismatchException;
import ru.astondevs.trainee.bookcrossing.mainsvc.exception.notfound.NotFoundException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    private ErrorReport handleNotFound(NotFoundException e) {
        return new ErrorReport(
                LocalDateTime.now(),
                String.format("Произошла ошибка %s c сообщением %s",
                        e.getClass().getName(), e.getMessage()),
                404
        );
    }

    @ExceptionHandler(ExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    private ErrorReport handleExists(ExistsException e) {

        return new ErrorReport(
                LocalDateTime.parse(LocalDateTime.now().toString()),
                String.format("Произошла ошибка %s c сообщением %s",
                        e.getClass().getName(), e.getMessage()),
                409
        );
    }

    @ExceptionHandler({ConstraintViolationException.class,
            MethodArgumentNotValidException.class,
            TransactionSystemException.class,
            MismatchException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    private ErrorReport handleForbidden(Exception e) {

        return new ErrorReport(
                LocalDateTime.parse(LocalDateTime.now().toString()),
                String.format("Произошла ошибка %s c сообщением %s",
                        e.getClass().getName(), e.getMessage()),
                403
        );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)

    private ErrorReport handleBadRequest(MissingServletRequestParameterException e) {
        return new ErrorReport(
                LocalDateTime.parse(LocalDateTime.now().toString()),
                String.format("Произошла ошибка %s c сообщением %s",
                        e.getClass().getName(), e.getMessage()),
                400
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    private ErrorReport handleUnexpectedException(Exception e) {

        return new ErrorReport(
                LocalDateTime.parse(LocalDateTime.now().toString()),
                String.format("Произошла непредвиденная ошибка %s c сообщением %s",
                        e.getClass().getName(), e.getMessage()),
                500
        );
    }
}

