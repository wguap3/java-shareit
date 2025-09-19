package ru.practicum.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.item.dto.CommentDto;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    private final Long userId = 1L;
    private final Long itemId = 1L;

    @Test
    void addItem_shouldReturnCreatedItem() throws Exception {
        ItemDto itemDto = new ItemDto(null, "Дрель", "Электрическая дрель", true, null, null, null, new ArrayList<>());
        ItemDto returnedDto = new ItemDto(itemId, "Дрель", "Электрическая дрель", true, null, null, null, new ArrayList<>());

        when(itemService.addItem(itemDto, userId)).thenReturn(returnedDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value("Дрель"))
                .andExpect(jsonPath("$.description").value("Электрическая дрель"));

        verify(itemService).addItem(itemDto, userId);
    }

    @Test
    void editingItem_shouldReturnUpdatedItem() throws Exception {
        ItemDto itemDto = new ItemDto(null, "Дрель Pro", "Электрическая дрель с кейсом", true, null, null, null, new ArrayList<>());
        ItemDto updatedDto = new ItemDto(itemId, "Дрель Pro", "Электрическая дрель с кейсом", true, null, null, null, new ArrayList<>());

        when(itemService.editingItem(itemId, itemDto, userId)).thenReturn(updatedDto);

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value("Дрель Pro"))
                .andExpect(jsonPath("$.description").value("Электрическая дрель с кейсом"));

        verify(itemService).editingItem(itemId, itemDto, userId);
    }

    @Test
    void getItemById_shouldReturnItem() throws Exception {
        ItemDto itemDto = new ItemDto(itemId, "Дрель", "Электрическая дрель", true, null, null, null, new ArrayList<>());

        when(itemService.getItemById(itemId, userId)).thenReturn(itemDto);

        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value("Дрель"));

        verify(itemService).getItemById(itemId, userId);
    }

    @Test
    void searchItems_shouldReturnListOfItems() throws Exception {
        String text = "дрель";
        ItemDto itemDto = new ItemDto(itemId, "Дрель", "Электрическая дрель", true, null, null, null, new ArrayList<>());

        when(itemService.searchItems(text)).thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items/search")
                        .param("text", text))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Дрель"));

        verify(itemService).searchItems(text);
    }

    @Test
    void addComment_shouldReturnCreatedComment() throws Exception {
        CommentDto commentDto = new CommentDto(null, "Отличная дрель", "Иван", LocalDateTime.now());
        CommentDto returnedDto = new CommentDto(1L, "Отличная дрель", "Иван", LocalDateTime.now());

        when(itemService.addComment(userId, itemId, commentDto)).thenReturn(returnedDto);

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.text").value("Отличная дрель"));

        verify(itemService).addComment(userId, itemId, commentDto);
    }

    @Test
    void getAllItemsByOwner_shouldReturnListOfItems() throws Exception {
        ItemDto itemDto = new ItemDto(itemId, "Дрель", "Электрическая дрель", true, null, null, null, new ArrayList<>());

        when(itemService.getAllByOwner(userId)).thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Дрель"));

        verify(itemService).getAllByOwner(userId);
    }
}

