package ru.practicum.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.booking.BookingClient;
import ru.practicum.booking.dto.CreateBookingRequestDto;
import ru.practicum.booking.enums.BookingState;
import ru.practicum.request.dto.ItemRequestDto;

import static ru.practicum.constants.Headers.USER_ID_HEADER;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
@Validated
public class RequestController {
    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader(USER_ID_HEADER) Long userId,
                                         @RequestBody @Valid ItemRequestDto requestDto) {
        return requestClient.createRequest(userId, requestDto);
    }


    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader(USER_ID_HEADER) Long userId) {
        return requestClient.getAllRequests(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserRequest(@RequestHeader(USER_ID_HEADER) Long userId) {
        return requestClient.getUserRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader(USER_ID_HEADER) Long userId,
                                                @PathVariable Long requestId) {
        return requestClient.getRequestById(userId, requestId);
    }
}
