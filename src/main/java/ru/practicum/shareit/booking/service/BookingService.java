package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequestDto;

public interface BookingService {
    BookingDto addBooking(int bookerUserId, NewBookingRequestDto newBookingRequestDto);

    BookingDto approveBooking(int bookingId, int ownerUserId, boolean isApproved);

    BookingDto getBookingById(int bookingId, int sharerUserId);
}
