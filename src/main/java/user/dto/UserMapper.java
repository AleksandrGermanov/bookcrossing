package user.dto;

import user.model.User;

public interface UserMapper {
    User userFromDto(UserDto userDto);

    UserDto dtoFromUser(User user);
}
