package user.service;

import exception.exists.UserExistsException;
import exception.notfound.UserNotFoundException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import user.dao.UserDao;
import user.dto.UserDto;
import user.dto.UserMapper;
import user.model.User;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDao userDao;
    private final UserMapper userMapper;
    private final Validator validator;

    @Override
    public UserDto createUser(UserDto userDto) {
        throwIfExists(userDto.getId());

        User userToCreate = userMapper.userFromDto(userDto);
        validator.validate(userToCreate);
        User created = userDao.save(userToCreate);

        return userMapper.dtoFromUser(created);
    }

    @Override
    public UserDto updateUser(Long id, UserDto userDto) {
        User userToUpdate = getUserElseThrow(id);

        userDto.setId(id);
        mergeIntoUser(userDto, userToUpdate);
        validator.validate(userToUpdate);
        User updated = userDao.save(userToUpdate);

        return userMapper.dtoFromUser(updated);
    }

    @Override
    public UserDto retrieveUser(Long id) {
        return userMapper.dtoFromUser(getUserElseThrow(id));
    }

    @Override
    public void deleteUser(Long id) {
        userDao.deleteById(id);
    }

    @Override
    public List<UserDto> findAll() {
        return userDao.findAll().stream()
                .map(userMapper::dtoFromUser)
                .toList();
    }

    @Override
    public User getUserElseThrow(Long id) {
        return userDao.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    private void mergeIntoUser(UserDto updated, User userToUpdate) {
        if (updated.getName() != null) {
            userToUpdate.setName(updated.getName());
        }
        if (updated.getEmail() != null) {
            userToUpdate.setEmail(updated.getEmail());
        }
    }

    private void throwIfExists(Long userId) {
        if (userId == null) {
            return;
        }

        if (userDao.existsById(userId)) {
            throw new UserExistsException(userId);
        }
    }
}
