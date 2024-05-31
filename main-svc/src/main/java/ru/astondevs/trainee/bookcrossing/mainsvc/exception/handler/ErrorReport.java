package ru.astondevs.trainee.bookcrossing.mainsvc.exception.handler;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ErrorReport {
    private LocalDateTime created;
    private String message;
    private int code;
}

