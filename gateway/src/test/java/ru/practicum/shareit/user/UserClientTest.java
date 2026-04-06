package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.user.dto.NewUserRequestDto;
import ru.practicum.shareit.user.dto.UpdateUserRequestDto;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class UserClientTest {

    private static final String BASE = "http://localhost:9090/users";

    private MockRestServiceServer mockServer;
    private UserClient userClient;

    @BeforeEach
    void setUp() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(BASE));
        mockServer = MockRestServiceServer.createServer(restTemplate);
        userClient = new UserClient(restTemplate);
    }

    @Test
    void addUser_BuildsCorrectRequest() {
        NewUserRequestDto dto = new NewUserRequestDto();
        dto.setName("Test User");
        dto.setEmail("test@example.com");

        mockServer.expect(method(HttpMethod.POST))
                .andExpect(requestTo(BASE))
                .andExpect(content().json("{\"name\":\"Test User\",\"email\":\"test@example.com\"}"))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        userClient.addUser(dto);

        mockServer.verify();
    }

    @Test
    void getUserById_BuildsCorrectRequest() {
        mockServer.expect(method(HttpMethod.GET))
                .andExpect(requestTo(BASE + "/1"))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        userClient.getUserById(1);

        mockServer.verify();
    }

    @Test
    void updateUser_BuildsCorrectRequest() {
        UpdateUserRequestDto dto = new UpdateUserRequestDto();
        dto.setName("Updated");

        mockServer.expect(method(HttpMethod.PATCH))
                .andExpect(requestTo(BASE + "/5"))
                .andExpect(content().json("{\"name\":\"Updated\"}"))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        userClient.updateUser(5, dto);

        mockServer.verify();
    }

    @Test
    void deleteUser_BuildsCorrectRequest() {
        mockServer.expect(method(HttpMethod.DELETE))
                .andExpect(requestTo(BASE + "/10"))
                .andRespond(withSuccess("", MediaType.APPLICATION_JSON));

        userClient.deleteUser(10);

        mockServer.verify();
    }
}
