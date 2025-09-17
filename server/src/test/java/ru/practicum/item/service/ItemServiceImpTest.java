package ru.practicum.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.booking.repository.BookingRepository;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.UnauthorizedActionException;
import ru.practicum.item.dto.CommentDto;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.item.mapper.CommentMapper;
import ru.practicum.item.mapper.ItemMapper;
import ru.practicum.item.model.Comment;
import ru.practicum.item.model.Item;
import ru.practicum.item.repository.CommentRepository;
import ru.practicum.item.repository.ItemRepository;
import ru.practicum.request.repository.ItemRequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;
import ru.practicum.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImpTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @InjectMocks
    private ItemServiceImp itemService;

    private final Long userId = 1L;
    private final Long itemId = 1L;

    @Test
    void addItem_shouldSaveItem() {
        User owner = new User(userId, "Иван", "ivan@test.com");
        ItemDto itemDto = new ItemDto(itemId, "Дрель", "Электрическая", true, null, null, null, null);
        Item item = new Item();
        Item savedItem = new Item();

        when(userService.findByIdOrThrow(userId)).thenReturn(owner);
        when(itemMapper.toItem(itemDto, userId)).thenReturn(item);
        when(itemRepository.save(item)).thenReturn(savedItem);
        when(itemMapper.toItemDto(savedItem)).thenReturn(itemDto);

        ItemDto result = itemService.addItem(itemDto, userId);

        assertEquals(itemDto, result);
        verify(itemRepository).save(item);
        verify(userService).findByIdOrThrow(userId);
    }

    @Test
    void editingItem_shouldUpdateItem_whenOwnerMatches() {
        Item existingItem = new Item();
        existingItem.setOwnerId(userId);
        ItemDto updateDto = new ItemDto();
        Item updatedItem = new Item();
        ItemDto updatedDto = new ItemDto();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));
        doNothing().when(itemMapper).updateItemFromDto(updateDto, existingItem);
        when(itemRepository.save(existingItem)).thenReturn(updatedItem);
        when(itemMapper.toItemDto(updatedItem)).thenReturn(updatedDto);

        ItemDto result = itemService.editingItem(itemId, updateDto, userId);

        assertEquals(updatedDto, result);
        verify(itemMapper).updateItemFromDto(updateDto, existingItem);
        verify(itemRepository).save(existingItem);
    }


    @Test
    void editingItem_shouldThrowException_whenOwnerDoesNotMatch() {
        Item existingItem = new Item();
        existingItem.setOwnerId(2L); // другой владелец
        ItemDto updateDto = new ItemDto();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));

        assertThrows(UnauthorizedActionException.class,
                () -> itemService.editingItem(itemId, updateDto, userId));
    }

    @Test
    void getItemById_shouldReturnItemWithComments() {
        Item item = new Item();
        item.setOwnerId(userId);
        List<Comment> comments = List.of(new Comment());
        List<CommentDto> commentDtos = List.of(new CommentDto());

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.findByItemIdAndEndBefore(anyLong(), any(), any())).thenReturn(Collections.emptyList());
        when(bookingRepository.findByItemIdAndStartAfter(anyLong(), any(), any())).thenReturn(Collections.emptyList());
        when(commentRepository.findByItemId(itemId)).thenReturn(comments);
        when(commentMapper.toDto(any())).thenReturn(commentDtos.get(0));
        when(itemMapper.toDtoWithBookings(item, Collections.emptyList(), Collections.emptyList(), commentDtos))
                .thenReturn(new ItemDto());

        ItemDto result = itemService.getItemById(itemId, userId);

        assertNotNull(result);
    }

    @Test
    void getAllByOwner_shouldReturnItems() {
        Item item = new Item();
        List<Item> items = List.of(item);
        ItemDto dto = new ItemDto();

        when(itemRepository.findByOwnerId(userId)).thenReturn(items);
        when(itemMapper.toItemDto(item)).thenReturn(dto);

        List<ItemDto> result = itemService.getAllByOwner(userId);

        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));
    }

    @Test
    void searchItems_shouldReturnMatchingItems() {
        String text = "дрель";
        Item item = new Item();
        List<Item> items = List.of(item);
        ItemDto dto = new ItemDto();

        when(itemRepository.searchAvailableByText(text)).thenReturn(items);
        when(itemMapper.toItemDto(item)).thenReturn(dto);

        List<ItemDto> result = itemService.searchItems(text);

        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));
    }

    @Test
    void searchItems_shouldReturnEmpty_whenTextBlank() {
        List<ItemDto> result = itemService.searchItems(" ");
        assertTrue(result.isEmpty());
    }

    @Test
    void addComment_shouldSaveComment_whenUserBookedItem() {
        Item item = new Item();
        User user = new User(userId, "Иван", "ivan@test.com");
        CommentDto commentDto = new CommentDto();
        Comment comment = new Comment();
        CommentDto savedDto = new CommentDto();

        when(userRepository.findById(eq(userId))).thenReturn(Optional.of(user));
        when(itemRepository.findById(eq(itemId))).thenReturn(Optional.of(item));
        when(bookingRepository.existsByItemIdAndBookerIdAndEndBefore(
                eq(itemId), eq(userId), any(LocalDateTime.class)))
                .thenReturn(true);
        when(commentMapper.fromDto(eq(commentDto), eq(item), eq(user))).thenReturn(comment);
        when(commentRepository.save(eq(comment))).thenReturn(comment);
        when(commentMapper.toDto(eq(comment))).thenReturn(savedDto);

        CommentDto result = itemService.addComment(userId, itemId, commentDto);

        assertEquals(savedDto, result);
        verify(commentRepository).save(comment);
    }

    @Test
    void addComment_shouldThrowException_whenUserNotBooked() {
        Item item = new Item();
        User user = new User(userId, "Иван", "ivan@test.com");
        CommentDto commentDto = new CommentDto();

        when(userRepository.findById(eq(userId))).thenReturn(Optional.of(user));
        when(itemRepository.findById(eq(itemId))).thenReturn(Optional.of(item));
        when(bookingRepository.existsByItemIdAndBookerIdAndEndBefore(
                eq(itemId), eq(userId), any(LocalDateTime.class)))
                .thenReturn(false);

        assertThrows(BadRequestException.class, () -> itemService.addComment(userId, itemId, commentDto));
    }

}
