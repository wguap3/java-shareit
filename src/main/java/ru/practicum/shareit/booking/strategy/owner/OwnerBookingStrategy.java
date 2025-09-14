package ru.practicum.shareit.booking.strategy.owner;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;

import java.time.LocalDateTime;
import java.util.List;

public interface OwnerBookingStrategy {
    boolean supports(BookingState state);

    List<Booking> findBookingsByOwner(Long ownerId, LocalDateTime now, BookingState state);
}

