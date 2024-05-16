package user.service;

import user.dto.UserDto;
import user.model.User;

import java.util.List;

public interface UserService {
    UserDto createUser(UserDto userDto);

    UserDto updateUser(Long id, UserDto userDto);

    UserDto retrieveUser(Long id);

    void deleteUser(Long id);

    List<UserDto> findAll();

    User getUserElseThrow(Long id);
}
