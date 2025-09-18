package booking.controller;

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
import ru.practicum.booking.BookingClient;
import ru.practicum.booking.BookingController;
import ru.practicum.booking.dto.CreateBookingRequestDto;
import ru.practicum.booking.enums.BookingState;

import java.time.LocalDateTime;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@ContextConfiguration(classes = GatewayApp.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingClient bookingClient;

    private final Long userId = 1L;
    private final Long bookingId = 10L;

    @Test
    void create_shouldReturnCreatedBooking() throws Exception {
        CreateBookingRequestDto requestDto = new CreateBookingRequestDto();
        requestDto.setItemId(5L);
        requestDto.setStart(LocalDateTime.now().plusDays(1));
        requestDto.setEnd(LocalDateTime.now().plusDays(2));

        Map<String, Object> responseBody = Map.of("id", bookingId, "itemId", 5L);
        ResponseEntity<Object> response = ResponseEntity.ok(responseBody);

        when(bookingClient.createBooking(eq(userId), any(CreateBookingRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.itemId").value(5));
    }

    @Test
    void approve_shouldReturnApprovedBooking() throws Exception {
        Map<String, Object> responseBody = Map.of("id", bookingId, "status", "APPROVED");
        ResponseEntity<Object> response = ResponseEntity.ok(responseBody);

        when(bookingClient.approveBooking(userId, bookingId, true)).thenReturn(response);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void cancel_shouldReturnCanceledBooking() throws Exception {
        Map<String, Object> responseBody = Map.of("id", bookingId, "status", "CANCELED");
        ResponseEntity<Object> response = ResponseEntity.ok(responseBody);

        when(bookingClient.cancelBooking(userId, bookingId)).thenReturn(response);

        mockMvc.perform(patch("/bookings/{bookingId}/cancel", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.status").value("CANCELED"));
    }

    @Test
    void getById_shouldReturnBooking() throws Exception {
        Map<String, Object> responseBody = Map.of("id", bookingId, "itemId", 5L);
        ResponseEntity<Object> response = ResponseEntity.ok(responseBody);

        when(bookingClient.getBooking(userId, bookingId)).thenReturn(response);

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.itemId").value(5));
    }

    @Test
    void getAllByUser_shouldReturnBookings() throws Exception {
        Map<String, Object> responseBody = Map.of("total", 1);
        ResponseEntity<Object> response = ResponseEntity.ok(responseBody);

        when(bookingClient.getBookings(userId, BookingState.ALL)).thenReturn(response);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("stateParam", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1));
    }

    @Test
    void getAllByOwner_shouldReturnBookings() throws Exception {
        Map<String, Object> responseBody = Map.of("total", 2);
        ResponseEntity<Object> response = ResponseEntity.ok(responseBody);

        when(bookingClient.getBookingsByOwner(userId, BookingState.ALL)).thenReturn(response);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("stateParam", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(2));
    }
}

