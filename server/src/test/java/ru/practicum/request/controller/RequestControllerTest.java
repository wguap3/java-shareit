package ru.practicum.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.request.dto.ItemRequestDto;
import ru.practicum.request.dto.ItemRequestResponseDto;
import ru.practicum.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RequestController.class)
class RequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService requestService;

    private Long userId = 1L;

    @Test
    void createRequest_shouldReturnCreatedRequest() throws Exception {
        ItemRequestDto dto = new ItemRequestDto("Нужна дрель");
        ItemRequestResponseDto responseDto = new ItemRequestResponseDto(1L, "Нужна дрель", LocalDateTime.now(), List.of());

        when(requestService.createRequest(eq(userId), any(ItemRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Нужна дрель"))
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getUserRequests_shouldReturnRequestsForUser() throws Exception {
        ItemRequestResponseDto responseDto = new ItemRequestResponseDto(1L, "Нужна дрель", LocalDateTime.now(), List.of());
        when(requestService.getUserRequests(userId)).thenReturn(List.of(responseDto));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value("Нужна дрель"))
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void getAllRequests_shouldReturnAllRequests() throws Exception {
        ItemRequestResponseDto dto1 = new ItemRequestResponseDto(1L, "Нужна дрель", LocalDateTime.now(), List.of());
        ItemRequestResponseDto dto2 = new ItemRequestResponseDto(2L, "Нужен молоток", LocalDateTime.now(), List.of());

        when(requestService.getAllRequests(userId)).thenReturn(List.of(dto1, dto2));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getRequestById_shouldReturnRequest() throws Exception {
        ItemRequestResponseDto responseDto = new ItemRequestResponseDto(1L, "Нужна дрель", LocalDateTime.now(), List.of());
        when(requestService.getRequestById(1L, userId)).thenReturn(responseDto);

        mockMvc.perform(get("/requests/{requestId}", 1L)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Нужна дрель"))
                .andExpect(jsonPath("$.id").value(1L));
    }
}

