package ru.astondevs.trainee.bookcrossing.mainsvc.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.astondevs.trainee.bookcrossing.mainsvc.user.dto.UserDto;
import ru.astondevs.trainee.bookcrossing.mainsvc.user.service.UserService;
import ru.astondevs.trainee.bookcrossing.mainsvc.util.logging.StandardLoggingProcedure;

import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
public class UserControllerImpl implements UserController {
    private final UserService userService;
    private final StandardLoggingProcedure loggingProcedure;

    @Override
    public ResponseEntity<List<UserDto>> findAll(HttpServletRequest request) {
        loggingProcedure.logRequest(log, request);
        return ResponseEntity.ofNullable(userService.findAll());
    }


    @Override
    public ResponseEntity<UserDto> createUser(HttpServletRequest request,
                                              UserDto userDto) {
        loggingProcedure.logRequest(log, request, userDto);
        return ResponseEntity.ofNullable(userService.createUser(userDto));
    }

    @Override
    public ResponseEntity<UserDto> retrieveUser(HttpServletRequest request,
                                                Long userId) {
        loggingProcedure.logRequest(log, request);
        return ResponseEntity.ofNullable(userService.retrieveUser(userId));
    }

    @Override
    public ResponseEntity<UserDto> updateUser(HttpServletRequest request,
                                              UserDto userDto,
                                              Long userId) {
        loggingProcedure.logRequest(log, request, userDto);
        return ResponseEntity.ofNullable(userService.updateUser(userId, userDto));
    }

    @Override
    public ResponseEntity<Void> deleteUser(HttpServletRequest request,
                                           Long userId) {
        loggingProcedure.logRequest(log, request);
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}