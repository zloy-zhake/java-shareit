package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UpdateItemRequestDtoTest {

    @Test
    void hasName_WithName_ReturnsTrue() {
        UpdateItemRequestDto dto = new UpdateItemRequestDto();
        dto.setName("Updated");

        assertTrue(dto.hasName());
    }

    @Test
    void hasName_WithNull_ReturnsFalse() {
        UpdateItemRequestDto dto = new UpdateItemRequestDto();

        assertFalse(dto.hasName());
    }

    @Test
    void hasName_WithBlank_ReturnsFalse() {
        UpdateItemRequestDto dto = new UpdateItemRequestDto();
        dto.setName("  ");

        assertFalse(dto.hasName());
    }

    @Test
    void hasDescription_WithDescription_ReturnsTrue() {
        UpdateItemRequestDto dto = new UpdateItemRequestDto();
        dto.setDescription("Updated desc");

        assertTrue(dto.hasDescription());
    }

    @Test
    void hasDescription_WithNull_ReturnsFalse() {
        UpdateItemRequestDto dto = new UpdateItemRequestDto();

        assertFalse(dto.hasDescription());
    }

    @Test
    void hasDescription_WithBlank_ReturnsFalse() {
        UpdateItemRequestDto dto = new UpdateItemRequestDto();
        dto.setDescription("");

        assertFalse(dto.hasDescription());
    }

    @Test
    void hasAvailable_WithTrue_ReturnsTrue() {
        UpdateItemRequestDto dto = new UpdateItemRequestDto();
        dto.setAvailable(true);

        assertTrue(dto.hasAvailable());
    }

    @Test
    void hasAvailable_WithFalse_ReturnsTrue() {
        UpdateItemRequestDto dto = new UpdateItemRequestDto();
        dto.setAvailable(false);

        assertTrue(dto.hasAvailable());
    }

    @Test
    void hasAvailable_WithNull_ReturnsFalse() {
        UpdateItemRequestDto dto = new UpdateItemRequestDto();

        assertFalse(dto.hasAvailable());
    }
}
