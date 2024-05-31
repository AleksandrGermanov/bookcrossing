package ru.astondevs.trainee.bookcrossing.mainsvc.bookrequest.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.astondevs.trainee.bookcrossing.mainsvc.bookrequest.dto.BookRequestDto;
import ru.astondevs.trainee.bookcrossing.mainsvc.bookrequest.service.BookRequestService;
import ru.astondevs.trainee.bookcrossing.mainsvc.util.logging.StandardLoggingProcedure;

@Slf4j
@RestController
@RequiredArgsConstructor
public class BookRequestControllerImpl implements BookRequestController {
    private final BookRequestService bookRequestService;
    private final StandardLoggingProcedure loggingProcedure;

    @Override
    public ResponseEntity<BookRequestDto> createBookRequest(HttpServletRequest request,
                                                            Long userId,
                                                            Long bookId) {
        loggingProcedure.logRequest(log, request);
        return ResponseEntity.ofNullable(bookRequestService.createBookRequest(userId, bookId));
    }

    @Override
    public ResponseEntity<BookRequestDto> retrieveBookRequest(HttpServletRequest request,
                                                              Long requestId) {
        loggingProcedure.logRequest(log, request);
        return ResponseEntity.ofNullable(bookRequestService.retrieveBookRequest(requestId));
    }

    @Override
    public ResponseEntity<Void> deleteBookRequest(HttpServletRequest request,
                                                  Long userId,
                                                  Long requestId) {
        loggingProcedure.logRequest(log, request);
        bookRequestService.deleteBookRequest(userId, requestId);
        return ResponseEntity.noContent().build();
    }
}
