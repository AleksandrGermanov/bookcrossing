package ru.astondevs.trainee.bookcrossing.loggingserver.requestlog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.astondevs.trainee.bookcrossing.commons.RequestLogDto;
import ru.astondevs.trainee.bookcrossing.loggingserver.requestlog.service.RequestLogService;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class RequestLogDtoControllerTest {
    @Mock
    private RequestLogService requestLogService;
    @InjectMocks
    private RequestLogController requestLogController;
    private ObjectMapper objectMapper = new ObjectMapper();
    private MockMvc mockMvc;
    private RequestLogDto log;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(requestLogController)
                .setControllerAdvice(ExceptionControllerAdvice.class)
                .build();
        Timestamp createdOn = Timestamp.valueOf(LocalDateTime.of(2023, 1, 1, 1, 1, 1));
        log = new RequestLogDto(null, "a", "b", "c", "d", "e", createdOn);
    }

    @Test
    @SneakyThrows
    public void testSaveRequestLog() {
        mockMvc.perform(post("/logs")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(log))
                )
                .andExpect(status().is2xxSuccessful());

        verify(requestLogService, times(1)).saveRequestLog(log);
    }

    @Test
    @SneakyThrows
    public void testRetrieveRequestLogList() {
        when(requestLogService.retrieveRequestLogList(0, 5)).thenReturn(List.of(log));

        mockMvc.perform(get("/logs")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(log))
                )
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(log))));
    }
}