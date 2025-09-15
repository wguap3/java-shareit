package ru.practicum.booking.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.booking.dto.BookingDto;
import ru.practicum.booking.dto.CreateBookingRequestDto;
import ru.practicum.booking.mapper.BookingMapper;
import ru.practicum.booking.model.Booking;
import ru.practicum.booking.model.BookingState;
import ru.practicum.booking.model.BookingStatus;
import ru.practicum.booking.repository.BookingRepository;
import ru.practicum.booking.strategy.booking.BookingStrategy;
import ru.practicum.booking.strategy.owner.OwnerBookingStrategy;
import ru.practicum.exception.*;
import ru.practicum.item.model.Item;
import ru.practicum.item.repository.ItemRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;
import ru.practicum.user.service.UserService;

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
    private final UserService userService;
    private final List<BookingStrategy> strategies;
    private final List<OwnerBookingStrategy> ownerStrategies;


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
        Booking booking = findByIdOrThrow(bookingId);

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
        Booking booking = findByIdOrThrow(bookingId);

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
        Booking booking = findByIdOrThrow(bookingId);

        if (!booking.getBooker().getId().equals(userId) &&
                !booking.getItem().getOwnerId().equals(userId)) {
            throw new BookingAccessDeniedException("Access to booking denied");
        }

        return bookingMapper.toDto(booking);
    }

    @Override
    public List<BookingDto> getAllByUser(Long userId, String stateParam) {
        userService.findByIdOrThrow(userId);

        BookingState state = BookingState.fromString(stateParam);
        LocalDateTime now = LocalDateTime.now();

        BookingStrategy strategy = strategies.stream()
                .filter(s -> s.supports(state))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported state: " + state));

        return strategy.findBookings(userId, now, state)
                .stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }


    @Override
    public List<BookingDto> getAllByOwner(Long ownerId, String stateParam) {
        userService.findByIdOrThrow(ownerId);

        BookingState state = BookingState.fromString(stateParam);
        LocalDateTime now = LocalDateTime.now();

        OwnerBookingStrategy strategy = ownerStrategies.stream()
                .filter(s -> s.supports(state))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported state: " + state));

        return strategy.findBookingsByOwner(ownerId, now, state)
                .stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Booking findByIdOrThrow(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking with id " + bookingId + " not found"));
    }
}