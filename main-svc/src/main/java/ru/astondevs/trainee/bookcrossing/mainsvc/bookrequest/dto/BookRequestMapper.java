package ru.astondevs.trainee.bookcrossing.mainsvc.bookrequest.dto;

import ru.astondevs.trainee.bookcrossing.mainsvc.book.model.Book;
import ru.astondevs.trainee.bookcrossing.mainsvc.bookrequest.model.BookRequest;
import ru.astondevs.trainee.bookcrossing.mainsvc.user.model.User;

public interface BookRequestMapper {
    BookRequestDto dtoFromBookRequest(BookRequest bookRequest);

    BookRequest bookRequestFromDto(BookRequestDto bookRequestDto, User user, Book book);
}
