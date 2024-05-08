package user.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import user.dao.UserDao;
import user.dto.UserDto;
import user.dto.UserMapper;
import user.model.User;
import util.validation.ValidationService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserDao userDao;
    @Mock
    private UserMapper userMapper;
    @Mock
    private ValidationService validationService;
    @InjectMocks
    private UserServiceImpl userService;
    UserDto userDto;
    User user;

    @BeforeEach
    void setUp(){
        userDto = new UserDto();
        userDto.setName("name");
        userDto.setEmail("email@mail.ru");
        user = new User(1L, userDto.getName(), userDto.getEmail());
    }

    @Test
    void createUser() {
        when(userMapper.userFromDto(userDto)).thenReturn(user);
        when(userDao.create(user)).thenReturn(user);
        when(userMapper.dtoFromUser(user)).thenReturn(userDto);

        assertEquals(userDto, userService.createUser(userDto));
        verify(validationService, times(1)).validate(user);
    }

    @Test
    void updateUser() {
        userDto.setName("another");

        when(userDao.obtain(1L)).thenReturn(Optional.of(user));
        when(userDao.update(user)).thenReturn(user);
        when(userMapper.dtoFromUser(user)).thenReturn(userDto);

        assertEquals(userDto, userService.updateUser(1L, userDto));
        assertEquals("another", user.getName());
        verify(validationService, times(1)).validate(user);
    }

    @Test
    void retrieveUser() {
        when(userDao.obtain(1L)).thenReturn(Optional.of(user));
        when(userMapper.dtoFromUser(user)).thenReturn(userDto);

        assertEquals(userDto, userService.retrieveUser(1L));
    }

    @Test
    void deleteUser() {
        userService.deleteUser(1L);

        verify(userDao, times(1)).delete(1L);
    }

    @Test
    void findAll() {
        when(userDao.findAll()).thenReturn(List.of(user));
        when(userMapper.dtoFromUser(user)).thenReturn(userDto);

        assertEquals(List.of(userDto), userService.findAll());
    }
}