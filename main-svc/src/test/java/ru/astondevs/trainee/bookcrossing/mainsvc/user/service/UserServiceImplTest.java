package ru.astondevs.trainee.bookcrossing.mainsvc.user.service;

import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.astondevs.trainee.bookcrossing.mainsvc.user.dao.UserDao;
import ru.astondevs.trainee.bookcrossing.mainsvc.user.dto.UserDto;
import ru.astondevs.trainee.bookcrossing.mainsvc.user.dto.UserMapper;
import ru.astondevs.trainee.bookcrossing.mainsvc.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    UserDto userDto;
    User user;
    @Mock
    private UserDao userDao;
    @Mock
    private UserMapper userMapper;
    @Mock
    private Validator validator;
    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userDto = new UserDto();
        userDto.setName("name");
        userDto.setEmail("email@mail.ru");
        user = new User(1L, userDto.getName(), userDto.getEmail());
    }

    @Test
    void createUser() {
        when(userMapper.userFromDto(userDto)).thenReturn(user);
        when(userDao.save(user)).thenReturn(user);
        when(userMapper.dtoFromUser(user)).thenReturn(userDto);

        assertEquals(userDto, userService.createUser(userDto));
        verify(validator, times(1)).validate(user);
    }

    @Test
    void updateUser() {
        userDto.setName("another");

        when(userDao.findById(1L)).thenReturn(Optional.of(user));
        when(userDao.save(user)).thenReturn(user);
        when(userMapper.dtoFromUser(user)).thenReturn(userDto);

        assertEquals(userDto, userService.updateUser(1L, userDto));
        assertEquals("another", user.getName());
        verify(validator, times(1)).validate(user);
    }

    @Test
    void retrieveUser() {
        when(userDao.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.dtoFromUser(user)).thenReturn(userDto);

        assertEquals(userDto, userService.retrieveUser(1L));
    }

    @Test
    void deleteUser() {
        userService.deleteUser(1L);

        verify(userDao, times(1)).deleteById(1L);
    }

    @Test
    void findAll() {
        when(userDao.findAll()).thenReturn(List.of(user));
        when(userMapper.dtoFromUser(user)).thenReturn(userDto);

        assertEquals(List.of(userDto), userService.findAll());
    }
}