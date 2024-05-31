package ru.astondevs.trainee.bookcrossing.mainsvc.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.astondevs.trainee.bookcrossing.mainsvc.user.dto.UserDto;

import java.util.List;

@RequestMapping("/users")
public interface UserController {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<List<UserDto>> findAll(HttpServletRequest request);

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    ResponseEntity<UserDto> createUser(HttpServletRequest request,
                                       @RequestBody UserDto userDto);

    @GetMapping({"/{userId}"})
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<UserDto> retrieveUser(HttpServletRequest request,
                                         @PathVariable @Positive Long userId);

    @PatchMapping({"/{userId}"})
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<UserDto> updateUser(HttpServletRequest request,
                                       @RequestBody UserDto userDto,
                                       @PathVariable @Positive Long userId);

    @DeleteMapping({"/{userId}"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    ResponseEntity<Void> deleteUser(HttpServletRequest request,
                                    @PathVariable @Positive Long userId);
}