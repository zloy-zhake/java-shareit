package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class NewItemRequestDtoJsonTest {

    @Autowired
    private JacksonTester<NewItemRequestDto> json;

    @Test
    void serialize_ValidData_FormatsCorrectly() throws Exception {
        NewItemRequestDto dto = new NewItemRequestDto();
        dto.setDescription("Need a laptop");

        String jsonContent = this.json.write(dto).getJson();

        assertThat(jsonContent).contains("\"description\":\"Need a laptop\"");
    }

    @Test
    void deserialize_ValidJson_CreatesDto() throws Exception {
        String jsonContent = "{\"description\":\"Looking for a phone\"}";

        NewItemRequestDto result = this.json.parseObject(jsonContent);

        assertThat(result.getDescription()).isEqualTo("Looking for a phone");
    }

    @Test
    void deserialize_WithNullDescription_CreatesDtoWithNull() throws Exception {
        String jsonContent = "{\"description\":null}";

        NewItemRequestDto result = this.json.parseObject(jsonContent);

        assertThat(result.getDescription()).isNull();
    }
}
