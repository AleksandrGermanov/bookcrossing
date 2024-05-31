package ru.astondevs.trainee.bookcrossing.mainsvc.book.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.astondevs.trainee.bookcrossing.mainsvc.book.dto.BookDto;
import ru.astondevs.trainee.bookcrossing.mainsvc.book.service.BookFetchOrder;
import ru.astondevs.trainee.bookcrossing.mainsvc.book.service.BookService;
import ru.astondevs.trainee.bookcrossing.mainsvc.util.logging.StandardLoggingProcedure;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class BookControllerImpl implements BookController {
    private final BookService bookService;
    private final StandardLoggingProcedure loggingProcedure;

    @Override
    public ResponseEntity<List<BookDto>> findAll(HttpServletRequest request) {
        loggingProcedure.logRequest(log, request);
        return ResponseEntity.ofNullable(bookService.findAll());
    }

    @Override
    public ResponseEntity<List<BookDto>> search(HttpServletRequest request,
                                                String title,
                                                String author,
                                                Integer publishedSince,
                                                Boolean isAvailable,
                                                BookFetchOrder order) {
        loggingProcedure.logRequest(log, request);
        Map<String, Object> params = new HashMap<>();
        params.put("title", title);
        params.put("author", author);
        params.put("publishedSince", publishedSince);
        params.put("isAvailable", isAvailable);

        return ResponseEntity.ofNullable(bookService.searchByParams(params, order));
    }

    @Override
    public ResponseEntity<BookDto> retrieveBook(HttpServletRequest request,
                                                Long bookId) {
        loggingProcedure.logRequest(log, request);
        return ResponseEntity.ofNullable(bookService.retrieveBook(bookId));
    }

    @Override
    public ResponseEntity<BookDto> createBook(HttpServletRequest request,
                                              BookDto bookDto,
                                              Long userId) {
        loggingProcedure.logRequest(log, request, bookDto);
        return ResponseEntity.ofNullable(bookService.createBook(userId, bookDto));
    }

    @Override
    public ResponseEntity<BookDto> updateBook(HttpServletRequest request,
                                              BookDto bookDto,
                                              Long bookId,
                                              Long userId) {
        loggingProcedure.logRequest(log, request, bookDto);
        return ResponseEntity.ofNullable(bookService.updateBook(userId, bookId, bookDto));
    }

    @Override
    public ResponseEntity<Void> giveBookAway(HttpServletRequest request,
                                             Long bookId,
                                             Long userId,
                                             Long userToId) {
        loggingProcedure.logRequest(log, request);
        bookService.giveBookAway(userId, userToId, bookId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> deleteBook(HttpServletRequest request,
                                           Long bookId,
                                           Long userId) {
        loggingProcedure.logRequest(log, request);
        bookService.deleteBook(userId, bookId);
        return ResponseEntity.noContent().build();
    }
}
