package item.controller;


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
import ru.practicum.item.ItemClient;
import ru.practicum.item.ItemController;
import ru.practicum.item.dto.CommentDto;
import ru.practicum.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@ContextConfiguration(classes = GatewayApp.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemClient itemClient;

    private final Long userId = 1L;
    private final Long itemId = 5L;

    @Test
    void addItem_shouldReturnCreatedItem() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Дрель");
        itemDto.setDescription("Электрическая дрель");
        itemDto.setAvailable(true);

        Map<String, Object> responseBody = Map.of("id", itemId, "name", "Дрель");
        ResponseEntity<Object> response = ResponseEntity.ok(responseBody);

        when(itemClient.addItem(eq(userId), any(ItemDto.class))).thenReturn(response);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value("Дрель"));
    }

    @Test
    void editingItem_shouldReturnUpdatedItem() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Дрель Updated");
        itemDto.setDescription("Обновленная дрель");

        Map<String, Object> responseBody = Map.of("id", itemId, "name", "Дрель Updated");
        ResponseEntity<Object> response = ResponseEntity.ok(responseBody);

        when(itemClient.editingItem(eq(userId), eq(itemId), any(ItemDto.class))).thenReturn(response);

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value("Дрель Updated"));
    }

    @Test
    void getItemById_shouldReturnItem() throws Exception {
        Map<String, Object> responseBody = Map.of("id", itemId, "name", "Дрель");
        ResponseEntity<Object> response = ResponseEntity.ok(responseBody);

        when(itemClient.getItemById(eq(userId), eq(itemId))).thenReturn(response);

        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value("Дрель"));
    }

    @Test
    void searchItems_shouldReturnMatchingItems() throws Exception {
        List<Map<String, Object>> responseBody = List.of(
                Map.of("id", itemId, "name", "Дрель")
        );
        ResponseEntity<Object> response = ResponseEntity.ok(responseBody);

        when(itemClient.searchItems("дрель")).thenReturn(response);

        mockMvc.perform(get("/items/search")
                        .param("text", "дрель"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemId))
                .andExpect(jsonPath("$[0].name").value("Дрель"));
    }

    @Test
    void addComment_shouldReturnCreatedComment() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Очень полезная дрель");
        commentDto.setAuthorName("Ivan");
        commentDto.setCreated(LocalDateTime.now());

        Map<String, Object> responseBody = Map.of("id", 1L, "text", "Очень полезная дрель");
        ResponseEntity<Object> response = ResponseEntity.ok(responseBody);

        when(itemClient.addComment(eq(userId), eq(itemId), any(CommentDto.class))).thenReturn(response);

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.text").value("Очень полезная дрель"));
    }

    @Test
    void getAllItemsByOwner_shouldReturnItems() throws Exception {
        List<Map<String, Object>> responseBody = List.of(
                Map.of("id", itemId, "name", "Дрель"),
                Map.of("id", itemId + 1, "name", "Молоток")
        );
        ResponseEntity<Object> response = ResponseEntity.ok(responseBody);

        when(itemClient.getAllItemsByOwner(userId)).thenReturn(response);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Дрель"))
                .andExpect(jsonPath("$[1].name").value("Молоток"));
    }
}
