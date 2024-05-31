package ru.astondevs.trainee.bookcrossing.mainsvc.bookrequest.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.astondevs.trainee.bookcrossing.mainsvc.bookrequest.dto.BookRequestDto;

@Validated
@RequestMapping("/requests")
public interface BookRequestController {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    ResponseEntity<BookRequestDto> createBookRequest(HttpServletRequest request,
                                                     @RequestParam(name = "user") @Positive Long userId,
                                                     @RequestParam(name = "book") @Positive Long bookId);

    @GetMapping("/{requestId}")
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<BookRequestDto> retrieveBookRequest(HttpServletRequest request,
                                                       @PathVariable @Positive Long requestId);

    @DeleteMapping("/{requestId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    ResponseEntity<Void> deleteBookRequest(HttpServletRequest request,
                                           @RequestParam(name = "user") @Positive Long userId,
                                           @PathVariable @Positive Long requestId);
}
