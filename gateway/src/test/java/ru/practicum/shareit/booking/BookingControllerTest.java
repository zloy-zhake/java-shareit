package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    private static final String BOOKINGS_URL = "/bookings";
    private static final long USER_ID = 1L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingClient bookingClient;

    @Test
    void getBookings_DefaultState_ReturnsOk() throws Exception {
        mockMvc.perform(get(BOOKINGS_URL)
                        .header("X-Sharer-User-Id", USER_ID))
                .andExpect(status().isOk());
    }

    @Test
    void getBookings_AllState_ReturnsOk() throws Exception {
        mockMvc.perform(get(BOOKINGS_URL)
                        .header("X-Sharer-User-Id", USER_ID)
                        .param("state", "ALL"))
                .andExpect(status().isOk());
    }

    @Test
    void getBookings_CurrentState_ReturnsOk() throws Exception {
        mockMvc.perform(get(BOOKINGS_URL)
                        .header("X-Sharer-User-Id", USER_ID)
                        .param("state", "CURRENT"))
                .andExpect(status().isOk());
    }

    @Test
    void getBookings_PastState_ReturnsOk() throws Exception {
        mockMvc.perform(get(BOOKINGS_URL)
                        .header("X-Sharer-User-Id", USER_ID)
                        .param("state", "PAST"))
                .andExpect(status().isOk());
    }

    @Test
    void getBookings_FutureState_ReturnsOk() throws Exception {
        mockMvc.perform(get(BOOKINGS_URL)
                        .header("X-Sharer-User-Id", USER_ID)
                        .param("state", "FUTURE"))
                .andExpect(status().isOk());
    }

    @Test
    void getBookings_WaitingState_ReturnsOk() throws Exception {
        mockMvc.perform(get(BOOKINGS_URL)
                        .header("X-Sharer-User-Id", USER_ID)
                        .param("state", "WAITING"))
                .andExpect(status().isOk());
    }

    @Test
    void getBookings_RejectedState_ReturnsOk() throws Exception {
        mockMvc.perform(get(BOOKINGS_URL)
                        .header("X-Sharer-User-Id", USER_ID)
                        .param("state", "REJECTED"))
                .andExpect(status().isOk());
    }

    @Test
    void getBookingsOfOwner_DefaultState_ReturnsOk() throws Exception {
        mockMvc.perform(get(BOOKINGS_URL + "/owner")
                        .header("X-Sharer-User-Id", USER_ID))
                .andExpect(status().isOk());
    }

    @Test
    void getBookingsOfOwner_AllStates_ReturnsOk() throws Exception {
        String[] states = {"ALL", "CURRENT", "PAST", "FUTURE", "WAITING", "REJECTED"};
        for (String state : states) {
            mockMvc.perform(get(BOOKINGS_URL + "/owner")
                            .header("X-Sharer-User-Id", USER_ID)
                            .param("state", state))
                    .andExpect(status().isOk());
        }
    }

    @Test
    void bookItem_ValidRequest_ReturnsOk() throws Exception {
        BookItemRequestDto requestDto = new BookItemRequestDto(
                1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );

        mockMvc.perform(post(BOOKINGS_URL)
                        .header("X-Sharer-User-Id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());
    }

    @Test
    void bookItem_PastStart_ReturnsBadRequest() throws Exception {
        BookItemRequestDto requestDto = new BookItemRequestDto(
                1L,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(2)
        );

        mockMvc.perform(post(BOOKINGS_URL)
                        .header("X-Sharer-User-Id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void bookItem_PastEnd_ReturnsBadRequest() throws Exception {
        BookItemRequestDto requestDto = new BookItemRequestDto(
                1L,
                LocalDateTime.now().plusDays(5),
                LocalDateTime.now().minusDays(1)
        );

        mockMvc.perform(post(BOOKINGS_URL)
                        .header("X-Sharer-User-Id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void approveBooking_Approve_ReturnsOk() throws Exception {
        mockMvc.perform(patch(BOOKINGS_URL + "/{bookingId}", 1)
                        .header("X-Sharer-User-Id", USER_ID)
                        .param("approved", "true"))
                .andExpect(status().isOk());
    }

    @Test
    void approveBooking_Reject_ReturnsOk() throws Exception {
        mockMvc.perform(patch(BOOKINGS_URL + "/{bookingId}", 1)
                        .header("X-Sharer-User-Id", USER_ID)
                        .param("approved", "false"))
                .andExpect(status().isOk());
    }

    @Test
    void getBooking_ReturnsOk() throws Exception {
        mockMvc.perform(get(BOOKINGS_URL + "/{bookingId}", 1)
                        .header("X-Sharer-User-Id", USER_ID))
                .andExpect(status().isOk());
    }
}
