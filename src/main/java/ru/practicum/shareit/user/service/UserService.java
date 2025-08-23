package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto addUser(UserDto userDto);

    UserDto update(Long userId, UserDto userDto);

    UserDto getUserById(Long userId);

    List<UserDto> getAllUsers();

    void deleteUser(Long userId);
}
