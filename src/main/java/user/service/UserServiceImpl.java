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
    public UserDto updateUser(UserDto userDto) {
        throwIfNotFound(userDto.getId());

        User userToUpdate = userMapper.userFromDto(userDto);
        validationService.validate(userToUpdate);
        User updated = userDao.update(userToUpdate);

        return userMapper.dtoFromUser(updated);
    }

    @Override
    public UserDto retrieveUser(Long id){
        return userMapper.dtoFromUser(userDao.obtain(id)
                .orElseThrow(()-> new UserNotFoundException(id)));
    }

    @Override
    public void deleteUser(Long id){
        userDao.delete(id);
    }

    @Override
    public List<UserDto> findAll(){
        return userDao.findAll().stream()
                .map(userMapper::dtoFromUser)
                .toList();
    }

    private void throwIfNotFound(Long userId){
        if(!userDao.exists(userId)){
            throw new UserNotFoundException(userId);
        }
    }

    private void throwIfExists(Long userId){
        if(userId == null){
            return;
        }

        if(userDao.exists(userId)){
            throw new UserExistsException(userId);
        }
    }
}
