package ru.astondevs.trainee.bookcrossing.mainsvc.requestlog;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.astondevs.trainee.bookcrossing.commons.RequestLogDto;
import ru.astondevs.trainee.bookcrossing.loggingclient.LoggingClient;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RequestLogControllerImpl implements RequestLogController {
    private final LoggingClient loggingClient;

    @Override
    public List<RequestLogDto> findLogs(Integer from, Integer size) {
        return loggingClient.retrieveRequestLogList(from, size);
    }
}
