package ru.practicum.shareit.booking.service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingRequestDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional
    public BookingDto create(Long userId, CreateBookingRequestDto requestDto) {
        if (requestDto.getStart() == null || requestDto.getEnd() == null) {
            throw new BadRequestException("Start and end dates must be provided");
        }
        if (requestDto.getStart().isAfter(requestDto.getEnd())) {
            throw new BadRequestException("End date must be after start date");
        }
        if (requestDto.getStart().isEqual(requestDto.getEnd())) {
            throw new BadRequestException("Start and end dates cannot be equal");
        }
        if (requestDto.getStart().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Start date cannot be in the past");
        }

        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Item item = itemRepository.findById(requestDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found"));

        if (!item.getAvailable()) {
            throw new BadRequestException("Item is not available");
        }
        if (item.getOwnerId().equals(userId)) {
            throw new BookingOwnItemException("Cannot book your own item");
        }

        boolean hasConflicts = bookingRepository.existsApprovedBookingsForItemBetweenDates(
                item.getId(),
                requestDto.getStart(),
                requestDto.getEnd()
        );

        if (hasConflicts) {
            throw new BadRequestException("Item is already booked for the selected dates");
        }

        Booking booking = new Booking();
        bookingMapper.fromCreateDto(requestDto, booking, item, booker);
        Booking savedBooking = bookingRepository.save(booking);

        return bookingMapper.toDto(savedBooking);
    }

    @Override
    @Transactional
    public BookingDto approve(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        if (!booking.getItem().getOwnerId().equals(userId)) {
            throw new ItemNotOwnedByUserException("User is not the owner");
        }
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new BookingAlreadyProcessedException("Booking already processed");
        }

        bookingMapper.updateStatus(approved, booking);
        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDto cancel(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        if (!booking.getBooker().getId().equals(userId)) {
            throw new BookingAccessDeniedException("Only booker can cancel booking");
        }

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new BookingAlreadyProcessedException("Only waiting bookings can be cancelled");
        }

        booking.setStatus(BookingStatus.CANCELED);
        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        if (!booking.getBooker().getId().equals(userId) &&
                !booking.getItem().getOwnerId().equals(userId)) {
            throw new BookingAccessDeniedException("Access to booking denied");
        }

        return bookingMapper.toDto(booking);
    }

    @Override
    public List<BookingDto> getAllByUser(Long userId, String state) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        List<Booking> bookings;
        LocalDateTime now = LocalDateTime.now();

        switch (BookingState.fromString(state)) {
            case CURRENT:
                bookings = bookingRepository.findByBookerIdAndStartBeforeAndEndAfter(
                        userId, now, now, Sort.by(Sort.Direction.DESC, "start"));
                break;
            case PAST:
                bookings = bookingRepository.findByBookerIdAndEndBefore(
                        userId, now, Sort.by(Sort.Direction.DESC, "start"));
                break;
            case FUTURE:
                bookings = bookingRepository.findByBookerIdAndStartAfter(
                        userId, now, Sort.by(Sort.Direction.DESC, "start"));
                break;
            case WAITING:
            case REJECTED:
                BookingStatus status = BookingStatus.valueOf(state);
                bookings = bookingRepository.findByBookerIdAndStatus(
                        userId, status, Sort.by(Sort.Direction.DESC, "start"));
                break;
            default:
                bookings = bookingRepository.findByBookerIdOrderByStartDesc(userId);
        }

        return bookings.stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getAllByOwner(Long userId, String state) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        List<Booking> bookings;
        LocalDateTime now = LocalDateTime.now();

        switch (BookingState.fromString(state)) {
            case CURRENT:
                bookings = bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfter(
                        userId, now, now, Sort.by(Sort.Direction.DESC, "start"));
                break;
            case PAST:
                bookings = bookingRepository.findByItemOwnerIdAndEndBefore(
                        userId, now, Sort.by(Sort.Direction.DESC, "start"));
                break;
            case FUTURE:
                bookings = bookingRepository.findByItemOwnerIdAndStartAfter(
                        userId, now, Sort.by(Sort.Direction.DESC, "start"));
                break;
            case WAITING:
            case REJECTED:
                BookingStatus status = BookingStatus.valueOf(state);
                bookings = bookingRepository.findByItemOwnerIdAndStatus(
                        userId, status, Sort.by(Sort.Direction.DESC, "start"));
                break;
            default:
                bookings = bookingRepository.findByItemOwnerIdOrderByStartDesc(userId);
        }

        return bookings.stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }
}