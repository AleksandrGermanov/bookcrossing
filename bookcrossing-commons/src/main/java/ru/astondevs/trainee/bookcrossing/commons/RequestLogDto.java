package ru.astondevs.trainee.bookcrossing.commons;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestLogDto {
    private Long id;
    private String method;
    private String path;
    private String ip;
    private String parameterMap;
    private String serializedBody;
    private Timestamp createdOn;

    public RequestLogDto(HttpServletRequest request) {
        method = request.getMethod().toUpperCase();
        path = request.getRequestURI();
        ip = request.getRemoteAddr();
        createdOn = Timestamp.valueOf(LocalDateTime.now());
    }
}
