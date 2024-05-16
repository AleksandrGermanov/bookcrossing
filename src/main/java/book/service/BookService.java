package book.service;

import book.dto.BookDto;
import book.model.Book;

import java.util.LinkedHashMap;
import java.util.List;

public interface BookService {
    BookDto createBook(Long ownerId, BookDto bookDto);

    BookDto updateBook(Long ownerId, Long bookId, BookDto bookDto);

    BookDto retrieveBook(Long id);

    void deleteBook(Long userId, Long bookId);

    List<BookDto> findAll();

    List<BookDto> searchByParams(LinkedHashMap<String, String> params, BookFetchOrder order);

    void giveBookAway(Long userFromId, Long userToId, Long bookId);

    Book getBookElseThrow(Long id);
}
