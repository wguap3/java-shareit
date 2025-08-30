package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {
    BookingDto create(Long userId, CreateBookingRequestDto requestDto);

    BookingDto approve(Long userId, Long bookingId, Boolean approved);

    BookingDto cancel(Long userId, Long bookingId);

    BookingDto getById(Long userId, Long bookingId);

    List<BookingDto> getAllByUser(Long userId, String state);

    List<BookingDto> getAllByOwner(Long userId, String state);

    Booking findByIdOrThrow(Long bookingId);
}
