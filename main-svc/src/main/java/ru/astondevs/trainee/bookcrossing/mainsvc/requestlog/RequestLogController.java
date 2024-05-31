package ru.astondevs.trainee.bookcrossing.mainsvc.requestlog;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.astondevs.trainee.bookcrossing.commons.RequestLogDto;

import java.util.List;


@RequestMapping("/logs")
public interface RequestLogController {
    @GetMapping()
    public List<RequestLogDto> findLogs(@RequestParam(required = false) @PositiveOrZero Integer from,
                                        @RequestParam(required = false) @Positive Integer size);
}