package ru.practicum.booking.strategy.owner;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import ru.practicum.booking.model.Booking;
import ru.practicum.booking.model.BookingState;
import ru.practicum.booking.repository.BookingRepository;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FutureOwnerBookingStrategy implements OwnerBookingStrategy {

    private final BookingRepository bookingRepository;

    @Override
    public boolean supports(BookingState state) {
        return state == BookingState.FUTURE;
    }

    @Override
    public List<Booking> findBookingsByOwner(Long ownerId, LocalDateTime now, BookingState state) {
        return bookingRepository.findByItemOwnerIdAndStartAfter(
                ownerId, now, Sort.by(Sort.Direction.DESC, "start"));
    }
}

