package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class ItemRequestServiceIntegrationTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User requester;
    private User otherUser;

    @BeforeEach
    void setUp() {
        itemRepository.deleteAll();
        itemRequestRepository.deleteAll();
        userRepository.deleteAll();

        requester = new User();
        requester.setName("Requester");
        requester.setEmail("requester@example.com");
        userRepository.save(requester);

        otherUser = new User();
        otherUser.setName("Other User");
        otherUser.setEmail("other@example.com");
        userRepository.save(otherUser);
    }

    @Test
    void addItemRequest_ValidData_CreatesRequest() {
        NewItemRequestDto requestDto = new NewItemRequestDto();
        requestDto.setDescription("Need a laptop");

        ItemRequestDto result = itemRequestService.addItemRequest(requester.getId(), requestDto);

        assertNotNull(result);
        assertEquals("Need a laptop", result.getDescription());
        assertEquals(requester.getId(), result.getRequesterId());
        assertNotNull(result.getCreated());
    }

    @Test
    void getRequestsOfUser_WithRequests_ReturnsRequests() {
        NewItemRequestDto requestDto = new NewItemRequestDto();
        requestDto.setDescription("Need a laptop");
        itemRequestService.addItemRequest(requester.getId(), requestDto);

        NewItemRequestDto requestDto2 = new NewItemRequestDto();
        requestDto2.setDescription("Need a phone");
        itemRequestService.addItemRequest(requester.getId(), requestDto2);

        List<ItemRequestDto> result = itemRequestService.getRequestsOfUser(requester.getId());

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void getRequestsOfUser_NoRequests_ReturnsEmptyList() {
        List<ItemRequestDto> result = itemRequestService.getRequestsOfUser(requester.getId());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllRequests_ExcludesOwnRequests() {
        ItemRequest ownRequest = new ItemRequest();
        ownRequest.setDescription("Own request");
        ownRequest.setRequesterId(requester.getId());
        itemRequestRepository.save(ownRequest);

        ItemRequest otherRequest = new ItemRequest();
        otherRequest.setDescription("Other request");
        otherRequest.setRequesterId(otherUser.getId());
        itemRequestRepository.save(otherRequest);

        List<ItemRequestDto> result = itemRequestService.getAllRequests(requester.getId());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertNotEquals(requester.getId(), result.getFirst().getRequesterId());
    }

    @Test
    void getRequestById_ExistingRequest_ReturnsRequestWithItems() {
        NewItemRequestDto requestDto = new NewItemRequestDto();
        requestDto.setDescription("Need a laptop");
        ItemRequestDto createdRequest = itemRequestService.addItemRequest(requester.getId(), requestDto);

        Item item = new Item();
        item.setName("Laptop");
        item.setDescription("A laptop");
        item.setAvailable(true);
        item.setOwnerId(otherUser.getId());
        item.setRequestId(createdRequest.getId());
        itemRepository.save(item);

        ItemRequestDto result = itemRequestService.getRequestById(createdRequest.getId());

        assertNotNull(result);
        assertEquals(createdRequest.getId(), result.getId());
        assertEquals("Need a laptop", result.getDescription());
        assertNotNull(result.getItems());
        assertEquals(1, result.getItems().size());
        assertEquals("Laptop", result.getItems().getFirst().getName());
    }

    @Test
    void getRequestById_NonExistingRequest_ThrowsException() {
        assertThrows(NoSuchElementException.class, () -> itemRequestService.getRequestById(99999));
    }
}
