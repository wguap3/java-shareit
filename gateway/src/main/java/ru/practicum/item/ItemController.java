package ru.practicum.item;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.item.dto.CommentDto;
import ru.practicum.item.dto.ItemDto;

import static ru.practicum.constants.Headers.USER_ID_HEADER;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader(USER_ID_HEADER) Long ownerId,
                                          @RequestBody @Valid ItemDto itemDto) {
        return itemClient.addItem(ownerId, itemDto);

    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> editingItem(@RequestHeader(USER_ID_HEADER) Long ownerId,
                                              @PathVariable Long itemId,
                                              @RequestBody ItemDto itemDto) {
        return itemClient.editingItem(ownerId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable Long itemId, @RequestHeader(USER_ID_HEADER) Long userId) {
        return itemClient.getItemById(userId, itemId);
    }


    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam("text") String text) {
        return itemClient.searchItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(USER_ID_HEADER) Long userId,
                                             @PathVariable Long itemId,
                                             @RequestBody @Valid CommentDto commentDto) {
        return itemClient.addComment(userId, itemId, commentDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemsByOwner(@RequestHeader(USER_ID_HEADER) Long userId) {
        return itemClient.getAllItemsByOwner(userId);
    }
}
