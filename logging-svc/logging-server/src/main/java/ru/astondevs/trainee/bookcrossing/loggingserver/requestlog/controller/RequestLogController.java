package ru.astondevs.trainee.bookcrossing.loggingserver.requestlog.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.astondevs.trainee.bookcrossing.commons.RequestLogDto;
import ru.astondevs.trainee.bookcrossing.loggingserver.requestlog.service.RequestLogService;

import java.util.List;

@Slf4j

@RestController
@RequiredArgsConstructor
@RequestMapping("/logs")
public class RequestLogController {
    private final RequestLogService requestLogService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<RequestLogDto> retrieveRequestLogList(@RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                      @RequestParam(defaultValue = "5") @Positive Integer size) {
        log.info("Processing request GET /logs; from = {}, size = {}", from, size);
        return requestLogService.retrieveRequestLogList(from, size);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> saveRequestLog(@RequestBody RequestLogDto requestLogDto) {
        log.info("Processing request POST /logs. RequestLog = {}", requestLogDto);
        requestLogService.saveRequestLog(requestLogDto);
        return ResponseEntity.noContent().build();
    }
}
