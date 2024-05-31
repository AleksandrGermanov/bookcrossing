package ru.astondevs.trainee.bookcrossing.loggingserver.requestlog.mapping;

import ru.astondevs.trainee.bookcrossing.commons.RequestLogDto;
import ru.astondevs.trainee.bookcrossing.loggingserver.requestlog.model.RequestLog;

public interface RequestLogMapper {
    RequestLog toModel(RequestLogDto dto);

    RequestLogDto toDto(RequestLog log);
}
