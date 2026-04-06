package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import java.time.LocalDateTime;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class BookingClientTest {

    private MockRestServiceServer mockServer;
    private BookingClient bookingClient;

    @BeforeEach
    void setUp() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory("http://localhost:9090/bookings"));
        mockServer = MockRestServiceServer.createServer(restTemplate);
        bookingClient = new BookingClient(restTemplate);
    }

    @Test
    void getBookingsOfBooker_BuildsCorrectRequest() {
        mockServer.expect(method(HttpMethod.GET))
                .andExpect(requestTo("http://localhost:9090/bookings?state=ALL"))
                .andExpect(header("X-Sharer-User-Id", "42"))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        bookingClient.getBookingsOfBooker(42L, BookingState.ALL);

        mockServer.verify();
    }

    @Test
    void getBookingsOfBooker_WithCurrentState() {
        mockServer.expect(method(HttpMethod.GET))
                .andExpect(requestTo("http://localhost:9090/bookings?state=CURRENT"))
                .andExpect(header("X-Sharer-User-Id", "1"))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        bookingClient.getBookingsOfBooker(1L, BookingState.CURRENT);

        mockServer.verify();
    }

    @Test
    void getBookingsOfOwner_BuildsCorrectRequest() {
        mockServer.expect(method(HttpMethod.GET))
                .andExpect(requestTo("http://localhost:9090/bookings/owner?state=WAITING"))
                .andExpect(header("X-Sharer-User-Id", "7"))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        bookingClient.getBookingsOfOwner(7L, BookingState.WAITING);

        mockServer.verify();
    }

    @Test
    void getBookingsOfOwner_WithPastState() {
        mockServer.expect(method(HttpMethod.GET))
                .andExpect(requestTo("http://localhost:9090/bookings/owner?state=PAST"))
                .andExpect(header("X-Sharer-User-Id", "5"))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        bookingClient.getBookingsOfOwner(5L, BookingState.PAST);

        mockServer.verify();
    }

    @Test
    void bookItem_BuildsCorrectRequest() {
        BookItemRequestDto dto = new BookItemRequestDto(
                10L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );

        mockServer.expect(method(HttpMethod.POST))
                .andExpect(requestTo("http://localhost:9090/bookings"))
                .andExpect(header("X-Sharer-User-Id", "3"))
                .andExpect(content().json("{\"itemId\":10}"))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        bookingClient.bookItem(3L, dto);

        mockServer.verify();
    }

    @Test
    void approveBooking_Approved_BuildsCorrectRequest() {
        mockServer.expect(method(HttpMethod.PATCH))
                .andExpect(requestTo("http://localhost:9090/bookings/15?approved=true"))
                .andExpect(header("X-Sharer-User-Id", "1"))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        bookingClient.approveBooking(1L, 15L, true);

        mockServer.verify();
    }

    @Test
    void approveBooking_Rejected_BuildsCorrectRequest() {
        mockServer.expect(method(HttpMethod.PATCH))
                .andExpect(requestTo("http://localhost:9090/bookings/42?approved=false"))
                .andExpect(header("X-Sharer-User-Id", "2"))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        bookingClient.approveBooking(2L, 42L, false);

        mockServer.verify();
    }

    @Test
    void getBooking_BuildsCorrectRequest() {
        mockServer.expect(method(HttpMethod.GET))
                .andExpect(requestTo("http://localhost:9090/bookings/99"))
                .andExpect(header("X-Sharer-User-Id", "5"))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        bookingClient.getBooking(5L, 99L);

        mockServer.verify();
    }
}
