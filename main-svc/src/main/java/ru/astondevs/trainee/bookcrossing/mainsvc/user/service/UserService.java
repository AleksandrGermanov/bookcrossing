package ru.astondevs.trainee.bookcrossing.mainsvc.user.service;

import ru.astondevs.trainee.bookcrossing.mainsvc.user.dto.UserDto;
import ru.astondevs.trainee.bookcrossing.mainsvc.user.model.User;

import java.util.List;

public interface UserService {
    UserDto createUser(UserDto userDto);

    UserDto updateUser(Long id, UserDto userDto);

    UserDto retrieveUser(Long id);

    void deleteUser(Long id);

    List<UserDto> findAll();

    User getUserElseThrow(Long id);
}
