package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequestDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.BookingStatusRequestParam;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.BookingNotValidException;
import ru.practicum.shareit.exceptions.DoesNotBelongToUserException;
import ru.practicum.shareit.exceptions.ItemNotAvailableException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class BookingServiceIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private User owner;
    private User booker;
    private Item availableItem;

    @BeforeEach
    void setUp() {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();

        owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@example.com");
        userRepository.save(owner);

        booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker@example.com");
        userRepository.save(booker);

        availableItem = new Item();
        availableItem.setName("Test Item");
        availableItem.setDescription("Test Description");
        availableItem.setAvailable(true);
        availableItem.setOwnerId(owner.getId());
        itemRepository.save(availableItem);
    }

    @Test
    void addBooking_ValidData_CreatesBooking() {
        NewBookingRequestDto requestDto = new NewBookingRequestDto();
        requestDto.setStart(LocalDateTime.now().plusDays(1));
        requestDto.setEnd(LocalDateTime.now().plusDays(2));
        requestDto.setItemId(availableItem.getId());

        BookingDto result = bookingService.addBooking(booker.getId(), requestDto);

        assertNotNull(result);
        assertEquals(BookingStatus.WAITING, result.getStatus());
        assertEquals(booker.getId(), result.getBooker().getId());
        assertEquals(availableItem.getId(), result.getItem().getId());
    }

    @Test
    void addBooking_ItemNotAvailable_ThrowsException() {
        availableItem.setAvailable(false);
        itemRepository.save(availableItem);

        NewBookingRequestDto requestDto = new NewBookingRequestDto();
        requestDto.setStart(LocalDateTime.now().plusDays(1));
        requestDto.setEnd(LocalDateTime.now().plusDays(2));
        requestDto.setItemId(availableItem.getId());

        assertThrows(ItemNotAvailableException.class, () -> bookingService.addBooking(booker.getId(), requestDto));
    }

    @Test
    void addBooking_StartInPast_ThrowsException() {
        NewBookingRequestDto requestDto = new NewBookingRequestDto();
        requestDto.setStart(LocalDateTime.now().minusDays(1));
        requestDto.setEnd(LocalDateTime.now().plusDays(2));
        requestDto.setItemId(availableItem.getId());

        assertThrows(BookingNotValidException.class, () -> bookingService.addBooking(booker.getId(), requestDto));
    }

    @Test
    void approveBooking_Approve_SetsApprovedStatus() {
        NewBookingRequestDto requestDto = new NewBookingRequestDto();
        requestDto.setStart(LocalDateTime.now().plusDays(1));
        requestDto.setEnd(LocalDateTime.now().plusDays(2));
        requestDto.setItemId(availableItem.getId());

        BookingDto booking = bookingService.addBooking(booker.getId(), requestDto);

        BookingDto result = bookingService.approveBooking(booking.getId(), owner.getId(), true);

        assertNotNull(result);
        assertEquals(BookingStatus.APPROVED, result.getStatus());
    }

    @Test
    void approveBooking_Reject_SetsRejectedStatus() {
        NewBookingRequestDto requestDto = new NewBookingRequestDto();
        requestDto.setStart(LocalDateTime.now().plusDays(1));
        requestDto.setEnd(LocalDateTime.now().plusDays(2));
        requestDto.setItemId(availableItem.getId());

        BookingDto booking = bookingService.addBooking(booker.getId(), requestDto);

        BookingDto result = bookingService.approveBooking(booking.getId(), owner.getId(), false);

        assertNotNull(result);
        assertEquals(BookingStatus.REJECTED, result.getStatus());
    }

    @Test
    void approveBooking_NotOwner_ThrowsException() {
        NewBookingRequestDto requestDto = new NewBookingRequestDto();
        requestDto.setStart(LocalDateTime.now().plusDays(1));
        requestDto.setEnd(LocalDateTime.now().plusDays(2));
        requestDto.setItemId(availableItem.getId());

        BookingDto booking = bookingService.addBooking(booker.getId(), requestDto);

        assertThrows(DoesNotBelongToUserException.class, () -> bookingService.approveBooking(booking.getId(), booker.getId(), true));
    }

    @Test
    void getBookingById_AsBooker_ReturnsBooking() {
        NewBookingRequestDto requestDto = new NewBookingRequestDto();
        requestDto.setStart(LocalDateTime.now().plusDays(1));
        requestDto.setEnd(LocalDateTime.now().plusDays(2));
        requestDto.setItemId(availableItem.getId());

        BookingDto booking = bookingService.addBooking(booker.getId(), requestDto);

        BookingDto result = bookingService.getBookingById(booking.getId(), booker.getId());

        assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
    }

    @Test
    void getBookingById_AsOwner_ReturnsBooking() {
        NewBookingRequestDto requestDto = new NewBookingRequestDto();
        requestDto.setStart(LocalDateTime.now().plusDays(1));
        requestDto.setEnd(LocalDateTime.now().plusDays(2));
        requestDto.setItemId(availableItem.getId());

        BookingDto booking = bookingService.addBooking(booker.getId(), requestDto);

        BookingDto result = bookingService.getBookingById(booking.getId(), owner.getId());

        assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
    }

    @Test
    void getBookingById_UnrelatedUser_ThrowsException() {
        User unrelatedUser = new User();
        unrelatedUser.setName("Unrelated");
        unrelatedUser.setEmail("unrelated@example.com");
        userRepository.save(unrelatedUser);

        NewBookingRequestDto requestDto = new NewBookingRequestDto();
        requestDto.setStart(LocalDateTime.now().plusDays(1));
        requestDto.setEnd(LocalDateTime.now().plusDays(2));
        requestDto.setItemId(availableItem.getId());

        BookingDto booking = bookingService.addBooking(booker.getId(), requestDto);

        assertThrows(DoesNotBelongToUserException.class, () -> bookingService.getBookingById(booking.getId(), unrelatedUser.getId()));
    }

    @Test
    void getBookingsOfOwner_AllStates_ReturnsBookings() {
        NewBookingRequestDto requestDto = new NewBookingRequestDto();
        requestDto.setStart(LocalDateTime.now().plusDays(1));
        requestDto.setEnd(LocalDateTime.now().plusDays(2));
        requestDto.setItemId(availableItem.getId());

        bookingService.addBooking(booker.getId(), requestDto);

        List<BookingDto> result = bookingService.getBookingsOfOwner(owner.getId(), BookingStatusRequestParam.ALL);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void getBookingsOfBooker_AllStates_ReturnsBookings() {
        NewBookingRequestDto requestDto = new NewBookingRequestDto();
        requestDto.setStart(LocalDateTime.now().plusDays(1));
        requestDto.setEnd(LocalDateTime.now().plusDays(2));
        requestDto.setItemId(availableItem.getId());

        bookingService.addBooking(booker.getId(), requestDto);

        List<BookingDto> result = bookingService.getBookingsOfBooker(booker.getId(), BookingStatusRequestParam.ALL);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }
}
