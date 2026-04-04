package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoJsonTest {

    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    void serialize_WithAllFields_FormatsCorrectly() throws Exception {
        ItemDto dto = new ItemDto();
        dto.setId(1);
        dto.setName("Test Item");
        dto.setDescription("Test Description");
        dto.setAvailable(true);
        dto.setOwner(100);
        dto.setRequest(5);

        BookingShortDto lastBooking = new BookingShortDto(
                10,
                LocalDateTime.of(2024, 1, 10, 10, 0),
                LocalDateTime.of(2024, 1, 11, 10, 0),
                20
        );
        dto.setLastBooking(lastBooking);

        BookingShortDto nextBooking = new BookingShortDto(
                11,
                LocalDateTime.of(2024, 1, 20, 10, 0),
                LocalDateTime.of(2024, 1, 21, 10, 0),
                30
        );
        dto.setNextBooking(nextBooking);

        CommentDto comment = new CommentDto();
        comment.setId(1);
        comment.setText("Great item!");
        comment.setAuthorName("John");
        comment.setCreated(LocalDateTime.of(2024, 1, 12, 15, 0));
        dto.setComments(List.of(comment));

        String jsonContent = this.json.write(dto).getJson();

        assertThat(jsonContent).contains("\"id\":1");
        assertThat(jsonContent).contains("\"name\":\"Test Item\"");
        assertThat(jsonContent).contains("\"description\":\"Test Description\"");
        assertThat(jsonContent).contains("\"available\":true");
        assertThat(jsonContent).contains("\"owner\":100");
        assertThat(jsonContent).contains("\"lastBooking\"");
        assertThat(jsonContent).contains("\"nextBooking\"");
        assertThat(jsonContent).contains("\"comments\"");
    }

    @Test
    void deserialize_WithAllFields_ParsesCorrectly() throws Exception {
        String jsonContent = "{"
                + "\"id\":1,"
                + "\"name\":\"Laptop\","
                + "\"description\":\"A powerful laptop\","
                + "\"available\":true,"
                + "\"owner\":100"
                + "}";

        ItemDto result = this.json.parseObject(jsonContent);

        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getName()).isEqualTo("Laptop");
        assertThat(result.getDescription()).isEqualTo("A powerful laptop");
        assertThat(result.getAvailable()).isTrue();
        assertThat(result.getOwner()).isEqualTo(100);
    }

    @Test
    void deserialize_WithNullBookings_ParsesCorrectly() throws Exception {
        String jsonContent = "{"
                + "\"id\":1,"
                + "\"name\":\"Laptop\","
                + "\"description\":\"A powerful laptop\","
                + "\"available\":true,"
                + "\"owner\":100,"
                + "\"lastBooking\":null,"
                + "\"nextBooking\":null,"
                + "\"comments\":[]"
                + "}";

        ItemDto result = this.json.parseObject(jsonContent);

        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getLastBooking()).isNull();
        assertThat(result.getNextBooking()).isNull();
        assertThat(result.getComments()).isEmpty();
    }
}
