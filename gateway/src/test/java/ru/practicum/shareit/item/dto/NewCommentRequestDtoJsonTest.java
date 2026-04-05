package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class NewCommentRequestDtoJsonTest {

    @Autowired
    private JacksonTester<NewCommentRequestDto> json;

    @Test
    void serialize_ValidText_FormatsCorrectly() throws Exception {
        NewCommentRequestDto dto = new NewCommentRequestDto();
        dto.setText("Great item!");

        String jsonContent = this.json.write(dto).getJson();

        assertThat(jsonContent).contains("\"text\":\"Great item!\"");
    }

    @Test
    void deserialize_ValidJson_CreatesDto() throws Exception {
        String jsonContent = "{\"text\":\"Nice product\"}";

        NewCommentRequestDto result = this.json.parseObject(jsonContent);

        assertThat(result.getText()).isEqualTo("Nice product");
    }
}
