package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.dto.BookerDto;
import ru.practicum.shareit.booking.dto.NewBookingRequestDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.BookingStatusRequestParam;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.BookingNotValidException;
import ru.practicum.shareit.exceptions.DoesNotBelongToUserException;
import ru.practicum.shareit.exceptions.ItemNotAvailableException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Test
    void addBooking_ValidRequest_ReturnsBooking() throws Exception {
        NewBookingRequestDto requestDto = new NewBookingRequestDto();
        requestDto.setStart(LocalDateTime.now().plusDays(1));
        requestDto.setEnd(LocalDateTime.now().plusDays(2));
        requestDto.setItemId(1);

        BookingDto expectedDto = createBookingDto(1, BookingStatus.WAITING);

        when(bookingService.addBooking(eq(1), any(NewBookingRequestDto.class))).thenReturn(expectedDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("WAITING"))
                .andExpect(jsonPath("$.booker.id").value(1));
    }

    @Test
    void addBooking_ItemNotAvailable_ReturnsBadRequest() throws Exception {
        NewBookingRequestDto requestDto = new NewBookingRequestDto();
        requestDto.setStart(LocalDateTime.now().plusDays(1));
        requestDto.setEnd(LocalDateTime.now().plusDays(2));
        requestDto.setItemId(1);

        when(bookingService.addBooking(eq(1), any(NewBookingRequestDto.class)))
                .thenThrow(new ItemNotAvailableException("Вещь с ID={1} недоступна"));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addBooking_StartInPast_ReturnsBadRequest() throws Exception {
        NewBookingRequestDto requestDto = new NewBookingRequestDto();
        requestDto.setStart(LocalDateTime.now().minusDays(1));
        requestDto.setEnd(LocalDateTime.now().plusDays(2));
        requestDto.setItemId(1);

        when(bookingService.addBooking(eq(1), any(NewBookingRequestDto.class)))
                .thenThrow(new BookingNotValidException("Дата начала бронирования находится в прошлом."));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void approveBooking_Approve_ReturnsApprovedBooking() throws Exception {
        BookingDto expectedDto = createBookingDto(1, BookingStatus.APPROVED);

        when(bookingService.approveBooking(1, 1, true)).thenReturn(expectedDto);

        mockMvc.perform(patch("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void approveBooking_NotOwner_ReturnsForbidden() throws Exception {
        when(bookingService.approveBooking(1, 1, true))
                .thenThrow(new DoesNotBelongToUserException("Вещь не принадлежит пользователю"));

        mockMvc.perform(patch("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void getBookingById_ExistingBooking_ReturnsBooking() throws Exception {
        BookingDto expectedDto = createBookingDto(1, BookingStatus.WAITING);

        when(bookingService.getBookingById(1, 1)).thenReturn(expectedDto);

        mockMvc.perform(get("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("WAITING"))
                .andExpect(jsonPath("$.booker.id").value(1));
    }

    @Test
    void getBookingById_NonExistingBooking_ReturnsNotFound() throws Exception {
        when(bookingService.getBookingById(999, 1))
                .thenThrow(new NoSuchElementException("Бронирования не существует"));

        mockMvc.perform(get("/bookings/{bookingId}", 999)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getBookingsOfOwner_AllStates_ReturnsBookings() throws Exception {
        BookingDto booking = createBookingDto(1, BookingStatus.WAITING);

        when(bookingService.getBookingsOfOwner(eq(1), eq(BookingStatusRequestParam.ALL)))
                .thenReturn(List.of(booking));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "ALL")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].status").value("WAITING"));
    }

    @Test
    void getBookingsOfBooker_AllStates_ReturnsBookings() throws Exception {
        BookingDto booking = createBookingDto(1, BookingStatus.WAITING);

        when(bookingService.getBookingsOfBooker(eq(1), eq(BookingStatusRequestParam.ALL)))
                .thenReturn(List.of(booking));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "ALL")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].status").value("WAITING"));
    }

    @Test
    void getBookingsOfOwner_InvalidState_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "INVALID_STATE")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    private BookingDto createBookingDto(int id, BookingStatus status) {
        BookingDto dto = new BookingDto();
        dto.setId(id);
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));
        dto.setStatus(status);

        BookingItemDto item = new BookingItemDto();
        item.setId(1);
        item.setName("Test Item");
        dto.setItem(item);

        BookerDto booker = new BookerDto();
        booker.setId(1);
        dto.setBooker(booker);

        return dto;
    }
}