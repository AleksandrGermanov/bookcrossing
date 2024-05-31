package ru.astondevs.trainee.bookcrossing.mainsvc.util.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.astondevs.trainee.bookcrossing.loggingclient.LoggingClientImpl;

@Data
@Configuration
@ConfigurationProperties(prefix = "logging-server")
@RequiredArgsConstructor
public class LoggingClientBeanRegistration {
    private final ObjectMapper objectMapper;
    private String url;


    @Bean(name = "loggingClient")
    public LoggingClientImpl registerLoggingClient() {
        return new LoggingClientImpl(url, objectMapper);
    }
}
