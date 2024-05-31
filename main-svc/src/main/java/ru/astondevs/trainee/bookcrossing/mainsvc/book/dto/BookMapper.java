package ru.astondevs.trainee.bookcrossing.mainsvc.book.dto;


import ru.astondevs.trainee.bookcrossing.mainsvc.book.model.Book;

public interface BookMapper {
    BookDto dtoFromBook(Book book);

    Book bookFromDto(BookDto bookDto);
}
