package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequestDto;
import ru.practicum.shareit.booking.model.BookingStatusRequestParam;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto addBooking(@RequestHeader("X-Sharer-User-Id") int bookerUserId, @RequestBody NewBookingRequestDto newBookingRequestDto) {
        log.info("Server BookingController:addBooking(): запрос на создание нового бронирования {} от пользователя с id={}", newBookingRequestDto, bookerUserId);
        return bookingService.addBooking(bookerUserId, newBookingRequestDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@RequestHeader("X-Sharer-User-Id") int ownerUserId, @PathVariable int bookingId, @RequestParam boolean approved) {
        log.info("Server BookingController:approveBooking(): запрос на подтверждение бронирования с ID={} от пользователя с ID={} approved={}", bookingId, ownerUserId, approved);
        return bookingService.approveBooking(bookingId, ownerUserId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@RequestHeader("X-Sharer-User-Id") int sharerUserId, @PathVariable int bookingId) {
        log.info("Server BookingController:getBookingById(): запрос на получение бронирования с ID={} от пользователя с ID={}", bookingId, sharerUserId);
        return bookingService.getBookingById(bookingId, sharerUserId);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsOfOwner(@RequestHeader("X-Sharer-User-Id") int ownerId, @RequestParam(value = "state", defaultValue = "ALL") String state) {
        log.info("Server BookingController:getBookingsOfOwner(): запрос на получение бронирований вещей пользователя с ID={}, state={}", ownerId, state);
        BookingStatusRequestParam stateParam = BookingStatusRequestParam.valueOf(state.toUpperCase());
        return bookingService.getBookingsOfOwner(ownerId, stateParam);
    }

    @GetMapping
    public List<BookingDto> getBookingsOfBooker(@RequestHeader("X-Sharer-User-Id") int userId, @RequestParam(value = "state", defaultValue = "ALL") String state) {
        log.info("Server BookingController:getBookingsOfBooker(): запрос на получение бронирований пользователя с ID={}, state={}", userId, state);
        BookingStatusRequestParam stateParam = BookingStatusRequestParam.valueOf(state.toUpperCase());
        return bookingService.getBookingsOfBooker(userId, stateParam);
    }
}
