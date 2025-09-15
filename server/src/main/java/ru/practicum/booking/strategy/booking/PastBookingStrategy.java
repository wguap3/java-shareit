package ru.practicum.booking.strategy.booking;

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
public class PastBookingStrategy implements BookingStrategy {
    private final BookingRepository bookingRepository;

    @Override
    public boolean supports(BookingState state) {
        return state == BookingState.PAST;
    }

    @Override
    public List<Booking> findBookings(Long userId, LocalDateTime now, BookingState state) {
        return bookingRepository.findByBookerIdAndEndBefore(
                userId, now, Sort.by(Sort.Direction.DESC, "start"));
    }
}
