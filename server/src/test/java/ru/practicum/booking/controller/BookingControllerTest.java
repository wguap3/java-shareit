package ru.practicum.booking.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.booking.dto.BookingDto;
import ru.practicum.booking.dto.CreateBookingRequestDto;
import ru.practicum.booking.model.BookingStatus;
import ru.practicum.booking.service.BookingService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    private final Long userId = 1L;
    private final Long bookingId = 10L;

    private BookingDto bookingDto;
    private CreateBookingRequestDto createRequestDto;

    @BeforeEach
    void setup() {
        BookingDto.BookerDto booker = new BookingDto.BookerDto();
        booker.setId(userId);
        booker.setName("Иван");

        BookingDto.ItemDto item = new BookingDto.ItemDto();
        item.setId(5L);
        item.setName("Дрель");

        bookingDto = new BookingDto(
                bookingId,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                BookingStatus.APPROVED,
                booker,
                item
        );

        createRequestDto = new CreateBookingRequestDto();
        createRequestDto.setItemId(5L);
        createRequestDto.setStart(LocalDateTime.now().plusDays(1));
        createRequestDto.setEnd(LocalDateTime.now().plusDays(2));
    }

    @Test
    void create_shouldReturnBooking() {
        when(bookingService.create(userId, createRequestDto)).thenReturn(bookingDto);

        BookingDto result = bookingController.create(userId, createRequestDto);

        assertEquals(bookingDto.getId(), result.getId());
        assertEquals(bookingDto.getStatus(), result.getStatus());
        verify(bookingService).create(userId, createRequestDto);
    }

    @Test
    void approve_shouldReturnBooking() {
        when(bookingService.approve(userId, bookingId, true)).thenReturn(bookingDto);

        BookingDto result = bookingController.approve(userId, bookingId, true);

        assertEquals(bookingDto.getId(), result.getId());
        verify(bookingService).approve(userId, bookingId, true);
    }

    @Test
    void cancel_shouldReturnBooking() {
        when(bookingService.cancel(userId, bookingId)).thenReturn(bookingDto);

        BookingDto result = bookingController.cancel(userId, bookingId);

        assertEquals(bookingDto.getId(), result.getId());
        verify(bookingService).cancel(userId, bookingId);
    }

    @Test
    void getById_shouldReturnBooking() {
        when(bookingService.getById(userId, bookingId)).thenReturn(bookingDto);

        BookingDto result = bookingController.getById(userId, bookingId);

        assertEquals(bookingDto.getId(), result.getId());
        verify(bookingService).getById(userId, bookingId);
    }

    @Test
    void getAllByOwner_shouldReturnListOfBookings() {
        when(bookingService.getAllByOwner(userId, "ALL")).thenReturn(List.of(bookingDto));

        List<BookingDto> result = bookingController.getAllByOwner(userId, "ALL");

        assertEquals(1, result.size());
        assertEquals(bookingDto.getId(), result.get(0).getId());
        verify(bookingService).getAllByOwner(userId, "ALL");
    }
}