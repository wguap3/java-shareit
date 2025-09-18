package ru.practicum.user.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.exception.EmailAlreadyExistsException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Transactional
public class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
    }

    @Test
    void addUser_shouldSaveAndReturnUser() {
        UserDto dto = new UserDto(null, "Иван", "ivan@test.com");

        UserDto saved = userService.addUser(dto);

        assertNotNull(saved.getId());
        assertEquals("Иван", saved.getName());

        User userFromDb = userRepository.findById(saved.getId()).orElseThrow();
        assertEquals("ivan@test.com", userFromDb.getEmail());
    }

    @Test
    void addUser_shouldThrowException_whenEmailAlreadyExists() {
        UserDto dto = new UserDto(null, "Иван", "ivan@test.com");
        userService.addUser(dto);

        UserDto duplicate = new UserDto(null, "Иван2", "ivan@test.com");

        Exception exception = assertThrows(EmailAlreadyExistsException.class, () -> {
            userService.addUser(duplicate);
        });

        assertEquals("Email уже используется: ivan@test.com", exception.getMessage());
    }

    @Test
    void getUserById_shouldReturnUser() {
        UserDto dto = new UserDto(null, "Мария", "maria@test.com");
        UserDto saved = userService.addUser(dto);

        UserDto found = userService.getUserById(saved.getId());

        assertEquals(saved.getId(), found.getId());
        assertEquals("Мария", found.getName());
    }

    @Test
    void updateUser_shouldUpdateUser() {
        UserDto dto = new UserDto(null, "Пётр", "petr@test.com");
        UserDto saved = userService.addUser(dto);

        UserDto updateDto = new UserDto(null, "Пётр Иванов", "petr@test.com");
        UserDto updated = userService.update(saved.getId(), updateDto);

        assertEquals(saved.getId(), updated.getId());
        assertEquals("Пётр Иванов", updated.getName());

        User userFromDb = userRepository.findById(saved.getId()).orElseThrow();
        assertEquals("Пётр Иванов", userFromDb.getName());
    }

    @Test
    void updateUser_shouldThrowException_whenEmailAlreadyExists() {
        UserDto dto1 = new UserDto(null, "Иван", "ivan@test.com");
        UserDto saved1 = userService.addUser(dto1);

        UserDto dto2 = new UserDto(null, "Мария", "maria@test.com");
        UserDto saved2 = userService.addUser(dto2);

        UserDto updateDto = new UserDto(null, "Мария Петрова", "ivan@test.com"); // email уже используется

        Exception exception = assertThrows(EmailAlreadyExistsException.class, () -> {
            userService.update(saved2.getId(), updateDto);
        });

        assertEquals("Email уже используется: ivan@test.com", exception.getMessage());
    }


    @Test
    void deleteUser_shouldRemoveUser() {
        UserDto dto = new UserDto(null, "Алексей", "aleksey@test.com");
        UserDto saved = userService.addUser(dto);

        userService.deleteUser(saved.getId());

        assertFalse(userRepository.findById(saved.getId()).isPresent());
    }

    @Test
    void getAllUsers_shouldReturnAllUsers() {
        UserDto dto1 = new UserDto(null, "Юлия", "yulia@test.com");
        UserDto dto2 = new UserDto(null, "Сергей", "sergey@test.com");

        userService.addUser(dto1);
        userService.addUser(dto2);

        List<UserDto> allUsers = userService.getAllUsers();

        assertEquals(2, allUsers.size());
        List<String> names = allUsers.stream().map(UserDto::getName).toList();
        assertTrue(names.contains("Юлия"));
        assertTrue(names.contains("Сергей"));
    }

    @Test
    void getUserById_shouldThrowException_whenUserNotFound() {
        long invalidId = 999L;

        Exception exception = assertThrows(NotFoundException.class, () -> {
            userService.getUserById(invalidId);
        });

        assertEquals("User with id 999 not found", exception.getMessage());
    }

}
