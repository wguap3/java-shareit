package ru.practicum.item.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.booking.model.Booking;
import ru.practicum.booking.repository.BookingRepository;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.UnauthorizedActionException;
import ru.practicum.item.dto.CommentDto;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.item.mapper.CommentMapper;
import ru.practicum.item.mapper.ItemMapper;
import ru.practicum.item.model.Comment;
import ru.practicum.item.model.Item;
import ru.practicum.item.repository.CommentRepository;
import ru.practicum.item.repository.ItemRepository;
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
public class ItemServiceImp implements ItemService {

    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;
    private final UserService userService;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    @Transactional
    public ItemDto addItem(ItemDto itemDto, Long ownerId) {
        User owner = userService.findByIdOrThrow(ownerId);
        Item item = itemMapper.toItem(itemDto, ownerId);
        if (itemDto.getRequestId() != null) {
            ItemRequest request = itemRequestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Request not found"));
            item.setRequest(request);
        }

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

