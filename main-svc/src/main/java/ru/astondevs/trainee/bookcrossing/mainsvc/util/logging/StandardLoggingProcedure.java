package ru.astondevs.trainee.bookcrossing.mainsvc.util.logging;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;

public interface StandardLoggingProcedure {
    void logRequest(Logger log, HttpServletRequest request, Object body);

    void logRequest(Logger log, HttpServletRequest request);
}
