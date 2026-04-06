package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class NewBookingRequestDtoJsonTest {

    @Autowired
    private JacksonTester<NewBookingRequestDto> json;

    @Test
    void serialize_WithLocalDateTime_FormatsCorrectly() throws Exception {
        LocalDateTime start = LocalDateTime.of(2024, 1, 15, 10, 0);
        LocalDateTime end = LocalDateTime.of(2024, 1, 16, 10, 0);

        NewBookingRequestDto dto = new NewBookingRequestDto();
        dto.setStart(start);
        dto.setEnd(end);
        dto.setItemId(5);

        String jsonContent = this.json.write(dto).getJson();

        assertThat(jsonContent).contains("\"itemId\":5");
        assertThat(jsonContent).containsPattern("\"start\":\"\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\"");
        assertThat(jsonContent).containsPattern("\"end\":\"\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\"");
    }

    @Test
    void deserialize_WithLocalDateTime_ParsesCorrectly() throws Exception {
        String jsonContent = "{\"start\":\"2024-01-15T10:00:00\",\"end\":\"2024-01-16T10:00:00\",\"itemId\":5}";

        NewBookingRequestDto result = this.json.parseObject(jsonContent);

        assertThat(result.getStart()).isEqualTo(LocalDateTime.of(2024, 1, 15, 10, 0));
        assertThat(result.getEnd()).isEqualTo(LocalDateTime.of(2024, 1, 16, 10, 0));
        assertThat(result.getItemId()).isEqualTo(5);
    }
}
