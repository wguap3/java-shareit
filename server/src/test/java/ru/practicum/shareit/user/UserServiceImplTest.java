package ru.practicum.shareit.user;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.exception.EmailAlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setup() {
        user = new User(1L, "Alice", "alice@example.com");
        userDto = new UserDto(1L, "Alice", "alice@example.com");
    }

    @Test
    void addUser_shouldReturnUserDto_whenEmailIsUnique() {
        when(userMapper.toUser(userDto)).thenReturn(user);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toUserDto(user)).thenReturn(userDto);

        UserDto result = userService.addUser(userDto);

        assertThat(result, notNullValue());
        assertThat(result.getEmail(), equalTo("alice@example.com"));
        verify(userRepository).save(user);
    }

    @Test
    void addUser_shouldThrowException_whenEmailExists() {
        when(userMapper.toUser(userDto)).thenReturn(user);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        assertThrows(EmailAlreadyExistsException.class, () -> userService.addUser(userDto));
        verify(userRepository, never()).save(any());
    }


    @Test
    void update_shouldReturnUpdatedUser() {
        UserDto updatedDto = new UserDto(1L, "Alice Updated", "alice@example.com");
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        doNothing().when(userMapper).updateUserFromDto(updatedDto, user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toUserDto(user)).thenReturn(updatedDto);

        UserDto result = userService.update(user.getId(), updatedDto);

        assertThat(result.getName(), equalTo("Alice Updated"));
        verify(userRepository).save(user);
    }

    @Test
    void update_shouldThrow_whenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.update(1L, userDto));
    }

    @Test
    void getUserById_shouldReturnUserDto() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userMapper.toUserDto(user)).thenReturn(userDto);

        UserDto result = userService.getUserById(user.getId());

        assertThat(result.getId(), equalTo(userDto.getId()));
        assertThat(result.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void getAllUsers_shouldReturnListOfUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toUserDto(user)).thenReturn(userDto);

        List<UserDto> users = userService.getAllUsers();

        assertThat(users, hasSize(1));
        assertThat(users.get(0).getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void deleteUser_shouldCallRepository() {
        doNothing().when(userRepository).deleteById(user.getId());

        userService.deleteUser(user.getId());

        verify(userRepository).deleteById(user.getId());
    }
}

