package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailAlreadyExistsException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserDto addUser(UserDto userDto) {
        User user = userMapper.toUser(userDto);

        if (userRepository.getEmails().contains(user.getEmail())) {
            throw new EmailAlreadyExistsException("Email уже используется: " + user.getEmail());
        }

        return userMapper.toUserDto(userRepository.save(user));
    }


    @Override
    public UserDto update(Long userId, UserDto userDto) {
        User existingUser = userRepository.findById(userId);

        if (!existingUser.getEmail().equals(userDto.getEmail())
                && userRepository.getEmails().contains(userDto.getEmail())) {
            throw new EmailAlreadyExistsException("Email уже используется: " + userDto.getEmail());
        }

        userMapper.updateUserFromDto(userDto, existingUser);

        return userMapper.toUserDto(userRepository.save(existingUser));
    }

    @Override
    public UserDto getUserById(Long userId) {
        User user = userRepository.findById(userId);
        return userMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
}
