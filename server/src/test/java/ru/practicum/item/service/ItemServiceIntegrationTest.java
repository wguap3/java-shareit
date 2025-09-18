package ru.practicum.item.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.ShareItApp;
import ru.practicum.booking.model.Booking;
import ru.practicum.booking.model.BookingStatus;
import ru.practicum.booking.repository.BookingRepository;
import ru.practicum.exception.BadRequestException;
import ru.practicum.item.dto.CommentDto;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.item.model.Item;
import ru.practicum.item.repository.CommentRepository;
import ru.practicum.item.repository.ItemRepository;
import ru.practicum.request.repository.ItemRequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Transactional
class ItemServiceIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private Long userId;
    private Long itemId;

    @BeforeEach
    void setup() {
        commentRepository.deleteAll();
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        itemRequestRepository.deleteAll();
        userRepository.deleteAll();


        User user = new User(null, "Иван", "ivan@test.com");
        userId = userRepository.save(user).getId();


        Item item = new Item(null, "Дрель", "дрель", true, userId, null);
        itemId = itemRepository.save(item).getId();
    }

    @Test
    void addItem_shouldSaveAndReturnItem() {
        ItemDto newItem = new ItemDto(null, "Молоток", "для гвоздей", true, null, null, null, Collections.emptyList());
        ItemDto savedItem = itemService.addItem(newItem, userId);

        assertNotNull(savedItem.getId());
        assertEquals("Молоток", savedItem.getName());
        assertEquals("для гвоздей", savedItem.getDescription());
    }

    @Test
    void editingItem_shouldUpdateItem() {
        ItemDto updateDto = new ItemDto(null, "Дрель Pro", "дрель с кейсом", true, null, null, null, Collections.emptyList());
        ItemDto updated = itemService.editingItem(itemId, updateDto, userId);

        assertEquals(itemId, updated.getId());
        assertEquals("Дрель Pro", updated.getName());
        assertEquals("дрель с кейсом", updated.getDescription());
    }

    @Test
    void getItemById_shouldReturnItem() {
        ItemDto found = itemService.getItemById(itemId, userId);

        assertEquals("Дрель", found.getName());
        assertEquals("дрель", found.getDescription());
        assertTrue(found.getAvailable());
    }

    @Test
    void getAllByOwner_shouldReturnAllItems() {
        List<ItemDto> items = itemService.getAllByOwner(userId);

        assertEquals(1, items.size());
        assertEquals("Дрель", items.get(0).getName());
    }

    @Test
    void searchItems_shouldReturnMatchingItems() {
        List<ItemDto> results = itemService.searchItems("дрель");

        assertEquals(1, results.size());
        assertEquals("Дрель", results.get(0).getName());
    }

    @Test
    void addComment_shouldSaveComment_whenUserBookedItem() {
        Item item = itemRepository.findById(itemId).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();

        Booking booking = new Booking(
                null,
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1),
                item,
                user,
                BookingStatus.APPROVED
        );
        bookingRepository.save(booking);

        bookingRepository.save(booking);

        CommentDto commentDto = new CommentDto(null, "Отличный инструмент", null, null);
        CommentDto saved = itemService.addComment(userId, itemId, commentDto);

        assertNotNull(saved.getId());
        assertEquals("Отличный инструмент", saved.getText());
    }

    @Test
    void addComment_shouldThrow_whenUserNotBooked() {
        CommentDto commentDto = new CommentDto(null, "Комментарий", null, null);

        assertThrows(BadRequestException.class, () -> itemService.addComment(userId, itemId, commentDto));
    }
}

