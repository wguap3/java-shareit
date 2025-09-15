package ru.practicum.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CreateBookingRequestDto {
    private Long itemId;

    @FutureOrPresent
    private LocalDateTime start;

    @Future
    private LocalDateTime end;
}
