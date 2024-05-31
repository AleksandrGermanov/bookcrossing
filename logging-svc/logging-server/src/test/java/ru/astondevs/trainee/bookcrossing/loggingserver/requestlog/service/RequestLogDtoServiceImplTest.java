package ru.astondevs.trainee.bookcrossing.loggingserver.requestlog.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.astondevs.trainee.bookcrossing.commons.RequestLogDto;
import ru.astondevs.trainee.bookcrossing.loggingserver.requestlog.dao.RequestLogDao;
import ru.astondevs.trainee.bookcrossing.loggingserver.requestlog.mapping.RequestLogMapper;
import ru.astondevs.trainee.bookcrossing.loggingserver.requestlog.model.RequestLog;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestLogDtoServiceImplTest {
    RequestLog log;
    RequestLogDto logDto;
    @Mock
    private RequestLogDao requestLogDao;
    @Mock
    private RequestLogMapper requestLogMapper;
    @InjectMocks
    private RequestLogServiceImpl requestLogService;

    @BeforeEach
    public void startup() {
        Timestamp createdOn = Timestamp.valueOf(
                LocalDateTime.of(2023, 1, 1, 1, 1, 1));
        log = new RequestLog(null, "a", "b", "c", "d", "e", createdOn);
        logDto = new RequestLogDto(null, "a", "b", "c", "d", "e", createdOn);
    }


    @Test
    void saveRequestLog() {
        when(requestLogMapper.toModel(logDto)).thenReturn(log);

        requestLogService.saveRequestLog(logDto);

        verify(requestLogDao, times(1)).save(log);
    }

    @Test
    void retrieveRequestLogList() {
        when(requestLogDao.findAll(any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(log)));
        when(requestLogMapper.toDto(log)).thenReturn(logDto);

        assertEquals(List.of(logDto), requestLogService.retrieveRequestLogList(0, 5));
    }
}