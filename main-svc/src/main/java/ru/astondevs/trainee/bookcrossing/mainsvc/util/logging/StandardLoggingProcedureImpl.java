package ru.astondevs.trainee.bookcrossing.mainsvc.util.logging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import ru.astondevs.trainee.bookcrossing.commons.RequestLogDto;
import ru.astondevs.trainee.bookcrossing.mainsvc.exception.BookcrossingException;

@Component
@RequiredArgsConstructor
public class StandardLoggingProcedureImpl implements StandardLoggingProcedure {
    private final ObjectMapper objectMapper;

    @Override
    public void logRequest(Logger log, HttpServletRequest request, Object body) {
        RequestLogDto requestLogDto = new RequestLogDto(request);

        addParameterMap(request, requestLogDto);
        addBody(body, requestLogDto);

        log.info("{}", requestLogDto);
    }

    @Override
    public void logRequest(Logger log, HttpServletRequest request) {
        logRequest(log, request, null);
    }

    private void addParameterMap(HttpServletRequest request, RequestLogDto requestLogDto) {
        if (request.getParameterMap().isEmpty()) {
            return;
        }

        try {
            String mapString = objectMapper.writeValueAsString(request.getParameterMap());
            requestLogDto.setParameterMap(mapString);
        } catch (JsonProcessingException e) {
            throw new BookcrossingException(e.getClass().getName() + " : " + e.getMessage());
        }
    }

    private void addBody(Object body, RequestLogDto requestLogDto) {
        if (body == null) {
            return;
        }

        try {
            String bodyString = objectMapper.writeValueAsString(body);
            requestLogDto.setSerializedBody(bodyString);
        } catch (JsonProcessingException e) {
            throw new BookcrossingException(e.getClass().getName() + " : " + e.getMessage());
        }
    }
}
