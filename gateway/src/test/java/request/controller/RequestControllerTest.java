package request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.request.RequestClient;
import ru.practicum.request.RequestController;
import ru.practicum.request.dto.ItemRequestDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RequestController.class)
class RequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RequestClient requestClient;

    private final Long userId = 1L;

    @Test
    void createRequest_shouldReturnResponseFromClient() throws Exception {
        ItemRequestDto dto = new ItemRequestDto("Нужна дрель");
        ResponseEntity<Object> clientResponse = ResponseEntity.ok("created");

        Mockito.when(requestClient.createRequest(eq(userId), any(ItemRequestDto.class)))
                .thenReturn(clientResponse);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("created"));
    }

    @Test
    void getAllRequests_shouldReturnResponseFromClient() throws Exception {
        ResponseEntity<Object> clientResponse = ResponseEntity.ok(List.of("r1", "r2"));

        Mockito.when(requestClient.getAllRequests(userId)).thenReturn(clientResponse);

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getUserRequest_shouldReturnResponseFromClient() throws Exception {
        ResponseEntity<Object> clientResponse = ResponseEntity.ok(List.of("r1"));

        Mockito.when(requestClient.getUserRequests(userId)).thenReturn(clientResponse);

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getRequestById_shouldReturnResponseFromClient() throws Exception {
        Long requestId = 5L;
        ResponseEntity<Object> clientResponse = ResponseEntity.ok("request5");

        Mockito.when(requestClient.getRequestById(userId, requestId)).thenReturn(clientResponse);

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().string("request5"));
    }
}
