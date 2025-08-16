package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static ru.practicum.shareit.common.HttpHeadersConstants.USER_ID_HEADER;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto addItem(@RequestHeader(USER_ID_HEADER) Long ownerId,
                           @RequestBody @Valid ItemDto itemDto) {
        return itemService.addItem(itemDto, ownerId);

    }

    @PatchMapping("/{itemId}")
    public ItemDto editingItem(@RequestHeader(USER_ID_HEADER) Long ownerId,
                               @PathVariable Long itemId,
                               @RequestBody ItemDto itemDto) {
        return itemService.editingItem(itemId, itemDto, ownerId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Long itemId) {
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public List<ItemDto> getItemsByOwner(@RequestHeader(USER_ID_HEADER) Long ownerId) {
        return itemService.getItemsByOwner(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam("text") String text) {
        return itemService.searchItems(text);
    }


}
