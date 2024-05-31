package ru.astondevs.trainee.bookcrossing.mainsvc.book.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.astondevs.trainee.bookcrossing.mainsvc.book.dto.BookDto;
import ru.astondevs.trainee.bookcrossing.mainsvc.book.service.BookFetchOrder;

import java.util.List;

@Validated
@RequestMapping("/books")
public interface BookController {
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<List<BookDto>> findAll(HttpServletRequest request);

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<List<BookDto>> search(HttpServletRequest request,
                                         @RequestParam(required = false) String title,
                                         @RequestParam(required = false) String author,
                                         @RequestParam(required = false, name = "published-since")
                                         Integer publishedSince,
                                         @RequestParam(required = false, name = "is-available")
                                         Boolean isAvailable,
                                         @RequestParam(required = false) BookFetchOrder order);

    @GetMapping("/{bookId}")
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<BookDto> retrieveBook(HttpServletRequest request,
                                         @PathVariable @Positive Long bookId);

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    ResponseEntity<BookDto> createBook(HttpServletRequest request,
                                       @RequestBody BookDto bookDto,
                                       @RequestParam(name = "user") @Positive Long userId);

    @PatchMapping("/{bookId}")
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<BookDto> updateBook(HttpServletRequest request,
                                       @RequestBody BookDto bookDto,
                                       @PathVariable Long bookId,
                                       @RequestParam(name = "user") @Positive Long userId);

    @PatchMapping("/{bookId}/give-away")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    ResponseEntity<Void> giveBookAway(HttpServletRequest request,
                                      @PathVariable Long bookId,
                                      @RequestParam(name = "user") @Positive Long userId,
                                      @RequestParam(name = "user-to") Long userToId);

    @DeleteMapping("/{bookId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    ResponseEntity<Void> deleteBook(HttpServletRequest request,
                                    @PathVariable @Positive Long bookId,
                                    @RequestParam(name = "user") @Positive Long userId);
}
