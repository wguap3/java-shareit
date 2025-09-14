package ru.practicum.shareit.item.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnauthorizedActionException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ItemServiceImp implements ItemService {

    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;
    private final UserService userService;

    @Override
    @Transactional
    public ItemDto addItem(ItemDto itemDto, Long ownerId) {
        User owner = userService.findByIdOrThrow(ownerId);
        Item item = itemMapper.toItem(itemDto, ownerId);
        Item saved = itemRepository.save(item);
        return itemMapper.toItemDto(saved);
    }

    @Override
    @Transactional
    public ItemDto editingItem(Long itemId, ItemDto itemDto, Long ownerId) {
        Item existingItem = findByIdOrThrow(itemId);
        if (!existingItem.getOwnerId().equals(ownerId)) {
            throw new UnauthorizedActionException("Редактировать может только владелец");
        }

        itemMapper.updateItemFromDto(itemDto, existingItem);
        Item updated = itemRepository.save(existingItem);
        return itemMapper.toItemDto(updated);
    }

    @Override
    public ItemDto getItemById(Long itemId, Long userId) {
        Item item = findByIdOrThrow(itemId);
        List<Booking> lastBookings = null;
        List<Booking> nextBookings = null;


        if (item.getOwnerId().equals(userId)) {
            LocalDateTime now = LocalDateTime.now();
            lastBookings = bookingRepository.findByItemIdAndEndBefore(
                    itemId, now, Sort.by(Sort.Direction.DESC, "start"));
            nextBookings = bookingRepository.findByItemIdAndStartAfter(
                    itemId, now, Sort.by(Sort.Direction.ASC, "start"));
        }

        List<CommentDto> comments = commentRepository.findByItemId(itemId).stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());

        return itemMapper.toDtoWithBookings(item, lastBookings, nextBookings, comments);
    }

    @Override
    public List<ItemDto> getAllByOwner(Long ownerId) {
        return itemRepository.findByOwnerId(ownerId).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.searchAvailableByText(text).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Item item = findByIdOrThrow(itemId);

        boolean hasBooked = bookingRepository.existsByItemIdAndBookerIdAndEndBefore(
                itemId, userId, LocalDateTime.now());

        if (!hasBooked) {
            throw new BadRequestException("User has not booked this item");
        }

        Comment comment = commentMapper.fromDto(commentDto, item, author);
        Comment savedComment = commentRepository.save(comment);
        return commentMapper.toDto(savedComment);
    }

    @Override
    public Item findByIdOrThrow(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with id " + itemId + " not found"));
    }
}

