package ru.practicum.shareit.booking.strategy.booking;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingStrategy {
    boolean supports(BookingState state);

    List<Booking> findBookings(Long userId, LocalDateTime now, BookingState state);
}

