package ru.practicum.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.service.UserService;

import java.util.List;


@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto addUser(@RequestBody UserDto userDto) {
        return userService.addUser(userDto);
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable Long userId) {
        return userService.getUserById(userId);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable Long userId, @RequestBody UserDto userDto) {
        return userService.update(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }

}
