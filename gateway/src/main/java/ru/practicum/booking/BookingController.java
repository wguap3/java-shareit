package ru.practicum.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.booking.dto.CreateBookingRequestDto;
import ru.practicum.booking.enums.BookingState;

import static ru.practicum.constants.Headers.USER_ID_HEADER;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(USER_ID_HEADER) Long userId,
                                         @RequestBody @Valid CreateBookingRequestDto requestDto) {
        return bookingClient.createBooking(userId, requestDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approve(@RequestHeader(USER_ID_HEADER) Long userId,
                                          @PathVariable Long bookingId,
                                          @RequestParam Boolean approved) {
        return bookingClient.approveBooking(userId, bookingId, approved);
    }

    @PatchMapping("/{bookingId}/cancel")
    public ResponseEntity<Object> cancel(@RequestHeader(USER_ID_HEADER) Long userId,
                                         @PathVariable Long bookingId) {
        return bookingClient.cancelBooking(userId, bookingId);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getById(@RequestHeader(USER_ID_HEADER) Long userId,
                                          @PathVariable Long bookingId) {
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByUser(@RequestHeader(USER_ID_HEADER) Long userId,
                                               @RequestParam(defaultValue = "ALL") String stateParam) {
        BookingState state = BookingState.fromString(stateParam);
        return bookingClient.getBookings(userId, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllByOwner(@RequestHeader(USER_ID_HEADER) Long userId,
                                                @RequestParam(defaultValue = "ALL") String stateParam) {
        BookingState state = BookingState.fromString(stateParam);
        return bookingClient.getBookingsByOwner(userId, state);
    }
}
