package booking.client;

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
import ru.practicum.booking.BookingClient;
import ru.practicum.booking.dto.CreateBookingRequestDto;
import ru.practicum.booking.enums.BookingState;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static ru.practicum.constants.Headers.USER_ID_HEADER;

@ExtendWith(MockitoExtension.class)
class BookingClientTest {

    @Mock
    private RestTemplate restTemplate;

    private BookingClient bookingClient;

    private final long userId = 1L;
    private final long bookingId = 10L;

    @BeforeEach
    void setUp() {
        bookingClient = new BookingClient(restTemplate);
    }

    @Test
    void getBookings_shouldSendGetWithState() {
        BookingState state = BookingState.ALL;
        ResponseEntity<Object> expected = ResponseEntity.ok("[]");

        when(restTemplate.exchange(eq("?state={state}"), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class), eq(Map.of("state", state.name()))))
                .thenReturn(expected);

        ResponseEntity<Object> response = bookingClient.getBookings(userId, state);

        assertThat(response).isEqualTo(expected);

        ArgumentCaptor<HttpEntity<Void>> captor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(eq("?state={state}"), eq(HttpMethod.GET), captor.capture(), eq(Object.class), eq(Map.of("state", state.name())));
        assertThat(captor.getValue().getHeaders().getFirst(USER_ID_HEADER)).isEqualTo(String.valueOf(userId));
    }

    @Test
    void createBooking_shouldSendPost() {
        CreateBookingRequestDto requestDto = new CreateBookingRequestDto();
        ResponseEntity<Object> expected = ResponseEntity.ok(Map.of("id", bookingId));

        when(restTemplate.exchange(eq(""), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expected);

        ResponseEntity<Object> response = bookingClient.createBooking(userId, requestDto);

        assertThat(response).isEqualTo(expected);

        ArgumentCaptor<HttpEntity<CreateBookingRequestDto>> captor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(eq(""), eq(HttpMethod.POST), captor.capture(), eq(Object.class));
        assertThat(captor.getValue().getHeaders().getFirst(USER_ID_HEADER)).isEqualTo(String.valueOf(userId));
    }

    @Test
    void getBooking_shouldSendGet() {
        ResponseEntity<Object> expected = ResponseEntity.ok(Map.of("id", bookingId));

        when(restTemplate.exchange(eq("/" + bookingId), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expected);

        ResponseEntity<Object> response = bookingClient.getBooking(userId, bookingId);

        assertThat(response).isEqualTo(expected);
        verify(restTemplate).exchange(eq("/" + bookingId), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void approveBooking_shouldSendPatch() {
        ResponseEntity<Object> expected = ResponseEntity.ok(Map.of("id", bookingId));

        when(restTemplate.exchange(eq("/" + bookingId + "?approved=true"), eq(HttpMethod.PATCH), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expected);

        ResponseEntity<Object> response = bookingClient.approveBooking(userId, bookingId, true);

        assertThat(response).isEqualTo(expected);
        verify(restTemplate).exchange(eq("/" + bookingId + "?approved=true"), eq(HttpMethod.PATCH), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void cancelBooking_shouldSendPatch() {
        ResponseEntity<Object> expected = ResponseEntity.ok(Map.of("id", bookingId));

        when(restTemplate.exchange(eq("/" + bookingId + "/cancel"), eq(HttpMethod.PATCH), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expected);

        ResponseEntity<Object> response = bookingClient.cancelBooking(userId, bookingId);

        assertThat(response).isEqualTo(expected);
        verify(restTemplate).exchange(eq("/" + bookingId + "/cancel"), eq(HttpMethod.PATCH), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void getBookingsByOwner_shouldSendGetWithState() {
        BookingState state = BookingState.REJECTED;
        ResponseEntity<Object> expected = ResponseEntity.ok("[]");

        when(restTemplate.exchange(eq("/owner?state={state}"), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class), eq(Map.of("state", state.name()))))
                .thenReturn(expected);

        ResponseEntity<Object> response = bookingClient.getBookingsByOwner(userId, state);

        assertThat(response).isEqualTo(expected);

        ArgumentCaptor<HttpEntity<Void>> captor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(eq("/owner?state={state}"), eq(HttpMethod.GET), captor.capture(), eq(Object.class), eq(Map.of("state", state.name())));
        assertThat(captor.getValue().getHeaders().getFirst(USER_ID_HEADER)).isEqualTo(String.valueOf(userId));
    }
}

