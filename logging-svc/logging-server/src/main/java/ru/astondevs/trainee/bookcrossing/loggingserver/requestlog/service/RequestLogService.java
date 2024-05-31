package ru.astondevs.trainee.bookcrossing.loggingserver.requestlog.service;

import ru.astondevs.trainee.bookcrossing.commons.RequestLogDto;

import java.util.List;

public interface RequestLogService {
    void saveRequestLog(RequestLogDto requestLogDto);

    List<RequestLogDto> retrieveRequestLogList(Integer from, Integer size);
}
