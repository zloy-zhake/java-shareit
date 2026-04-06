package ru.practicum.shareit.item.dto;

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
    void serialize_WithRequestId_MapsToRequestIdJsonField() throws Exception {
        NewItemRequestDto dto = new NewItemRequestDto();
        dto.setName("Laptop");
        dto.setDescription("A laptop");
        dto.setAvailable(true);
        dto.setRequest(5);

        String jsonContent = this.json.write(dto).getJson();

        assertThat(jsonContent).contains("\"name\":\"Laptop\"");
        assertThat(jsonContent).contains("\"description\":\"A laptop\"");
        assertThat(jsonContent).contains("\"available\":true");
        assertThat(jsonContent).contains("\"requestId\":5");
    }

    @Test
    void deserialize_WithRequestId_ParsesCorrectly() throws Exception {
        String jsonContent = "{\"name\":\"Laptop\",\"description\":\"A laptop\",\"available\":true,\"requestId\":10}";

        NewItemRequestDto result = this.json.parseObject(jsonContent);

        assertThat(result.getName()).isEqualTo("Laptop");
        assertThat(result.getDescription()).isEqualTo("A laptop");
        assertThat(result.getAvailable()).isTrue();
        assertThat(result.getRequest()).isEqualTo(10);
    }

    @Test
    void deserialize_WithoutRequestId_SetsNull() throws Exception {
        String jsonContent = "{\"name\":\"Laptop\",\"description\":\"A laptop\",\"available\":true}";

        NewItemRequestDto result = this.json.parseObject(jsonContent);

        assertThat(result.getRequest()).isNull();
    }

    @Test
    void deserialize_WithNullAvailable_SetsNull() throws Exception {
        String jsonContent = "{\"name\":\"Laptop\",\"description\":\"A laptop\",\"available\":null}";

        NewItemRequestDto result = this.json.parseObject(jsonContent);

        assertThat(result.getAvailable()).isNull();
    }
}
