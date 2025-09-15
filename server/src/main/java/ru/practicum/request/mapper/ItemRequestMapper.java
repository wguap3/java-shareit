package ru.practicum.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.item.dto.ItemShortDto;
import ru.practicum.request.dto.ItemRequestDto;
import ru.practicum.request.dto.ItemRequestResponseDto;
import ru.practicum.request.model.ItemRequest;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemRequestMapper {


    ItemRequestDto toItemRequestDto(ItemRequest itemRequest);

    ItemRequest toItemRequest(ItemRequestDto dto);

    @Mapping(target = "items", source = "items")
    ItemRequestResponseDto toItemRequestResponseDto(ItemRequest request, List<ItemShortDto> items);


}