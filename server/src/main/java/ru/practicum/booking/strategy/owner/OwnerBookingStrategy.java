package ru.practicum.booking.strategy.owner;

import ru.practicum.booking.model.Booking;
import ru.practicum.booking.model.BookingState;

import java.time.LocalDateTime;
import java.util.List;

public interface OwnerBookingStrategy {
    boolean supports(BookingState state);

    List<Booking> findBookingsByOwner(Long ownerId, LocalDateTime now, BookingState state);
}

