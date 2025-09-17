package ru.practicum.booking.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.booking.dto.BookingDto;
import ru.practicum.booking.dto.CreateBookingRequestDto;
import ru.practicum.booking.model.BookingStatus;
import ru.practicum.booking.repository.BookingRepository;
import ru.practicum.item.model.Item;
import ru.practicum.item.repository.ItemRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class BookingServiceIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private User booker;
    private User owner;
    private Item item;

    @BeforeEach
    void setUp() {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();

        owner = userRepository.save(new User(null, "Owner", "owner@test.com"));
        booker = userRepository.save(new User(null, "Booker", "booker@test.com"));

        item = itemRepository.save(new Item(null, "Дрель", "дрель", true, owner.getId(), null));
    }

    @Test
    void createBooking_shouldSaveBooking() {
        CreateBookingRequestDto requestDto = new CreateBookingRequestDto(
                item.getId(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );

        BookingDto result = bookingService.create(booker.getId(), requestDto);

        assertNotNull(result.getId());
        assertEquals(BookingStatus.WAITING, result.getStatus());
        assertEquals(booker.getId(), result.getBooker().getId());
        assertEquals(item.getId(), result.getItem().getId());
    }

    @Test
    void approveBooking_shouldChangeStatus() {
        CreateBookingRequestDto requestDto = new CreateBookingRequestDto(
                item.getId(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );
        BookingDto created = bookingService.create(booker.getId(), requestDto);

        BookingDto approved = bookingService.approve(owner.getId(), created.getId(), true);

        assertEquals(BookingStatus.APPROVED, approved.getStatus());
    }

    @Test
    void cancelBooking_shouldSetStatusCanceled() {
        CreateBookingRequestDto requestDto = new CreateBookingRequestDto(
                item.getId(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );
        BookingDto created = bookingService.create(booker.getId(), requestDto);

        BookingDto canceled = bookingService.cancel(booker.getId(), created.getId());

        assertEquals(BookingStatus.CANCELED, canceled.getStatus());
    }

    @Test
    void getById_shouldReturnBooking() {
        CreateBookingRequestDto requestDto = new CreateBookingRequestDto(
                item.getId(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );
        BookingDto created = bookingService.create(booker.getId(), requestDto);

        BookingDto found = bookingService.getById(booker.getId(), created.getId());

        assertEquals(created.getId(), found.getId());
    }

    @Test
    void getAllByUser_shouldReturnUserBookings() {
        CreateBookingRequestDto requestDto1 = new CreateBookingRequestDto(
                item.getId(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );
        bookingService.create(booker.getId(), requestDto1);

        List<BookingDto> bookings = bookingService.getAllByUser(booker.getId(), "ALL");

        assertEquals(1, bookings.size());
        assertEquals(booker.getId(), bookings.get(0).getBooker().getId());
    }

    @Test
    void getAllByOwner_shouldReturnOwnerBookings() {
        CreateBookingRequestDto requestDto1 = new CreateBookingRequestDto(
                item.getId(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );
        bookingService.create(booker.getId(), requestDto1);

        List<BookingDto> bookings = bookingService.getAllByOwner(owner.getId(), "ALL");

        assertEquals(1, bookings.size());
    }
}
