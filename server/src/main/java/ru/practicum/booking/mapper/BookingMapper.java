package ru.practicum.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.data.repository.query.Param;
import ru.practicum.booking.dto.BookingDto;
import ru.practicum.booking.dto.CreateBookingRequestDto;
import ru.practicum.booking.model.Booking;
import ru.practicum.booking.model.BookingStatus;
import ru.practicum.item.mapper.ItemMapper;
import ru.practicum.item.model.Item;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;


@Mapper(componentModel = "spring",
        uses = {UserMapper.class, ItemMapper.class},
        imports = {BookingStatus.class})
public interface BookingMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", expression = "java(BookingStatus.WAITING)")
    @Mapping(target = "booker", source = "booker")
    @Mapping(target = "item", source = "item")
    @Mapping(target = "start", source = "requestDto.start")
    @Mapping(target = "end", source = "requestDto.end")
    Booking fromCreateDto(
            CreateBookingRequestDto requestDto,
            @MappingTarget Booking booking,
            Item item,
            User booker
    );

    @Mapping(target = "status", expression = "java(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED)")
    void updateStatus(
            @Param("approved") Boolean approved,
            @MappingTarget Booking booking
    );

    @Mapping(target = "id", source = "booking.id")
    @Mapping(target = "start", source = "booking.start")
    @Mapping(target = "end", source = "booking.end")
    @Mapping(target = "status", source = "booking.status")
    @Mapping(target = "booker", source = "booking.booker")
    @Mapping(target = "item", source = "booking.item")
    BookingDto toDto(Booking booking);
}
