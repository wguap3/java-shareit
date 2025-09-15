package ru.practicum.item.dto;

import lombok.Data;
import ru.practicum.booking.dto.BookingDto;

@Data
public class ItemOwnerDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long ownerId;
    private BookingDto lastBooking;
    private BookingDto nextBooking;
}