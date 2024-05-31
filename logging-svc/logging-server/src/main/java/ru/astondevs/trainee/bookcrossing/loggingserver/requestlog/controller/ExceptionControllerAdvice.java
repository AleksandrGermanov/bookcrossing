package ru.astondevs.trainee.bookcrossing.loggingserver.requestlog.controller;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ExceptionControllerAdvice {
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleConstraintViolation(ConstraintViolationException e) {
        List<String> violations = e.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.toList());
        log.debug("Входящие данные не прошли валидацию. {}", String.join(" | ", violations));
        return new ResponseEntity<>(
                "Входящие данные не прошли валидацию. " + String.join(" | ", violations),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({MissingServletRequestParameterException.class})
    public ResponseEntity<String> handleBadRequest(Exception e) {
        log.debug("Из-за неверно составленного запроса произошла ошибка {} с сообщением {}.", e.getClass(), e.getMessage());
        log.debug(Arrays.toString(e.getStackTrace()));
        return new ResponseEntity<>("Из-за неверно составленного запроса произошла ошибка " + e.getClass()
                + " с сообщением \"" + e.getMessage() + '\"', HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleUnexpected(Exception e) {
        log.warn("Произошла непредвиденная ошибка {} с сообщением {}.", e.getClass(), e.getMessage());
        log.warn(Arrays.toString(e.getStackTrace()));
        return new ResponseEntity<>("Произошла непредвиденная ошибка " + e.getClass()
                + " с сообщением \"" + e.getMessage() + "\".", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
