package ru.astondevs.trainee.bookcrossing.loggingclient;

import org.springframework.http.ResponseEntity;
import ru.astondevs.trainee.bookcrossing.commons.RequestLogDto;

import java.util.List;

public interface LoggingClient {
    ResponseEntity<Void> saveRequestLog(RequestLogDto requestLogDto);

    List<RequestLogDto> retrieveRequestLogList(Integer from, Integer size);
}
