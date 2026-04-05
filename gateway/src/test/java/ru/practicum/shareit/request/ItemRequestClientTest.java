package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.request.dto.NewItemRequestDto;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class ItemRequestClientTest {

    private static final String BASE = "http://localhost:9090/requests";

    private MockRestServiceServer mockServer;
    private ItemRequestClient itemRequestClient;

    @BeforeEach
    void setUp() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(BASE));
        mockServer = MockRestServiceServer.createServer(restTemplate);
        itemRequestClient = new ItemRequestClient(restTemplate);
    }

    @Test
    void addItemRequest_BuildsCorrectRequest() {
        NewItemRequestDto dto = new NewItemRequestDto();
        dto.setDescription("Need a laptop");

        mockServer.expect(method(HttpMethod.POST))
                .andExpect(requestTo(BASE))
                .andExpect(header("X-Sharer-User-Id", "1"))
                .andExpect(content().json("{\"description\":\"Need a laptop\"}"))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        itemRequestClient.addItemRequest(1L, dto);

        mockServer.verify();
    }

    @Test
    void getRequestsOfUser_BuildsCorrectRequest() {
        mockServer.expect(method(HttpMethod.GET))
                .andExpect(requestTo(BASE))
                .andExpect(header("X-Sharer-User-Id", "2"))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        itemRequestClient.getRequestsOfUser(2L);

        mockServer.verify();
    }

    @Test
    void getAllRequests_BuildsCorrectRequest() {
        mockServer.expect(method(HttpMethod.GET))
                .andExpect(requestTo(BASE + "/all"))
                .andExpect(header("X-Sharer-User-Id", "3"))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        itemRequestClient.getAllRequests(3L);

        mockServer.verify();
    }

    @Test
    void getRequestById_BuildsCorrectRequest() {
        mockServer.expect(method(HttpMethod.GET))
                .andExpect(requestTo(BASE + "/7"))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        itemRequestClient.getRequestById(7);

        mockServer.verify();
    }
}
