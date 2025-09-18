package request.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.request.RequestClient;
import ru.practicum.request.dto.ItemRequestDto;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RequestClientTest {

    @Mock
    private RestTemplate restTemplate;

    private RequestClient requestClient;

    private final Long userId = 1L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        requestClient = new RequestClient(restTemplate);
    }

    @Test
    void createRequest_shouldSendPost() {
        ItemRequestDto dto = new ItemRequestDto("Нужна дрель");
        ResponseEntity<Object> expected = ResponseEntity.ok(Map.of("id", 1L, "description", "Нужна дрель"));

        when(restTemplate.exchange(eq(""), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expected);

        ResponseEntity<Object> response = requestClient.createRequest(userId, dto);

        assertThat(response).isEqualTo(expected);

        ArgumentCaptor<HttpEntity<ItemRequestDto>> captor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(eq(""), eq(HttpMethod.POST), captor.capture(), eq(Object.class));

        HttpEntity<ItemRequestDto> requestEntity = captor.getValue();
        assertThat(requestEntity.getBody().getDescription()).isEqualTo("Нужна дрель");
        assertThat(requestEntity.getHeaders().getFirst("X-Sharer-User-Id")).isEqualTo("1");
    }

    @Test
    void getUserRequests_shouldSendGet() {
        ResponseEntity<Object> expected = ResponseEntity.ok("[{\"id\":1,\"description\":\"Нужна дрель\"}]");

        when(restTemplate.exchange(eq(""), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expected);

        ResponseEntity<Object> response = requestClient.getUserRequests(userId);

        assertThat(response).isEqualTo(expected);

        ArgumentCaptor<HttpEntity<Void>> captor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(eq(""), eq(HttpMethod.GET), captor.capture(), eq(Object.class));

        HttpEntity<Void> requestEntity = captor.getValue();
        assertThat(requestEntity.getHeaders().getFirst("X-Sharer-User-Id")).isEqualTo("1");
    }

    @Test
    void getAllRequests_shouldSendGetToAll() {
        ResponseEntity<Object> expected = ResponseEntity.ok(
                "[{\"id\":1,\"description\":\"дрель\"},{\"id\":2,\"description\":\"молоток\"}]"
        );

        when(restTemplate.exchange(eq("/all"), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expected);

        ResponseEntity<Object> response = requestClient.getAllRequests(userId);

        assertThat(response).isEqualTo(expected);

        ArgumentCaptor<HttpEntity<Void>> captor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(eq("/all"), eq(HttpMethod.GET), captor.capture(), eq(Object.class));

        HttpEntity<Void> requestEntity = captor.getValue();
        assertThat(requestEntity.getHeaders().getFirst("X-Sharer-User-Id")).isEqualTo("1");
    }

    @Test
    void getRequestById_shouldSendGetWithId() {
        ResponseEntity<Object> expected = ResponseEntity.ok(Map.of("id", 5L, "description", "Отвёртка"));

        when(restTemplate.exchange(eq("/5"), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expected);

        ResponseEntity<Object> response = requestClient.getRequestById(userId, 5L);

        assertThat(response).isEqualTo(expected);

        ArgumentCaptor<HttpEntity<Void>> captor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(eq("/5"), eq(HttpMethod.GET), captor.capture(), eq(Object.class));

        HttpEntity<Void> requestEntity = captor.getValue();
        assertThat(requestEntity.getHeaders().getFirst("X-Sharer-User-Id")).isEqualTo("1");
    }
}