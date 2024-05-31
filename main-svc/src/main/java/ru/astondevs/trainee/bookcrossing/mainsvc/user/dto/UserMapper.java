package ru.astondevs.trainee.bookcrossing.mainsvc.user.dto;

import ru.astondevs.trainee.bookcrossing.mainsvc.user.model.User;

public interface UserMapper {
    User userFromDto(UserDto userDto);

    UserDto dtoFromUser(User user);
}
