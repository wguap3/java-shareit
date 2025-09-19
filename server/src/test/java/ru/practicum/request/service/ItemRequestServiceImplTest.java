package ru.practicum.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.exception.NotFoundException;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private ItemRequestMapper itemRequestMapper;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private final Long userId = 1L;

    @Test
    void createRequest_shouldSaveAndReturnRequest() {
        ItemRequestDto requestDto = new ItemRequestDto("Нужна дрель");
        User user = new User(userId, "Иван", "ivan@test.com");

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(requestDto.getDescription());
        itemRequest.setRequester(user);
        itemRequest.setCreated(LocalDateTime.now());

        ItemRequest savedRequest = new ItemRequest();
        savedRequest.setId(1L);
        savedRequest.setDescription(requestDto.getDescription());
        savedRequest.setRequester(user);
        savedRequest.setCreated(LocalDateTime.now());

        ItemRequestResponseDto responseDto = new ItemRequestResponseDto(1L, "Нужна дрель", LocalDateTime.now(), Collections.emptyList());

        when(userService.findByIdOrThrow(userId)).thenReturn(user);
        when(itemRequestMapper.toItemRequest(requestDto)).thenReturn(itemRequest);
        when(itemRequestRepository.save(itemRequest)).thenReturn(savedRequest);
        when(itemRequestMapper.toItemRequestResponseDto(savedRequest, Collections.emptyList())).thenReturn(responseDto);

        ItemRequestResponseDto result = itemRequestService.createRequest(userId, requestDto);

        assertEquals(responseDto.getId(), result.getId());
        assertEquals(responseDto.getDescription(), result.getDescription());

        verify(userService).findByIdOrThrow(userId);
        verify(itemRequestRepository).save(itemRequest);
    }

    @Test
    void getUserRequests_shouldReturnRequestsForUser() {
        User user = new User(userId, "Иван", "ivan@test.com");

        ItemRequest request = new ItemRequest();
        request.setId(1L);
        request.setDescription("Нужна дрель");
        request.setRequester(user);
        request.setCreated(LocalDateTime.now());

        ItemRequestResponseDto responseDto = new ItemRequestResponseDto(1L, "Нужна дрель", LocalDateTime.now(), Collections.emptyList());

        when(userService.findByIdOrThrow(userId)).thenReturn(user);
        when(itemRequestRepository.findByRequesterIdOrderByCreatedDesc(userId)).thenReturn(List.of(request));
        when(itemRepository.findByRequestId(request.getId())).thenReturn(Collections.emptyList());
        when(itemRequestMapper.toItemRequestResponseDto(request, Collections.emptyList())).thenReturn(responseDto);

        List<ItemRequestResponseDto> result = itemRequestService.getUserRequests(userId);

        assertEquals(1, result.size());
        assertEquals(responseDto.getDescription(), result.get(0).getDescription());

        verify(userService).findByIdOrThrow(userId);
        verify(itemRequestRepository).findByRequesterIdOrderByCreatedDesc(userId);
    }

    @Test
    void getAllRequests_shouldReturnRequestsForOtherUsers() {
        User user = new User(userId, "Иван", "ivan@test.com");
        User otherUser = new User(2L, "Мария", "maria@test.com");

        ItemRequest request = new ItemRequest();
        request.setId(1L);
        request.setDescription("Нужна дрель");
        request.setRequester(otherUser);
        request.setCreated(LocalDateTime.now());

        ItemRequestResponseDto responseDto = new ItemRequestResponseDto(1L, "Нужна дрель", LocalDateTime.now(), Collections.emptyList());

        when(userService.findByIdOrThrow(userId)).thenReturn(user);
        when(itemRequestRepository.findByRequesterIdNotOrderByCreatedDesc(userId)).thenReturn(List.of(request));
        when(itemRepository.findByRequestId(request.getId())).thenReturn(Collections.emptyList());
        when(itemRequestMapper.toItemRequestResponseDto(request, Collections.emptyList())).thenReturn(responseDto);

        List<ItemRequestResponseDto> result = itemRequestService.getAllRequests(userId);

        assertEquals(1, result.size());
        assertEquals("Нужна дрель", result.get(0).getDescription());

        verify(userService).findByIdOrThrow(userId);
        verify(itemRequestRepository).findByRequesterIdNotOrderByCreatedDesc(userId);
    }

    @Test
    void getRequestById_shouldReturnRequest() {
        User user = new User(userId, "Иван", "ivan@test.com");

        ItemRequest request = new ItemRequest();
        request.setId(1L);
        request.setDescription("Нужна дрель");
        request.setRequester(user);
        request.setCreated(LocalDateTime.now());

        ItemRequestResponseDto responseDto = new ItemRequestResponseDto(1L, "Нужна дрель", LocalDateTime.now(), Collections.emptyList());

        when(userService.findByIdOrThrow(userId)).thenReturn(user);
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(itemRepository.findByRequestId(1L)).thenReturn(Collections.emptyList());
        when(itemRequestMapper.toItemRequestResponseDto(request, Collections.emptyList())).thenReturn(responseDto);

        ItemRequestResponseDto result = itemRequestService.getRequestById(1L, userId);

        assertEquals(responseDto.getId(), result.getId());
        assertEquals("Нужна дрель", result.getDescription());

        verify(userService).findByIdOrThrow(userId);
        verify(itemRequestRepository).findById(1L);
    }

    @Test
    void getRequestById_shouldThrowNotFoundException() {
        User user = new User(userId, "Иван", "ivan@test.com");

        when(userService.findByIdOrThrow(userId)).thenReturn(user);
        when(itemRequestRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.getRequestById(999L, userId));

        verify(userService).findByIdOrThrow(userId);
        verify(itemRequestRepository).findById(999L);
    }
}