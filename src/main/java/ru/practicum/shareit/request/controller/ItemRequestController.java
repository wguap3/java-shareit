package ru.practicum.shareit.request.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;

import java.util.List;

import static ru.practicum.shareit.constants.Headers.USER_ID_HEADER;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestResponseDto createRequest(@RequestHeader (USER_ID_HEADER) Long userId,
                                                @RequestBody @Valid ItemRequestDto requestDto){
        return itemRequestService.createRequest(userId,requestDto);
    }

    @GetMapping
    public List<ItemRequestResponseDto> getUserRequests(@RequestHeader(USER_ID_HEADER) Long userId) {
        return itemRequestService.getUserRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestResponseDto> getAllRequests(@RequestHeader(USER_ID_HEADER) Long userId) {
        return itemRequestService.getAllRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestResponseDto getRequestById(@RequestHeader(USER_ID_HEADER) Long userId,
                                                 @PathVariable Long requestId) {
        return itemRequestService.getRequestById(requestId, userId);
    }



}
