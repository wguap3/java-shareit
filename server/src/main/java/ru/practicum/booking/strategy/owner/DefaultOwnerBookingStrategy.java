package ru.practicum.booking.strategy.owner;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.booking.model.Booking;
import ru.practicum.booking.model.BookingState;
import ru.practicum.booking.repository.BookingRepository;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DefaultOwnerBookingStrategy implements OwnerBookingStrategy {

    private final BookingRepository bookingRepository;

    @Override
    public boolean supports(BookingState state) {
        return state == BookingState.ALL;
    }

    @Override
    public List<Booking> findBookingsByOwner(Long ownerId, LocalDateTime now, BookingState state) {
        return bookingRepository.findByItemOwnerIdOrderByStartDesc(ownerId);
    }
}
