package ru.astondevs.trainee.bookcrossing.mainsvc.util.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.stereotype.Component;
import ru.astondevs.trainee.bookcrossing.commons.RequestLogDto;
import ru.astondevs.trainee.bookcrossing.loggingclient.LoggingClient;

import java.util.Arrays;

@Component
@Slf4j
@RequiredArgsConstructor
public class RequestAppender extends AppenderBase<ILoggingEvent> {
    private LoggingClient loggingClient;

    @Autowired
    public void setLoggingClient(LoggingClient loggingClient) {
        this.loggingClient = loggingClient;
    }

    @Override
    protected void append(ILoggingEvent iLoggingEvent) {
        RequestLogDto requestLogDto = getRequestLog(iLoggingEvent);
        if (requestLogDto == null) {
            return;
        }

        loggingClient.saveRequestLog(requestLogDto);
    }

    private RequestLogDto getRequestLog(ILoggingEvent iLoggingEvent) {
        Object requestLogObj = Arrays.stream(iLoggingEvent.getArgumentArray())
                .filter(o -> o instanceof RequestLogDto)
                .findFirst()
                .orElse(null);

        return requestLogObj != null
                ? (RequestLogDto) requestLogObj
                : null;
    }
}

