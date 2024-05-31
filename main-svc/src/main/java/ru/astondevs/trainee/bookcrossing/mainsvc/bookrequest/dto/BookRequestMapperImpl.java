package ru.astondevs.trainee.bookcrossing.mainsvc.bookrequest.dto;

import org.springframework.stereotype.Component;
import ru.astondevs.trainee.bookcrossing.mainsvc.book.model.Book;
import ru.astondevs.trainee.bookcrossing.mainsvc.bookrequest.model.BookRequest;
import ru.astondevs.trainee.bookcrossing.mainsvc.user.model.User;

@Component
public class BookRequestMapperImpl implements BookRequestMapper {
    @Override
    public BookRequestDto dtoFromBookRequest(BookRequest bookRequest) {
        return new BookRequestDto(
                bookRequest.getId(),
                bookRequest.getRequester().getId(),
                bookRequest.getBook().getId(),
                bookRequest.getCreatedOn());
    }

    @Override
    public BookRequest bookRequestFromDto(BookRequestDto bookRequestDto, User user, Book book) {
        return new BookRequest(
                bookRequestDto.getId(),
                user,
                book,
                bookRequestDto.getCreatedOn());
    }
}
