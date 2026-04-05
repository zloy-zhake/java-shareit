package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BookingStateTest {

    @Test
    void from_AllLowerCase_ReturnsAll() {
        Optional<BookingState> result = BookingState.from("all");
        assertTrue(result.isPresent());
        assertEquals(BookingState.ALL, result.get());
    }

    @Test
    void from_AllUppercase_ReturnsAll() {
        Optional<BookingState> result = BookingState.from("ALL");
        assertTrue(result.isPresent());
        assertEquals(BookingState.ALL, result.get());
    }

    @Test
    void from_Current_ReturnsCurrent() {
        Optional<BookingState> result = BookingState.from("current");
        assertTrue(result.isPresent());
        assertEquals(BookingState.CURRENT, result.get());
    }

    @Test
    void from_Future_ReturnsFuture() {
        Optional<BookingState> result = BookingState.from("FUTURE");
        assertTrue(result.isPresent());
        assertEquals(BookingState.FUTURE, result.get());
    }

    @Test
    void from_Past_ReturnsPast() {
        Optional<BookingState> result = BookingState.from("past");
        assertTrue(result.isPresent());
        assertEquals(BookingState.PAST, result.get());
    }

    @Test
    void from_Waiting_ReturnsWaiting() {
        Optional<BookingState> result = BookingState.from("WAITING");
        assertTrue(result.isPresent());
        assertEquals(BookingState.WAITING, result.get());
    }

    @Test
    void from_Rejected_ReturnsRejected() {
        Optional<BookingState> result = BookingState.from("rejected");
        assertTrue(result.isPresent());
        assertEquals(BookingState.REJECTED, result.get());
    }

    @Test
    void from_MixedCase_ReturnsCorrectState() {
        Optional<BookingState> result = BookingState.from("WaItInG");
        assertTrue(result.isPresent());
        assertEquals(BookingState.WAITING, result.get());
    }

    @Test
    void from_InvalidState_ReturnsEmpty() {
        Optional<BookingState> result = BookingState.from("INVALID");
        assertTrue(result.isEmpty());
    }

    @Test
    void from_EmptyString_ReturnsEmpty() {
        Optional<BookingState> result = BookingState.from("");
        assertTrue(result.isEmpty());
    }

    @Test
    void from_NullString_ReturnsEmpty() {
        Optional<BookingState> result = BookingState.from(null);
        assertTrue(result.isEmpty());
    }
}
