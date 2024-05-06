package book.dto;

import book.model.Book;
import user.model.User;

import java.util.Collections;

public class BookMapperImpl implements BookMapper {
    @Override
    public BookDto dtoFromBook(Book book) {
        return new BookDto(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getPublicationYear(),
                book.getIsAvailable(),
                book.getOwnedBy().stream()
                        .map(User::getId)
                        .toList()
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
                Collections.emptyList()
        );
    }
}
