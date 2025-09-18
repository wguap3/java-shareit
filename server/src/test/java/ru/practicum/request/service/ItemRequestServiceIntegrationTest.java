package ru.practicum.request.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.exception.NotFoundException;
import ru.practicum.item.repository.ItemRepository;
import ru.practicum.request.dto.ItemRequestDto;
import ru.practicum.request.dto.ItemRequestResponseDto;
import ru.practicum.request.model.ItemRequest;
import ru.practicum.request.repository.ItemRequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
class ItemRequestServiceIntegrationTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User user1;
    private User user2;

    @BeforeEach
    void setup() {
        itemRepository.deleteAll();
        itemRequestRepository.deleteAll();
        userRepository.deleteAll();

        user1 = userRepository.save(new User(null, "Иван", "ivan@test.com"));
        user2 = userRepository.save(new User(null, "Мария", "maria@test.com"));
    }

    @Test
    void createRequest_shouldSaveAndReturnRequest() {
        ItemRequestDto requestDto = new ItemRequestDto("Нужна дрель");

        ItemRequestResponseDto saved = itemRequestService.createRequest(user1.getId(), requestDto);

        assertNotNull(saved.getId());
        assertEquals("Нужна дрель", saved.getDescription());

        ItemRequest fromDb = itemRequestRepository.findById(saved.getId()).orElseThrow();
        assertEquals(user1.getId(), fromDb.getRequester().getId());
    }

    @Test
    void getUserRequests_shouldReturnUserRequests() {
        ItemRequest request1 = new ItemRequest(null, LocalDateTime.now(), "Нужна дрель", user1, Collections.emptyList());
        ItemRequest request2 = new ItemRequest(null, LocalDateTime.now(), "Нужен молоток", user1, Collections.emptyList());
        itemRequestRepository.save(request1);
        itemRequestRepository.save(request2);

        List<ItemRequestResponseDto> requests = itemRequestService.getUserRequests(user1.getId());

        assertEquals(2, requests.size());
        List<String> descriptions = requests.stream().map(ItemRequestResponseDto::getDescription).toList();
        assertTrue(descriptions.contains("Нужна дрель"));
        assertTrue(descriptions.contains("Нужен молоток"));
    }

    @Test
    void getAllRequests_shouldReturnRequestsOfOtherUsers() {
        ItemRequest request1 = new ItemRequest(null, LocalDateTime.now(), "Нужна дрель", user1, Collections.emptyList());
        ItemRequest request2 = new ItemRequest(null, LocalDateTime.now(), "Нужен молоток", user2, Collections.emptyList());
        itemRequestRepository.save(request1);
        itemRequestRepository.save(request2);

        List<ItemRequestResponseDto> requests = itemRequestService.getAllRequests(user1.getId());

        assertEquals(1, requests.size());
        assertEquals("Нужен молоток", requests.get(0).getDescription());
    }

    @Test
    void getRequestById_shouldReturnRequest() {
        ItemRequest request = new ItemRequest(null, LocalDateTime.now(), "Нужна дрель", user1, Collections.emptyList());
        itemRequestRepository.save(request);

        ItemRequestResponseDto found = itemRequestService.getRequestById(request.getId(), user1.getId());

        assertEquals(request.getId(), found.getId());
        assertEquals("Нужна дрель", found.getDescription());
    }

    @Test
    void getRequestById_shouldThrowNotFoundException() {
        assertThrows(NotFoundException.class, () -> itemRequestService.getRequestById(999L, user1.getId()));
    }
}
