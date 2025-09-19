package ru.practicum.booking.strategy.owner;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import ru.practicum.booking.model.Booking;
import ru.practicum.booking.model.BookingState;
import ru.practicum.booking.model.BookingStatus;
import ru.practicum.booking.repository.BookingRepository;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class StatusOwnerBookingStrategy implements OwnerBookingStrategy {
    private final BookingRepository bookingRepository;

    @Override
    public boolean supports(BookingState state) {
        return state == BookingState.WAITING || state == BookingState.REJECTED;
    }

    @Override
    public List<Booking> findBookingsByOwner(Long ownerId, LocalDateTime now, BookingState state) {
        BookingStatus status = BookingStatus.valueOf(state.name());
        return bookingRepository.findByItemOwnerIdAndStatus(ownerId, status, Sort.by(Sort.Direction.DESC, "start"));
    }
}

