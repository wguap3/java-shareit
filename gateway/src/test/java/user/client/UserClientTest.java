package user.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.user.UserClient;
import ru.practicum.user.dto.UserDto;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserClientTest {

    @Mock
    private RestTemplate restTemplate;

    private UserClient userClient;

    @BeforeEach
    void setUp() {
        userClient = new UserClient(restTemplate); // используем тестовый конструктор
    }

    @Test
    void addUser_shouldSendPost() {
        UserDto userDto = new UserDto();
        userDto.setName("Ivan");
        userDto.setEmail("ivan@example.com");

        ResponseEntity<Object> expected = ResponseEntity.ok(Map.of("id", 1L, "name", "Ivan"));

        when(restTemplate.exchange(eq(""), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expected);

        ResponseEntity<Object> response = userClient.addUser(userDto);

        assertThat(response).isEqualTo(expected);

        ArgumentCaptor<HttpEntity<UserDto>> captor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(eq(""), eq(HttpMethod.POST), captor.capture(), eq(Object.class));

        HttpEntity<UserDto> requestEntity = captor.getValue();
        assertThat(requestEntity.getBody().getName()).isEqualTo("Ivan");
    }

    @Test
    void getUserById_shouldSendGet() {
        ResponseEntity<Object> expected = ResponseEntity.ok(Map.of("id", 1L, "name", "Ivan"));

        when(restTemplate.exchange(eq("/1"), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expected);

        ResponseEntity<Object> response = userClient.getUserById(1L);

        assertThat(response).isEqualTo(expected);

        ArgumentCaptor<HttpEntity<Void>> captor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(eq("/1"), eq(HttpMethod.GET), captor.capture(), eq(Object.class));
    }

    @Test
    void getAllUsers_shouldSendGet() {
        ResponseEntity<Object> expected = ResponseEntity.ok("[]");

        when(restTemplate.exchange(eq(""), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expected);

        ResponseEntity<Object> response = userClient.getAllUsers();

        assertThat(response).isEqualTo(expected);

        verify(restTemplate).exchange(eq(""), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void update_shouldSendPatch() {
        UserDto userDto = new UserDto();
        userDto.setName("Ivan Updated");
        userDto.setEmail("ivan.updated@example.com");

        ResponseEntity<Object> expected = ResponseEntity.ok(Map.of("id", 1L, "name", "Ivan Updated"));

        when(restTemplate.exchange(eq("/1"), eq(HttpMethod.PATCH), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expected);

        ResponseEntity<Object> response = userClient.update(1L, userDto);

        assertThat(response).isEqualTo(expected);

        ArgumentCaptor<HttpEntity<UserDto>> captor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(eq("/1"), eq(HttpMethod.PATCH), captor.capture(), eq(Object.class));
        assertThat(captor.getValue().getBody().getName()).isEqualTo("Ivan Updated");
    }

    @Test
    void deleteUser_shouldSendDelete() {
        ResponseEntity<Object> expected = ResponseEntity.ok().build();

        when(restTemplate.exchange(eq("/1"), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expected);

        ResponseEntity<Object> response = userClient.deleteUser(1L);

        assertThat(response).isEqualTo(expected);

        verify(restTemplate).exchange(eq("/1"), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(Object.class));
    }
}

