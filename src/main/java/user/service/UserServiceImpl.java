package user.service;

import exception.exists.UserExistsException;
import exception.notfound.UserNotFoundException;
import user.dao.UserDao;
import user.dto.UserDto;
import user.dto.UserMapper;
import user.model.User;
import util.beanlib.DaoLib;
import util.beanlib.MapperLib;
import util.validation.ValidationService;

import java.util.List;

public class UserServiceImpl implements UserService {
    private final UserDao userDao = DaoLib.getDefaultUserDao();
    private final UserMapper userMapper = MapperLib.getDefaultUserMapper();
    private final ValidationService validationService = ValidationService.DEFAULT_INSTANCE;

    @Override
    public UserDto createUser(UserDto userDto) {
        throwIfExists(userDto.getId());

        User userToCreate = userMapper.userFromDto(userDto);
        validationService.validate(userToCreate);
        User created = userDao.create(userToCreate);

        return userMapper.dtoFromUser(created);
    }

    @Override
    public UserDto updateUser(Long id, UserDto userDto) {
        User userToUpdate = getUserElseThrow(id);

        userDto.setId(id);
        mergeIntoUser(userDto, userToUpdate);
        validationService.validate(userToUpdate);
        User updated = userDao.update(userToUpdate);

        return userMapper.dtoFromUser(updated);
    }

    @Override
    public UserDto retrieveUser(Long id) {
        return userMapper.dtoFromUser(getUserElseThrow(id));
    }

    @Override
    public void deleteUser(Long id) {
        userDao.delete(id);
    }

    @Override
    public List<UserDto> findAll() {
        return userDao.findAll().stream()
                .map(userMapper::dtoFromUser)
                .toList();
    }

    private void mergeIntoUser(UserDto updated, User userToUpdate) {
        if (updated.getName() != null) {
            userToUpdate.setName(updated.getName());
        }
        if (updated.getEmail() != null) {
            userToUpdate.setEmail(updated.getEmail());
        }
    }

    private User getUserElseThrow(Long id) {
        return userDao.obtain(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }


    private void throwIfExists(Long userId) {
        if (userId == null) {
            return;
        }

        if (userDao.exists(userId)) {
            throw new UserExistsException(userId);
        }
    }
}
