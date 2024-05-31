package ru.astondevs.trainee.bookcrossing.loggingserver.requestlog.mapping;

import org.springframework.stereotype.Service;
import ru.astondevs.trainee.bookcrossing.commons.RequestLogDto;
import ru.astondevs.trainee.bookcrossing.loggingserver.requestlog.model.RequestLog;

@Service
public class RequestLogMapperImpl implements RequestLogMapper {

    @Override
    public RequestLog toModel(RequestLogDto dto) {
        return new RequestLog(
                dto.getId(),
                dto.getMethod(),
                dto.getPath(),
                dto.getIp(),
                dto.getParameterMap(),
                dto.getSerializedBody(),
                dto.getCreatedOn()
        );
    }

    @Override
    public RequestLogDto toDto(RequestLog log) {
        return new RequestLogDto(
                log.getId(),
                log.getMethod(),
                log.getPath(),
                log.getIp(),
                log.getParameterMap(),
                log.getSerializedBody(),
                log.getCreatedOn()
        );
    }
}
