package ru.practicum.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.booking.enums.BookingStatus;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class BookingDto {
    private Long id;
    @NotNull
    private LocalDateTime start;
    @NotNull
    private LocalDateTime end;
    @NotNull
    private BookingStatus status;
    private BookerDto booker;
    private ItemDto item;

    @Data
    public static class BookerDto {
        private Long id;
        private String name;
    }

    @Data
    public static class ItemDto {
        private Long id;
        private String name;
    }
}
