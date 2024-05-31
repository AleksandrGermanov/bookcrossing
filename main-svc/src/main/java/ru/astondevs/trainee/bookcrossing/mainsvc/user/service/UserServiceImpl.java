package ru.astondevs.trainee.bookcrossing.mainsvc.user.service;

import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.astondevs.trainee.bookcrossing.mainsvc.exception.exists.UserExistsException;
import ru.astondevs.trainee.bookcrossing.mainsvc.exception.notfound.UserNotFoundException;
import ru.astondevs.trainee.bookcrossing.mainsvc.user.dao.UserDao;
import ru.astondevs.trainee.bookcrossing.mainsvc.user.dto.UserDto;
import ru.astondevs.trainee.bookcrossing.mainsvc.user.dto.UserMapper;
import ru.astondevs.trainee.bookcrossing.mainsvc.user.model.User;

import java.util.List;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDao userDao;
    private final UserMapper userMapper;
    private final Validator validator;

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        throwIfExists(userDto.getId());

        User userToCreate = userMapper.userFromDto(userDto);
        validator.validate(userToCreate);
        User created = userDao.save(userToCreate);

        return userMapper.dtoFromUser(created);
    }

    @Override
    @Transactional
    public UserDto updateUser(Long id, UserDto userDto) {
        User userToUpdate = getUserElseThrow(id);

        userDto.setId(id);
        mergeIntoUser(userDto, userToUpdate);
        validator.validate(userToUpdate);
        User updated = userDao.save(userToUpdate);

        return userMapper.dtoFromUser(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto retrieveUser(Long id) {
        return userMapper.dtoFromUser(getUserElseThrow(id));
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        userDao.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
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
