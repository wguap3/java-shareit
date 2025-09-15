package ru.practicum.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
    private BookingInfo lastBooking;
    private BookingInfo nextBooking;
    private List<CommentDto> comments;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookingInfo {
        private Long id;
        private Long bookerId;
    }
}
