package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class NewUserRequestDtoJsonTest {

    @Autowired
    private JacksonTester<NewUserRequestDto> json;

    @Test
    void serialize_ValidData_FormatsCorrectly() throws Exception {
        NewUserRequestDto dto = new NewUserRequestDto();
        dto.setName("Test User");
        dto.setEmail("test@example.com");

        String jsonContent = this.json.write(dto).getJson();

        assertThat(jsonContent).contains("\"name\":\"Test User\"");
        assertThat(jsonContent).contains("\"email\":\"test@example.com\"");
    }

    @Test
    void deserialize_ValidJson_CreatesDto() throws Exception {
        String jsonContent = "{\"name\":\"John\",\"email\":\"john@example.com\"}";

        NewUserRequestDto result = this.json.parseObject(jsonContent);

        assertThat(result.getName()).isEqualTo("John");
        assertThat(result.getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void deserialize_WithNullFields_CreatesDtoWithNulls() throws Exception {
        String jsonContent = "{\"name\":null,\"email\":null}";

        NewUserRequestDto result = this.json.parseObject(jsonContent);

        assertThat(result.getName()).isNull();
        assertThat(result.getEmail()).isNull();
    }
}
