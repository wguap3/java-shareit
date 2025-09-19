package ru.practicum.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.booking.dto.BookingDto;
import ru.practicum.booking.dto.CreateBookingRequestDto;
import ru.practicum.booking.mapper.BookingMapper;
import ru.practicum.booking.model.Booking;
import ru.practicum.booking.model.BookingStatus;
import ru.practicum.booking.repository.BookingRepository;
import ru.practicum.booking.strategy.booking.BookingStrategy;
import ru.practicum.booking.strategy.owner.OwnerBookingStrategy;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.BookingOwnItemException;
import ru.practicum.item.model.Item;
import ru.practicum.item.repository.ItemRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;
import ru.practicum.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private UserService userService;

    @Mock
    private List<BookingStrategy> strategies;

    @Mock
    private List<OwnerBookingStrategy> ownerStrategies;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private final Long userId = 1L;
    private final Long itemId = 2L;
    private final Long bookingId = 3L;

    private User user;
    private Item item;
    private Booking booking;
    private BookingDto bookingDto;
    private CreateBookingRequestDto requestDto;

    @BeforeEach
    void setup() {
        user = new User(userId, "Иван", "ivan@test.com");
        item = new Item(
                2L,
                "Дрель",
                "дрель",
                true,
                99L,
                null);
        booking = new Booking(
                bookingId,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                item,
                user,
                BookingStatus.WAITING
        );

        bookingDto = new BookingDto(
                bookingId,
                booking.getStart(),
                booking.getEnd(),
                BookingStatus.WAITING,
                null,
                null
        );

        requestDto = new CreateBookingRequestDto();
        requestDto.setItemId(item.getId());
        requestDto.setStart(LocalDateTime.now().plusDays(1));
        requestDto.setEnd(LocalDateTime.now().plusDays(2));
    }

    @Test
    void create_shouldReturnBooking() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.existsApprovedBookingsForItemBetweenDates(itemId, requestDto.getStart(), requestDto.getEnd()))
                .thenReturn(false);

        doAnswer(invocation -> {
            Booking b = invocation.getArgument(1);
            return null;
        }).when(bookingMapper).fromCreateDto(eq(requestDto), any(Booking.class), eq(item), eq(user));

        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(bookingMapper.toDto(booking)).thenReturn(bookingDto);

        BookingDto result = bookingService.create(userId, requestDto);

        assertEquals(bookingDto.getId(), result.getId());
        assertEquals(bookingDto.getStatus(), result.getStatus());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void create_shouldThrowException_whenItemNotAvailable() {
        Item unavailableItem = new Item(itemId, "Дрель", "дрель", false, 99L, null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(unavailableItem));

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> bookingService.create(userId, requestDto)
        );

        assertEquals("Item is not available", exception.getMessage());
    }

    @Test
    void create_shouldThrowException_whenBookingOwnItem() {
        Item ownItem = new Item(itemId, "Дрель", "дрель", true, userId, null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(ownItem));

        BookingOwnItemException exception = assertThrows(
                BookingOwnItemException.class,
                () -> bookingService.create(userId, requestDto)
        );

        assertEquals("Cannot book your own item", exception.getMessage());
    }

    @Test
    void approve_shouldChangeStatus() {
        Booking waitingBooking = new Booking(
                bookingId,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                item,
                user,
                BookingStatus.WAITING
        );

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(waitingBooking));
        when(bookingRepository.save(waitingBooking)).thenReturn(waitingBooking);
        when(bookingMapper.toDto(waitingBooking)).thenReturn(bookingDto);
        doNothing().when(bookingMapper).updateStatus(true, waitingBooking);

        BookingDto result = bookingService.approve(item.getOwnerId(), bookingId, true);

        assertEquals(bookingDto.getId(), result.getId());
        verify(bookingMapper).updateStatus(true, waitingBooking);
    }

    @Test
    void cancel_shouldSetStatusCanceled() {
        Booking waitingBooking = new Booking(
                bookingId,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                item,
                user,
                BookingStatus.WAITING
        );

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(waitingBooking));
        when(bookingRepository.save(waitingBooking)).thenReturn(waitingBooking);
        when(bookingMapper.toDto(waitingBooking)).thenReturn(bookingDto);

        BookingDto result = bookingService.cancel(userId, bookingId);

        assertEquals(BookingStatus.CANCELED, waitingBooking.getStatus()); // реально изменился
        assertEquals(bookingDto.getId(), result.getId());

    }

    @Test
    void getById_shouldReturnBooking() {
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingMapper.toDto(booking)).thenReturn(bookingDto);

        BookingDto result = bookingService.getById(userId, bookingId);

        assertEquals(bookingDto.getId(), result.getId());
        verify(bookingMapper).toDto(booking);
    }

}

