package ru.practicum.shareit.booking.strategy.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.repository.BookingRepository;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FutureBookingStrategy implements BookingStrategy {
    private final BookingRepository bookingRepository;

    @Override
    public boolean supports(BookingState state) {
        return state == BookingState.FUTURE;
    }

    @Override
    public List<Booking> findBookings(Long userId, LocalDateTime now, BookingState state) {
        return bookingRepository.findByBookerIdAndStartAfter(
                userId, now, Sort.by(Sort.Direction.DESC, "start"));
    }
}
