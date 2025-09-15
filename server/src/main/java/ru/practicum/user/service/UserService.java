package ru.practicum.user.service;

import ru.practicum.user.dto.UserDto;
import ru.practicum.user.model.User;

import java.util.List;

public interface UserService {
    UserDto addUser(UserDto userDto);

    UserDto update(Long userId, UserDto userDto);

    UserDto getUserById(Long userId);

    List<UserDto> getAllUsers();

    void deleteUser(Long userId);

    User findByIdOrThrow(Long userId);
}
