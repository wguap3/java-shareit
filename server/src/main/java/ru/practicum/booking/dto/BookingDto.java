package ru.practicum.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.booking.model.BookingStatus;

import java.time.LocalDateTime;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class BookingDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
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
