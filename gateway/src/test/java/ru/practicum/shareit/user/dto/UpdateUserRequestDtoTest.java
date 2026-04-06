package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UpdateUserRequestDtoTest {

    @Test
    void hasName_WithName_ReturnsTrue() {
        UpdateUserRequestDto dto = new UpdateUserRequestDto();
        dto.setName("Test");

        assertTrue(dto.hasName());
    }

    @Test
    void hasName_WithNull_ReturnsFalse() {
        UpdateUserRequestDto dto = new UpdateUserRequestDto();

        assertFalse(dto.hasName());
    }

    @Test
    void hasName_WithBlank_ReturnsFalse() {
        UpdateUserRequestDto dto = new UpdateUserRequestDto();
        dto.setName("   ");

        assertFalse(dto.hasName());
    }

    @Test
    void hasEmail_WithEmail_ReturnsTrue() {
        UpdateUserRequestDto dto = new UpdateUserRequestDto();
        dto.setEmail("test@test.com");

        assertTrue(dto.hasEmail());
    }

    @Test
    void hasEmail_WithNull_ReturnsFalse() {
        UpdateUserRequestDto dto = new UpdateUserRequestDto();

        assertFalse(dto.hasEmail());
    }

    @Test
    void hasEmail_WithBlank_ReturnsFalse() {
        UpdateUserRequestDto dto = new UpdateUserRequestDto();
        dto.setEmail("  ");

        assertFalse(dto.hasEmail());
    }
}
