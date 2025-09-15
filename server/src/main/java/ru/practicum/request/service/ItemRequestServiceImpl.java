package ru.practicum.request.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.exception.NotFoundException;
import ru.practicum.item.dto.ItemShortDto;
import ru.practicum.item.mapper.ItemMapper;
import ru.practicum.item.repository.ItemRepository;
import ru.practicum.request.dto.ItemRequestDto;
import ru.practicum.request.dto.ItemRequestResponseDto;
import ru.practicum.request.mapper.ItemRequestMapper;
import ru.practicum.request.model.ItemRequest;
import ru.practicum.request.repository.ItemRequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;
import ru.practicum.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemMapper itemMapper;
    private final ItemRequestMapper itemRequestMapper;

    @Override
    @Transactional
    public ItemRequestResponseDto createRequest(Long userId, ItemRequestDto requestDto) {
        User user = userService.findByIdOrThrow(userId);
        ItemRequest itemRequest = itemRequestMapper.toItemRequest(requestDto);
        itemRequest.setRequester(user);
        itemRequest.setCreated(LocalDateTime.now());
        log.info("Creating request: {}", requestDto);
        ItemRequest savedRequest = itemRequestRepository.save(itemRequest);
        log.info("Saved request id: {}", savedRequest.getId());
        return itemRequestMapper.toItemRequestResponseDto(savedRequest, Collections.emptyList());

    }

    @Override
    public List<ItemRequestResponseDto> getUserRequests(Long userId) {
        userService.findByIdOrThrow(userId);
        List<ItemRequest> userRequests = itemRequestRepository.findByRequesterIdOrderByCreatedDesc(userId);
        return userRequests.stream()
                .map(request -> {
                    List<ItemShortDto> items = itemRepository.findByRequestId(request.getId()).stream()
                            .map(itemMapper::toItemShortDto)
                            .collect(Collectors.toList());
                    return itemRequestMapper.toItemRequestResponseDto(request, items);
                })
                .collect(Collectors.toList());
    }


    @Override
    public List<ItemRequestResponseDto> getAllRequests(Long userId) {
        userService.findByIdOrThrow(userId);
        List<ItemRequest> userRequests = itemRequestRepository.findByRequesterIdNotOrderByCreatedDesc(userId);
        return userRequests.stream()
                .map(request -> {
                    List<ItemShortDto> items = itemRepository.findByRequestId(request.getId()).stream()
                            .map(itemMapper::toItemShortDto)
                            .collect(Collectors.toList());
                    return itemRequestMapper.toItemRequestResponseDto(request, items);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ItemRequestResponseDto getRequestById(Long requestId, Long userId) {
        userService.findByIdOrThrow(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("ItemRequest not found"));
        List<ItemShortDto> items = itemRepository.findByRequestId(requestId).stream()
                .map(itemMapper::toItemShortDto)
                .collect(Collectors.toList());
        return itemRequestMapper.toItemRequestResponseDto(itemRequest, items);

    }

}
