package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookItemRequestDtoJsonTest {

    @Autowired
    private JacksonTester<BookItemRequestDto> json;

    @Test
    void serialize_WithLocalDateTime_FormatsCorrectly() throws Exception {
        LocalDateTime start = LocalDateTime.of(2024, 6, 15, 10, 0);
        LocalDateTime end = LocalDateTime.of(2024, 6, 16, 10, 0);

        BookItemRequestDto dto = new BookItemRequestDto(1L, start, end);

        String jsonContent = this.json.write(dto).getJson();

        assertThat(jsonContent).contains("\"itemId\":1");
        assertThat(jsonContent).containsPattern("\"start\":\"\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\"");
        assertThat(jsonContent).containsPattern("\"end\":\"\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\"");
    }

    @Test
    void deserialize_WithLocalDateTime_ParsesCorrectly() throws Exception {
        String jsonContent = "{\"itemId\":5,\"start\":\"2024-06-15T10:00:00\",\"end\":\"2024-06-16T10:00:00\"}";

        BookItemRequestDto result = this.json.parseObject(jsonContent);

        assertThat(result.getItemId()).isEqualTo(5L);
        assertThat(result.getStart()).isEqualTo(LocalDateTime.of(2024, 6, 15, 10, 0));
        assertThat(result.getEnd()).isEqualTo(LocalDateTime.of(2024, 6, 16, 10, 0));
    }
}
