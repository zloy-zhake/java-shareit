package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.CommentNotPossibleException;
import ru.practicum.shareit.exceptions.DoesNotBelongToUserException;
import ru.practicum.shareit.exceptions.ItemNotValidException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
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

    private User testUser;
    private User otherUser;

    @BeforeEach
    void setUp() {
        commentRepository.deleteAll();
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();

        testUser = new User();
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        userRepository.save(testUser);

        otherUser = new User();
        otherUser.setName("Other User");
        otherUser.setEmail("other@example.com");
        userRepository.save(otherUser);
    }

    @Test
    void addItem_ValidData_CreatesItem() {
        NewItemRequestDto requestDto = new NewItemRequestDto();
        requestDto.setName("Test Item");
        requestDto.setDescription("Test Description");
        requestDto.setAvailable(true);

        ItemDto result = itemService.addItem(testUser.getId(), requestDto);

        assertNotNull(result);
        assertEquals("Test Item", result.getName());
        assertEquals("Test Description", result.getDescription());
        assertTrue(result.getAvailable());
        assertEquals(testUser.getId(), result.getOwner());
    }

    @Test
    void addItem_NullName_ThrowsException() {
        NewItemRequestDto requestDto = new NewItemRequestDto();
        requestDto.setName(null);
        requestDto.setDescription("Test Description");
        requestDto.setAvailable(true);

        assertThrows(ItemNotValidException.class, () -> itemService.addItem(testUser.getId(), requestDto));
    }

    @Test
    void addItem_NonExistingUser_ThrowsException() {
        NewItemRequestDto requestDto = new NewItemRequestDto();
        requestDto.setName("Test Item");
        requestDto.setDescription("Test Description");
        requestDto.setAvailable(true);

        assertThrows(NoSuchElementException.class, () -> itemService.addItem(99999, requestDto));
    }

    @Test
    void updateItem_ValidData_UpdatesItem() {
        Item item = new Item();
        item.setName("Original Name");
        item.setDescription("Original Description");
        item.setAvailable(true);
        item.setOwnerId(testUser.getId());
        itemRepository.save(item);

        UpdateItemRequestDto updateDto = new UpdateItemRequestDto();
        updateDto.setName("Updated Name");

        ItemDto result = itemService.updateItem(testUser.getId(), item.getId(), updateDto);

        assertNotNull(result);
        assertEquals("Updated Name", result.getName());
        assertEquals("Original Description", result.getDescription());
    }

    @Test
    void updateItem_ItemNotBelongToUser_ThrowsException() {
        Item item = new Item();
        item.setName("Other User Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwnerId(otherUser.getId());
        itemRepository.save(item);

        UpdateItemRequestDto updateDto = new UpdateItemRequestDto();
        updateDto.setName("Updated Name");

        assertThrows(DoesNotBelongToUserException.class,
                () -> itemService.updateItem(testUser.getId(), item.getId(), updateDto));
    }

    @Test
    void getItemById_ExistingItem_ReturnsItem() {
        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwnerId(testUser.getId());
        itemRepository.save(item);

        ItemDto result = itemService.getItemById(item.getId(), testUser.getId());

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        assertEquals("Test Item", result.getName());
    }

    @Test
    void getItemById_AsOwner_IncludesBookings() {
        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwnerId(testUser.getId());
        itemRepository.save(item);

        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().minusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setBooker(otherUser.getId());
        booking.setItem(item.getId());
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);

        ItemDto result = itemService.getItemById(item.getId(), testUser.getId());

        assertNotNull(result);
        assertNotNull(result.getLastBooking());
        assertEquals(otherUser.getId(), result.getLastBooking().getBookerId());
    }

    @Test
    void getAllItemsFromUser_ReturnsAllUserItems() {
        Item item1 = new Item();
        item1.setName("Item 1");
        item1.setDescription("Description 1");
        item1.setAvailable(true);
        item1.setOwnerId(testUser.getId());
        itemRepository.save(item1);

        Item item2 = new Item();
        item2.setName("Item 2");
        item2.setDescription("Description 2");
        item2.setAvailable(true);
        item2.setOwnerId(testUser.getId());
        itemRepository.save(item2);

        List<ItemDto> result = itemService.getAllItemsFromUser(testUser.getId());

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void searchAvailableItems_WithText_ReturnsMatchingItems() {
        Item item = new Item();
        item.setName("Laptop");
        item.setDescription("A powerful laptop");
        item.setAvailable(true);
        item.setOwnerId(testUser.getId());
        itemRepository.save(item);

        List<ItemDto> result = itemService.searchAvailableItems("laptop", testUser.getId());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Laptop", result.getFirst().getName());
    }

    @Test
    void searchAvailableItems_EmptyText_ReturnsEmptyList() {
        List<ItemDto> result = itemService.searchAvailableItems("", testUser.getId());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void addComment_ValidBooking_CreatesComment() {
        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwnerId(testUser.getId());
        itemRepository.save(item);

        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        booking.setBooker(otherUser.getId());
        booking.setItem(item.getId());
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);

        NewCommentRequestDto commentDto = new NewCommentRequestDto();
        commentDto.setText("Great item!");

        CommentDto result = itemService.addComment(otherUser.getId(), item.getId(), commentDto);

        assertNotNull(result);
        assertEquals("Great item!", result.getText());
        assertEquals(otherUser.getName(), result.getAuthorName());
    }

    @Test
    void addComment_NoBooking_ThrowsException() {
        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwnerId(testUser.getId());
        itemRepository.save(item);

        NewCommentRequestDto commentDto = new NewCommentRequestDto();
        commentDto.setText("Great item!");

        assertThrows(CommentNotPossibleException.class,
                () -> itemService.addComment(otherUser.getId(), item.getId(), commentDto));
    }
}
