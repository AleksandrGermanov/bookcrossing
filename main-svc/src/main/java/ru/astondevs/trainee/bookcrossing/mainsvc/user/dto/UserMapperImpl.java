package ru.astondevs.trainee.bookcrossing.mainsvc.user.dto;

import org.springframework.stereotype.Component;
import ru.astondevs.trainee.bookcrossing.mainsvc.book.model.Book;
import ru.astondevs.trainee.bookcrossing.mainsvc.bookrequest.model.BookRequest;
import ru.astondevs.trainee.bookcrossing.mainsvc.user.model.User;

@Component
public class UserMapperImpl implements UserMapper {
    @Override
    public User userFromDto(UserDto userDto) {
        return new User(userDto.getId(), userDto.getName(), userDto.getEmail());
    }

    @Override
    public UserDto dtoFromUser(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getBooksInPossession() != null
                        ? user.getBooksInPossession().stream()
                        .map(Book::getId)
                        .toList()
                        : null,
                user.getRequestsFrom() != null
                        ? user.getRequestsFrom().stream()
                        .map(BookRequest::getId)
                        .toList()
                        : null
        );
    }
}
