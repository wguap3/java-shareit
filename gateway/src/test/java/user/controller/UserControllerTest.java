package user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.GatewayApp;
import ru.practicum.user.UserClient;
import ru.practicum.user.UserController;
import ru.practicum.user.dto.UserDto;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@ContextConfiguration(classes = GatewayApp.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserClient userClient;

    @Test
    void addUser_shouldReturnCreatedUser() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("Ivan");
        userDto.setEmail("ivan@example.com");

        Map<String, Object> responseBody = Map.of(
                "id", 1L,
                "name", "Ivan",
                "email", "ivan@example.com"
        );
        ResponseEntity<Object> response = ResponseEntity.ok(responseBody);

        when(userClient.addUser(any(UserDto.class))).thenReturn(response);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Ivan"))
                .andExpect(jsonPath("$.email").value("ivan@example.com"));
    }

    @Test
    void getUserById_shouldReturnUser() throws Exception {
        Map<String, Object> responseBody = Map.of(
                "id", 1L,
                "name", "Ivan",
                "email", "ivan@example.com"
        );
        ResponseEntity<Object> response = ResponseEntity.ok(responseBody);

        when(userClient.getUserById(1L)).thenReturn(response);

        mockMvc.perform(get("/users/{userId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Ivan"))
                .andExpect(jsonPath("$.email").value("ivan@example.com"));
    }

    @Test
    void getAllUsers_shouldReturnUsers() throws Exception {
        List<Map<String, Object>> responseBody = List.of(
                Map.of("id", 1L, "name", "Ivan", "email", "ivan@example.com"),
                Map.of("id", 2L, "name", "Petr", "email", "petr@example.com")
        );
        ResponseEntity<Object> response = ResponseEntity.ok(responseBody);

        when(userClient.getAllUsers()).thenReturn(response);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Ivan"))
                .andExpect(jsonPath("$[1].name").value("Petr"));
    }

    @Test
    void update_shouldReturnUpdatedUser() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("Ivan Updated");
        userDto.setEmail("ivan.updated@example.com");

        Map<String, Object> responseBody = Map.of(
                "id", 1L,
                "name", "Ivan Updated",
                "email", "ivan.updated@example.com"
        );
        ResponseEntity<Object> response = ResponseEntity.ok(responseBody);

        when(userClient.update(eq(1L), any(UserDto.class))).thenReturn(response);

        mockMvc.perform(patch("/users/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Ivan Updated"))
                .andExpect(jsonPath("$.email").value("ivan.updated@example.com"));
    }

}
