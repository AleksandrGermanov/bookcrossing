package user.dto;

import book.model.Book;
import user.model.User;

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
                user.getBooksInPossession().stream()
                        .map(Book::getId)
                        .toList(),
                user.getRequestsFrom(),
                user.getRequestsTo()
        );
    }
}
