package ru.practicum.shareit.booking.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequestDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.BookingNotValidException;
import ru.practicum.shareit.exceptions.DoesNotBelongToUserException;
import ru.practicum.shareit.exceptions.ItemNotAvailableException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingDto addBooking(int bookerUserId, NewBookingRequestDto newBookingRequestDto) {
        log.info(
                "BookingServiceImpl:addBooking(): запрос на создание нового бронирования {} от пользователя с id={}",
                newBookingRequestDto,
                bookerUserId
        );
        checkIfUserExists(bookerUserId);
        validateNewBookingRequestDto(newBookingRequestDto);
        Booking newBooking = BookingMapper.newBookingRequestDtoToBooking(newBookingRequestDto);
        newBooking.setBooker(bookerUserId);
        newBooking.setStatus(BookingStatus.WAITING);
        Booking createdBooking = bookingRepository.save(newBooking);
        log.info("BookingServiceImpl:addBooking(): создано новое бронирование {}", createdBooking);
        User booker = userRepository.findById(bookerUserId).orElseThrow();
        Item item = itemRepository.findById(newBooking.getItem()).orElseThrow();
        return BookingMapper.bookingToBookingDto(createdBooking, booker, item);
    }

    @Override
    public BookingDto approveBooking(int bookingId, int ownerUserId, boolean isApproved) {
        log.info(
                "BookingServiceImpl:approveBooking(): запрос на подтверждение бронирования с ID={} от пользователя с ID={} approved={}",
                bookingId,
                ownerUserId,
                isApproved
        );
        Booking booking = bookingRepository.findById(bookingId).orElseThrow();
        int itemId = booking.getItem();
        if (!itemBelongsToUser(itemId, ownerUserId)) {
            throw new DoesNotBelongToUserException("Вещь с ID={" + itemId + "} не принадлежит пользователю с ID={" + ownerUserId + "}");
        }
        if (isApproved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        log.info(
                "BookingServiceImpl:approveBooking(): выполнен запрос на подтверждение бронирования с ID={} от пользователя с ID={} approved={}",
                bookingId,
                ownerUserId,
                isApproved
        );
        User booker = userRepository.findById(booking.getBooker()).orElseThrow();
        Item item = itemRepository.findById(booking.getItem()).orElseThrow();
        return BookingMapper.bookingToBookingDto(booking, booker, item);
    }

    @Override
    public BookingDto getBookingById(int bookingId, int sharerUserId) {
        log.info(
                "BookingServiceImpl:getBookingById(): запрос на получение бронирования с ID={} от пользователя с ID={}",
                bookingId,
                sharerUserId
        );
        Booking booking = bookingRepository.findById(bookingId).orElseThrow();
        Item item = itemRepository.findById(booking.getItem()).orElseThrow();
        if (sharerUserId != booking.getBooker() && sharerUserId != item.getOwnerId()) {
            throw new DoesNotBelongToUserException("Запрошенное бронирование с ID=" + bookingId + " никак не связано с пользователем с ID=" + sharerUserId);
        }
        User booker = userRepository.findById(booking.getBooker()).orElseThrow();
        return BookingMapper.bookingToBookingDto(booking, booker, item);
    }

    private void checkIfUserExists(int userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NoSuchElementException("Пользователя с ID " + userId + " не существует");
        }
    }

    private void checkIfItemExists(int itemId) {
        if (itemRepository.findById(itemId).isEmpty()) {
            throw new NoSuchElementException("Вещи с ID " + itemId + " не существует");
        }
    }

    private void checkIfItemAvailable(int itemId) {
        if (!itemRepository.findById(itemId).orElseThrow().isAvailable()) {
            throw new ItemNotAvailableException("Вещь с ID={" + itemId + "} недоступна");
        }
    }

    private void validateNewBookingRequestDto(NewBookingRequestDto newBookingRequestDto) {
        if (newBookingRequestDto.getStart().isBefore(LocalDateTime.now())) {
            throw new BookingNotValidException("Дата начала бронирования находится в прошлом.");
        }
        if (!newBookingRequestDto.getEnd().isAfter(newBookingRequestDto.getStart())) {
            throw new BookingNotValidException("Дата окончания бронирования находится раньше даты начала бронирования.");
        }
        if (newBookingRequestDto.getItemId() == null) {
            throw new BookingNotValidException("Вещь не может быть равна null");
        }
        checkIfItemExists(newBookingRequestDto.getItemId());
        checkIfItemAvailable(newBookingRequestDto.getItemId());
    }

    private boolean itemBelongsToUser(int itemId, int userId) {
        Item item = itemRepository.findById(itemId).orElseThrow();
        return item.getOwnerId() == userId;
    }

}
