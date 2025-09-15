package ru.practicum.booking.strategy.booking;

import ru.practicum.booking.model.Booking;
import ru.practicum.booking.model.BookingState;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingStrategy {
    boolean supports(BookingState state);

    List<Booking> findBookings(Long userId, LocalDateTime now, BookingState state);
}

