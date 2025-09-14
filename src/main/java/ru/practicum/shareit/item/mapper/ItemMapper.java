package ru.practicum.shareit.item.mapper;

import org.mapstruct.*;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    ItemDto toItemDto(Item item);

    @Mapping(target = "ownerId", source = "ownerId")
    Item toItem(ItemDto dto, Long ownerId);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateItemFromDto(ItemDto dto, @MappingTarget Item item);

    @Mapping(target = "lastBooking", expression = "java(mapBookingInfo(lastBookings))")
    @Mapping(target = "nextBooking", expression = "java(mapBookingInfo(nextBookings))")
    @Mapping(target = "comments", source = "comments")
    ItemDto toDtoWithBookings(
            Item item,
            @Param("lastBookings") List<Booking> lastBookings,
            @Param("nextBookings") List<Booking> nextBookings,
            List<CommentDto> comments
    );

    default ItemDto.BookingInfo mapBookingInfo(List<Booking> bookings) {
        if (bookings == null || bookings.isEmpty()) {
            return null;
        }
        Booking booking = bookings.get(0); // берем первый после сортировки
        return new ItemDto.BookingInfo(booking.getId(), booking.getBooker().getId());
    }
}

