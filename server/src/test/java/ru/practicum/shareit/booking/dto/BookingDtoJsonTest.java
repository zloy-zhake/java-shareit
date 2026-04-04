package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoJsonTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void serialize_WithNestedObjects_FormatsCorrectly() throws Exception {
        BookingDto dto = new BookingDto();
        dto.setId(1);
        dto.setStart(LocalDateTime.of(2024, 1, 15, 10, 0));
        dto.setEnd(LocalDateTime.of(2024, 1, 16, 10, 0));
        dto.setStatus(BookingStatus.APPROVED);

        BookingItemDto item = new BookingItemDto();
        item.setId(10);
        item.setName("Test Item");
        dto.setItem(item);

        BookerDto booker = new BookerDto();
        booker.setId(20);
        dto.setBooker(booker);

        String jsonContent = this.json.write(dto).getJson();

        assertThat(jsonContent).contains("\"id\":1");
        assertThat(jsonContent).containsPattern("\"start\":\"\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\"");
        assertThat(jsonContent).contains("\"status\":\"APPROVED\"");
        assertThat(jsonContent).contains("\"item\":{\"id\":10,\"name\":\"Test Item\"}");
        assertThat(jsonContent).contains("\"booker\":{\"id\":20}");
    }

    @Test
    void deserialize_WithNestedObjects_ParsesCorrectly() throws Exception {
        String jsonContent = "{"
                + "\"id\":1,"
                + "\"start\":\"2024-01-15T10:00:00\","
                + "\"end\":\"2024-01-16T10:00:00\","
                + "\"status\":\"APPROVED\","
                + "\"item\":{\"id\":10,\"name\":\"Test Item\"},"
                + "\"booker\":{\"id\":20}"
                + "}";

        BookingDto result = this.json.parseObject(jsonContent);

        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getStart()).isEqualTo(LocalDateTime.of(2024, 1, 15, 10, 0));
        assertThat(result.getEnd()).isEqualTo(LocalDateTime.of(2024, 1, 16, 10, 0));
        assertThat(result.getStatus()).isEqualTo(BookingStatus.APPROVED);
        assertThat(result.getItem()).isNotNull();
        assertThat(result.getItem().getId()).isEqualTo(10);
        assertThat(result.getItem().getName()).isEqualTo("Test Item");
        assertThat(result.getBooker()).isNotNull();
        assertThat(result.getBooker().getId()).isEqualTo(20);
    }
}
