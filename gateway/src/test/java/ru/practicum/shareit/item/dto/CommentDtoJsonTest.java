package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentDtoJsonTest {

    @Autowired
    private JacksonTester<CommentDto> json;

    @Test
    void serialize_WithAllFields_FormatsCorrectly() throws Exception {
        CommentDto dto = new CommentDto();
        dto.setId(1);
        dto.setText("Great item!");
        dto.setItem(5);
        dto.setAuthorName("John");
        dto.setCreated(LocalDateTime.of(2024, 3, 15, 14, 30));

        String jsonContent = this.json.write(dto).getJson();

        assertThat(jsonContent).contains("\"id\":1");
        assertThat(jsonContent).contains("\"text\":\"Great item!\"");
        assertThat(jsonContent).contains("\"item\":5");
        assertThat(jsonContent).contains("\"authorName\":\"John\"");
        assertThat(jsonContent).containsPattern("\"created\":\"\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\"");
    }

    @Test
    void deserialize_WithAllFields_ParsesCorrectly() throws Exception {
        String jsonContent = "{" + "\"id\":1," + "\"text\":\"Great item!\"," + "\"item\":5," + "\"authorName\":\"John\"," + "\"created\":\"2024-03-15T14:30:00\"" + "}";

        CommentDto result = this.json.parseObject(jsonContent);

        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getText()).isEqualTo("Great item!");
        assertThat(result.getItem()).isEqualTo(5);
        assertThat(result.getAuthorName()).isEqualTo("John");
        assertThat(result.getCreated()).isEqualTo(LocalDateTime.of(2024, 3, 15, 14, 30));
    }
}
