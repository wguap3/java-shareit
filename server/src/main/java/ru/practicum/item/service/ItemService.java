package ru.practicum.item.service;


import ru.practicum.item.dto.CommentDto;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto addItem(ItemDto itemDto, Long ownerId);

    ItemDto editingItem(Long itemId, ItemDto itemDto, Long ownerId);

    ItemDto getItemById(Long itemId, Long userId);

    List<ItemDto> searchItems(String text);

    CommentDto addComment(Long userId, Long itemId, CommentDto commentDto);

    List<ItemDto> getAllByOwner(Long ownerId);

    Item findByIdOrThrow(Long itemId);
}
