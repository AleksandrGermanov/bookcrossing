package ru.astondevs.trainee.bookcrossing.mainsvc.util.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import ru.astondevs.trainee.bookcrossing.loggingclient.LoggingClientImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class LoggingClientBeanRegistrationTest {
    private final Environment environment;
    private final ApplicationContext applicationContext;

    @Test
    void getLoggingClient() {
        LoggingClientImpl loggingClientImpl = (LoggingClientImpl) applicationContext.getBean("loggingClient");
        assertEquals(environment.getProperty("logging-server.url"), loggingClientImpl.getLoggingServerUrl());
        assertEquals(applicationContext.getBean(ObjectMapper.class), loggingClientImpl.getObjectMapper());
    }
}