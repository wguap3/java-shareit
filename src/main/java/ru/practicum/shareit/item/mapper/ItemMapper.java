package ru.practicum.shareit.item.mapper;

import org.mapstruct.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    ItemDto toItemDto(Item item);

    @Mapping(target = "ownerId", source = "ownerId")
    Item toItem(ItemDto dto, Long ownerId);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateItemFromDto(ItemDto dto, @MappingTarget Item item);
}

