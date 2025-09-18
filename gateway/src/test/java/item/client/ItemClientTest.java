package item.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.item.ItemClient;
import ru.practicum.item.dto.CommentDto;
import ru.practicum.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ItemClientTest {

    @Mock
    private RestTemplate restTemplate;

    private ItemClient itemClient;

    private final Long userId = 1L;
    private final Long itemId = 5L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        itemClient = new ItemClient(restTemplate);
    }

    @Test
    void addItem_shouldSendPost() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Дрель");
        itemDto.setDescription("Электрическая дрель");
        itemDto.setAvailable(true);

        ResponseEntity<Object> expected = ResponseEntity.ok(Map.of("id", 1L, "name", "Дрель"));

        when(restTemplate.exchange(eq(""), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expected);

        ResponseEntity<Object> response = itemClient.addItem(userId, itemDto);

        assertThat(response).isEqualTo(expected);

        ArgumentCaptor<HttpEntity<ItemDto>> captor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(eq(""), eq(HttpMethod.POST), captor.capture(), eq(Object.class));

        HttpEntity<ItemDto> requestEntity = captor.getValue();
        assertThat(requestEntity.getBody().getName()).isEqualTo("Дрель");
        assertThat(requestEntity.getHeaders().getFirst("X-Sharer-User-Id")).isEqualTo("1");
    }

    @Test
    void editingItem_shouldSendPatch() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Дрель");
        itemDto.setDescription("Обновлённая дрель");
        itemDto.setAvailable(true);

        ResponseEntity<Object> expected = ResponseEntity.ok(Map.of("id", itemId, "name", "Дрель"));

        when(restTemplate.exchange(eq("/" + itemId), eq(HttpMethod.PATCH), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expected);

        ResponseEntity<Object> response = itemClient.editingItem(userId, itemId, itemDto);

        assertThat(response).isEqualTo(expected);

        ArgumentCaptor<HttpEntity<ItemDto>> captor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(eq("/" + itemId), eq(HttpMethod.PATCH), captor.capture(), eq(Object.class));

        assertThat(captor.getValue().getHeaders().getFirst("X-Sharer-User-Id")).isEqualTo("1");
    }

    @Test
    void getItemById_shouldSendGet() {
        ResponseEntity<Object> expected = ResponseEntity.ok(Map.of("id", itemId, "name", "Дрель"));

        when(restTemplate.exchange(eq("/" + itemId), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expected);

        ResponseEntity<Object> response = itemClient.getItemById(userId, itemId);

        assertThat(response).isEqualTo(expected);

        ArgumentCaptor<HttpEntity<Void>> captor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(eq("/" + itemId), eq(HttpMethod.GET), captor.capture(), eq(Object.class));
        assertThat(captor.getValue().getHeaders().getFirst("X-Sharer-User-Id")).isEqualTo("1");
    }

    @Test
    void searchItems_shouldSendGetWithQuery() {
        String text = "дрель";
        ResponseEntity<Object> expected = ResponseEntity.ok("[]");

        when(restTemplate.exchange(eq("/search?text=" + text), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expected);

        ResponseEntity<Object> response = itemClient.searchItems(text);

        assertThat(response).isEqualTo(expected);
        verify(restTemplate).exchange(eq("/search?text=" + text), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void addComment_shouldSendPost() {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Отличная дрель");
        commentDto.setAuthorName("Иван");
        commentDto.setCreated(LocalDateTime.now());

        ResponseEntity<Object> expected = ResponseEntity.ok(Map.of("id", 1L, "text", "Отличная дрель"));

        when(restTemplate.exchange(eq("/" + itemId + "/comment"), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expected);

        ResponseEntity<Object> response = itemClient.addComment(userId, itemId, commentDto);

        assertThat(response).isEqualTo(expected);

        ArgumentCaptor<HttpEntity<CommentDto>> captor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(eq("/" + itemId + "/comment"), eq(HttpMethod.POST), captor.capture(), eq(Object.class));
        assertThat(captor.getValue().getHeaders().getFirst("X-Sharer-User-Id")).isEqualTo("1");
    }

    @Test
    void getAllItemsByOwner_shouldSendGet() {
        ResponseEntity<Object> expected = ResponseEntity.ok("[]");

        when(restTemplate.exchange(eq(""), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expected);

        ResponseEntity<Object> response = itemClient.getAllItemsByOwner(userId);

        assertThat(response).isEqualTo(expected);

        ArgumentCaptor<HttpEntity<Void>> captor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(eq(""), eq(HttpMethod.GET), captor.capture(), eq(Object.class));
        assertThat(captor.getValue().getHeaders().getFirst("X-Sharer-User-Id")).isEqualTo("1");
    }
}


