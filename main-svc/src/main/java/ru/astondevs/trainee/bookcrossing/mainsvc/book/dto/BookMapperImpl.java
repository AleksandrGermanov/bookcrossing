package ru.astondevs.trainee.bookcrossing.mainsvc.book.dto;

import org.springframework.stereotype.Component;
import ru.astondevs.trainee.bookcrossing.mainsvc.book.model.Book;
import ru.astondevs.trainee.bookcrossing.mainsvc.bookrequest.model.BookRequest;
import ru.astondevs.trainee.bookcrossing.mainsvc.user.model.User;

import java.util.Collections;

@Component
public class BookMapperImpl implements BookMapper {
    @Override
    public BookDto dtoFromBook(Book book) {
        return new BookDto(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getPublicationYear(),
                book.getIsAvailable(),
                book.getOwnedBy() != null
                        ? book.getOwnedBy().stream()
                        .map(User::getId)
                        .toList()
                        : null,
                book.getRequestsForBook() != null
                        ? book.getRequestsForBook().stream()
                        .map(BookRequest::getId)
                        .toList()
                        : null
        );
    }

    @Override
    public Book bookFromDto(BookDto bookDto) {
        return new Book(
                bookDto.getId(),
                bookDto.getTitle(),
                bookDto.getAuthor(),
                bookDto.getPublicationYear(),
                bookDto.getIsAvailable(),
                Collections.emptyList(),
                Collections.emptyList()
        );
    }
}
