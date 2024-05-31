package ru.astondevs.trainee.bookcrossing.mainsvc.book.service;

import ru.astondevs.trainee.bookcrossing.mainsvc.book.dto.BookDto;
import ru.astondevs.trainee.bookcrossing.mainsvc.book.model.Book;

import java.util.List;
import java.util.Map;

public interface BookService {
    BookDto createBook(Long ownerId, BookDto bookDto);

    BookDto updateBook(Long ownerId, Long bookId, BookDto bookDto);

    BookDto retrieveBook(Long id);

    void deleteBook(Long userId, Long bookId);

    List<BookDto> findAll();

    List<BookDto> searchByParams(Map<String, Object> params, BookFetchOrder order);

    void giveBookAway(Long userFromId, Long userToId, Long bookId);

    Book getBookElseThrow(Long id);
}
