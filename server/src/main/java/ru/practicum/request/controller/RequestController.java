package ru.practicum.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.ItemRequestDto;
import ru.practicum.request.dto.ItemRequestResponseDto;
import ru.practicum.request.service.ItemRequestService;

import java.util.List;

import static ru.practicum.common.HttpHeadersConstants.USER_ID_HEADER;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class RequestController {

    private final ItemRequestService requestService;

    @PostMapping
    public ItemRequestResponseDto createRequest(@RequestHeader(USER_ID_HEADER) Long userId,
                                                @RequestBody ItemRequestDto requestDto) {
        return requestService.createRequest(userId, requestDto);
    }

    @GetMapping("/all")
    public List<ItemRequestResponseDto> getAllRequests(@RequestHeader(USER_ID_HEADER) Long userId) {
        return requestService.getAllRequests(userId);
    }

    @GetMapping
    public List<ItemRequestResponseDto> getUserRequests(@RequestHeader(USER_ID_HEADER) Long userId) {
        return requestService.getUserRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestResponseDto getRequestById(@RequestHeader(USER_ID_HEADER) Long userId,
                                                 @PathVariable Long requestId) {
        return requestService.getRequestById(requestId, userId);
    }
}
