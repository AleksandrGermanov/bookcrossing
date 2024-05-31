package ru.astondevs.trainee.bookcrossing.loggingclient;

public class LoggingClientException extends RuntimeException {
    public LoggingClientException(String message) {
        super(message);
    }

    public LoggingClientException(String message, Throwable cause) {
        super(message, cause);
    }

}
