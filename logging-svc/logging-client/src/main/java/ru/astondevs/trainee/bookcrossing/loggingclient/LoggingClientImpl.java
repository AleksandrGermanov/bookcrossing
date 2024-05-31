package ru.astondevs.trainee.bookcrossing.loggingclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import ru.astondevs.trainee.bookcrossing.commons.RequestLogDto;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Getter
public class LoggingClientImpl implements LoggingClient {
    private final String loggingServerUrl;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public ResponseEntity<Void> saveRequestLog(RequestLogDto requestLogDto) {
        final String path = "/logs";

        try {
            RequestEntity<String> entity = RequestEntity
                    .post(loggingServerUrl + path)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .acceptCharset(StandardCharsets.UTF_8)
                    .body(objectMapper.writeValueAsString(requestLogDto));
            log.debug("Saving request log.");
            ResponseEntity<Void> response = restTemplate.exchange(entity, Void.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new LoggingClientException(String.format("Response code was not 2xx : %s.",
                        response.getStatusCode()));
            }
            return response;
        } catch (HttpClientErrorException | JsonProcessingException e) {
            throw new LoggingClientException("Unable to sava data to remote server : "
                    + e.getMessage(), e);
        }
    }

    @Override
    public List<RequestLogDto> retrieveRequestLogList(Integer from, Integer size) {
        final String path = "/logs";
        String query = "";
        Map<String, String> params = Collections.emptyMap();

        if (from != null && size != null) {
            query = "?from={from}&size={size}";
            params = Map.of(
                    "from", from.toString(),
                    "size", size.toString()
            );
        }

        RequestEntity<Void> entity = RequestEntity
                .method(HttpMethod.GET, loggingServerUrl + path + query, params)
                .accept(MediaType.APPLICATION_JSON)
                .acceptCharset(StandardCharsets.UTF_8)
                .build();

        log.debug("Requesting log list from logging server.");
        ParameterizedTypeReference<List<RequestLogDto>> reference = new ParameterizedTypeReference<>() {
        };
        try {
            return restTemplate.exchange(entity, reference).getBody();
        } catch (HttpClientErrorException e) {
            throw new LoggingClientException("Obtaining data from remote server failed: "
                    + e.getMessage(), e);
        }
    }
}
