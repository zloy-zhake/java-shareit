package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.item.dto.NewCommentRequestDto;
import ru.practicum.shareit.item.dto.NewItemRequestDto;
import ru.practicum.shareit.item.dto.UpdateItemRequestDto;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class ItemClientTest {

    private MockRestServiceServer mockServer;
    private ItemClient itemClient;

    @BeforeEach
    void setUp() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory("http://localhost:9090/items"));
        mockServer = MockRestServiceServer.createServer(restTemplate);
        itemClient = new ItemClient(restTemplate);
    }

    @Test
    void addItem_BuildsCorrectRequest() {
        NewItemRequestDto dto = new NewItemRequestDto();
        dto.setName("Laptop");
        dto.setDescription("A laptop");
        dto.setAvailable(true);

        mockServer.expect(method(HttpMethod.POST))
                .andExpect(requestTo("http://localhost:9090/items"))
                .andExpect(header("X-Sharer-User-Id", "1"))
                .andExpect(content().json("{\"name\":\"Laptop\",\"description\":\"A laptop\",\"available\":true}"))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        itemClient.addItem(1L, dto);

        mockServer.verify();
    }

    @Test
    void updateItem_BuildsCorrectRequest() {
        UpdateItemRequestDto dto = new UpdateItemRequestDto();
        dto.setName("Updated");

        mockServer.expect(method(HttpMethod.PATCH))
                .andExpect(requestTo("http://localhost:9090/items/5"))
                .andExpect(header("X-Sharer-User-Id", "2"))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        itemClient.updateItem(2L, 5, dto);

        mockServer.verify();
    }

    @Test
    void getItemById_BuildsCorrectRequest() {
        mockServer.expect(method(HttpMethod.GET))
                .andExpect(requestTo("http://localhost:9090/items/10"))
                .andExpect(header("X-Sharer-User-Id", "3"))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        itemClient.getItemById(3L, 10);

        mockServer.verify();
    }

    @Test
    void getAllItemsFromUser_BuildsCorrectRequest() {
        mockServer.expect(method(HttpMethod.GET))
                .andExpect(requestTo("http://localhost:9090/items"))
                .andExpect(header("X-Sharer-User-Id", "4"))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        itemClient.getAllItemsFromUser(4L);

        mockServer.verify();
    }

    @Test
    void searchAvailableItems_BuildsCorrectRequest() {
        mockServer.expect(method(HttpMethod.GET))
                .andExpect(requestTo("http://localhost:9090/items/search?text=laptop"))
                .andExpect(header("X-Sharer-User-Id", "5"))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        itemClient.searchAvailableItems(5L, "laptop");

        mockServer.verify();
    }

    @Test
    void searchAvailableItems_WithSpecialChars_BuildsCorrectRequest() {
        mockServer.expect(method(HttpMethod.GET))
                .andExpect(requestTo("http://localhost:9090/items/search?text=hello%20world"))
                .andExpect(header("X-Sharer-User-Id", "6"))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        itemClient.searchAvailableItems(6L, "hello world");

        mockServer.verify();
    }

    @Test
    void addComment_BuildsCorrectRequest() {
        NewCommentRequestDto dto = new NewCommentRequestDto();
        dto.setText("Great!");

        mockServer.expect(method(HttpMethod.POST))
                .andExpect(requestTo("http://localhost:9090/items/7/comment"))
                .andExpect(header("X-Sharer-User-Id", "8"))
                .andExpect(content().json("{\"text\":\"Great!\"}"))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        itemClient.addComment(8L, 7, dto);

        mockServer.verify();
    }
}
