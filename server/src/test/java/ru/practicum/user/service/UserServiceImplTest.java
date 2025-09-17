package ru.practicum.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.exception.EmailAlreadyExistsException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void addUser_shouldSaveUser_whenEmailUnique() {
        UserDto dto = new UserDto(null, "Иван", "ivan@test.com");
        User user = new User(null, "Иван", "ivan@test.com");
        User saved = new User(1L, "Иван", "ivan@test.com");

        when(userMapper.toUser(dto)).thenReturn(user);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(user)).thenReturn(saved);
        when(userMapper.toUserDto(saved)).thenReturn(new UserDto(1L, "Иван", "ivan@test.com"));

        UserDto result = userService.addUser(dto);

        assertEquals(1L, result.getId());
        assertEquals("Иван", result.getName());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void addUser_shouldThrowException_whenEmailExists() {
        UserDto dto = new UserDto(null, "Иван", "ivan@test.com");
        User user = new User(null, "Иван", "ivan@test.com");

        when(userMapper.toUser(dto)).thenReturn(user);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        assertThrows(EmailAlreadyExistsException.class, () -> userService.addUser(dto));
    }

    @Test
    void update_shouldUpdateUser_whenValid() {
        User existing = new User(1L, "Иван", "ivan@test.com");
        UserDto dto = new UserDto(null, "Иван Иванов", "ivan@test.com");
        User updated = new User(1L, "Иван Иванов", "ivan@test.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        lenient().when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(existing)).thenReturn(updated);
        when(userMapper.toUserDto(updated)).thenReturn(new UserDto(1L, "Иван Иванов", "ivan@test.com"));

        UserDto result = userService.update(1L, dto);

        assertEquals("Иван Иванов", result.getName());

        verify(userMapper).updateUserFromDto(dto, existing); // проверяем, что вызван метод
        verify(userRepository).save(existing);
    }


    @Test
    void update_shouldThrowEmailAlreadyExistsException_whenEmailTaken() {
        User existing = new User(1L, "Иван", "ivan@test.com");
        UserDto dto = new UserDto(null, "Иван Иванов", "other@test.com");
        User other = new User(2L, "Пётр", "other@test.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(other));

        assertThrows(EmailAlreadyExistsException.class, () -> userService.update(1L, dto));
        verify(userRepository, never()).save(any());
    }

    @Test
    void update_shouldThrowNotFoundException_whenUserNotFound() {
        UserDto dto = new UserDto(null, "Иван Иванов", "ivan@test.com");

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.update(1L, dto));
        verify(userRepository, never()).save(any());
    }

    @Test
    void getUserById_shouldReturnUser_whenExists() {
        User user = new User(1L, "Иван", "ivan@test.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toUserDto(user)).thenReturn(new UserDto(1L, "Иван", "ivan@test.com"));

        UserDto result = userService.getUserById(1L);

        assertEquals("Иван", result.getName());
    }

    @Test
    void getUserById_shouldThrowNotFoundException_whenNotExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUserById(1L));
    }

    @Test
    void getAllUsers_shouldReturnList() {
        User user1 = new User(1L, "Иван", "ivan@test.com");
        User user2 = new User(2L, "Пётр", "petr@test.com");

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));
        when(userMapper.toUserDto(user1)).thenReturn(new UserDto(1L, "Иван", "ivan@test.com"));
        when(userMapper.toUserDto(user2)).thenReturn(new UserDto(2L, "Пётр", "petr@test.com"));

        List<UserDto> result = userService.getAllUsers();

        assertEquals(2, result.size());
        assertEquals("Иван", result.get(0).getName());
        assertEquals("Пётр", result.get(1).getName());
    }

    @Test
    void deleteUser_shouldCallRepository() {
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void findByIdOrThrow_shouldReturnUser_whenExists() {
        User user = new User(1L, "Иван", "ivan@test.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.findByIdOrThrow(1L);

        assertEquals("Иван", result.getName());
    }

    @Test
    void findByIdOrThrow_shouldThrowNotFoundException_whenNotExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.findByIdOrThrow(1L));
    }


}
