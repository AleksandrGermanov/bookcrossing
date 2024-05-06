package book.dto;

import book.model.Book;

public interface BookMapper {
    BookDto dtoFromBook(Book book);
    Book bookFromDto(BookDto bookDto);
}
