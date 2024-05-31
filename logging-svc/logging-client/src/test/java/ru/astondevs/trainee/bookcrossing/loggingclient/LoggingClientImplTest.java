package ru.astondevs.trainee.bookcrossing.loggingclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import ru.astondevs.trainee.bookcrossing.commons.RequestLogDto;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class LoggingClientImplTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    LoggingClientImpl client;
    private MockRestServiceServer mockServer;
    private RequestLogDto requestLogDto;

    @BeforeEach
    public void setup() {
        client = new LoggingClientImpl("http://root.root", objectMapper);
        mockServer = MockRestServiceServer.createServer(client.getRestTemplate());
        Timestamp createdOn = Timestamp.valueOf(LocalDateTime.of(2023, 1, 1, 1, 1, 1));
        requestLogDto = new RequestLogDto(null, "a", "b", "c", "d", "e", createdOn);
    }

    @Test
    @SneakyThrows
    public void getRequestLogListWritesQueryAndReturnsList() {
        List<RequestLogDto> list = List.of(requestLogDto);
        mockServer.expect(ExpectedCount.once(), requestTo("http://root.root/logs"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Accept", "application/json"))
                .andRespond(withSuccess()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(list)));

        Assertions.assertEquals(list, client.retrieveRequestLogList(null, null));
        mockServer.verify();
    }

    @Test
    @SneakyThrows
    public void postLogReturnsResponseEntityVoid() {
        mockServer.expect(ExpectedCount.once(), requestTo("http://root.root/logs"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Accept", "application/json"))
                .andExpect(content().json(
                        objectMapper.writeValueAsString(requestLogDto)))
                .andRespond(withSuccess()
                        .contentType(MediaType.APPLICATION_JSON));

        Assertions.assertTrue(client.saveRequestLog(requestLogDto).getStatusCode().is2xxSuccessful());
        mockServer.verify();
    }
}