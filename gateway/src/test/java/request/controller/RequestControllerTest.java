package request.controller;

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
import ru.practicum.request.RequestClient;
import ru.practicum.request.RequestController;
import ru.practicum.request.dto.ItemRequestDto;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.constants.Headers.USER_ID_HEADER;

@WebMvcTest(RequestController.class)
@ContextConfiguration(classes = GatewayApp.class)
class RequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RequestClient requestClient;

    private final Long userId = 1L;

    @Test
    void createRequest_shouldReturnCreatedRequest() throws Exception {
        ItemRequestDto dto = new ItemRequestDto("Нужна дрель");

        Map<String, Object> responseBody = Map.of(
                "id", 1L,
                "description", "Нужна дрель"
        );
        ResponseEntity<Object> response = ResponseEntity.ok(responseBody);

        when(requestClient.createRequest(eq(userId), any(ItemRequestDto.class)))
                .thenReturn(response);

        mockMvc.perform(post("/requests")
                        .header(USER_ID_HEADER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Нужна дрель"))
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getUserRequests_shouldReturnRequestsForUser() throws Exception {
        List<Map<String, Object>> responseBody = List.of(
                Map.of("id", 1L, "description", "Нужна дрель")
        );
        ResponseEntity<Object> response = ResponseEntity.ok(responseBody);

        when(requestClient.getUserRequests(userId)).thenReturn(response);

        mockMvc.perform(get("/requests")
                        .header(USER_ID_HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value("Нужна дрель"))
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void getAllRequests_shouldReturnAllRequests() throws Exception {
        List<Map<String, Object>> responseBody = List.of(
                Map.of("id", 1L, "description", "Нужна дрель"),
                Map.of("id", 2L, "description", "Нужен молоток")
        );
        ResponseEntity<Object> response = ResponseEntity.ok(responseBody);

        when(requestClient.getAllRequests(userId)).thenReturn(response);

        mockMvc.perform(get("/requests/all")
                        .header(USER_ID_HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getRequestById_shouldReturnRequest() throws Exception {
        Map<String, Object> responseBody = Map.of(
                "id", 1L,
                "description", "Нужна дрель"
        );
        ResponseEntity<Object> response = ResponseEntity.ok(responseBody);

        when(requestClient.getRequestById(userId, 1L)).thenReturn(response);

        mockMvc.perform(get("/requests/{requestId}", 1L)
                        .header(USER_ID_HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Нужна дрель"))
                .andExpect(jsonPath("$.id").value(1L));
    }
}
