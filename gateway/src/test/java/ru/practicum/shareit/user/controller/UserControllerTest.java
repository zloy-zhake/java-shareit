package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.UserClient;
import ru.practicum.shareit.user.dto.NewUserRequestDto;
import ru.practicum.shareit.user.dto.UpdateUserRequestDto;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    private static final String USER_URL = "/users";
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserClient userClient;

    @Test
    void addUser_ValidRequest_ReturnsCreated() throws Exception {
        NewUserRequestDto requestDto = new NewUserRequestDto();
        requestDto.setName("Test User");
        requestDto.setEmail("test@example.com");

        mockMvc.perform(post(USER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());
    }

    @Test
    void addUser_InvalidName_ReturnsBadRequest() throws Exception {
        NewUserRequestDto requestDto = new NewUserRequestDto();
        requestDto.setName("");
        requestDto.setEmail("test@example.com");

        mockMvc.perform(post(USER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addUser_InvalidEmail_ReturnsBadRequest() throws Exception {
        NewUserRequestDto requestDto = new NewUserRequestDto();
        requestDto.setName("Test User");
        requestDto.setEmail("not-an-email");

        mockMvc.perform(post(USER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addUser_NullName_ReturnsBadRequest() throws Exception {
        String body = "{\"name\":null,\"email\":\"test@example.com\"}";

        mockMvc.perform(post(USER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addUser_BlankEmail_ReturnsBadRequest() throws Exception {
        String body = "{\"name\":\"Test\",\"email\":\"\"}";

        mockMvc.perform(post(USER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUserById_ReturnsOk() throws Exception {
        mockMvc.perform(get(USER_URL + "/{userId}", 1))
                .andExpect(status().isOk());
    }

    @Test
    void updateUser_ValidRequest_ReturnsOk() throws Exception {
        UpdateUserRequestDto requestDto = new UpdateUserRequestDto();
        requestDto.setName("Updated Name");

        mockMvc.perform(patch(USER_URL + "/{userId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());
    }

    @Test
    void updateUser_WithAnyEmail_ReturnsOk() throws Exception {
        // Gateway doesn't validate email format on update — only NewUserRequestDto has @Email
        UpdateUserRequestDto requestDto = new UpdateUserRequestDto();
        requestDto.setName("Updated");
        requestDto.setEmail("invalid-email");

        mockMvc.perform(patch(USER_URL + "/{userId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());
    }

    @Test
    void updateUser_EmptyBody_ReturnsOk() throws Exception {
        // Empty body is valid for UpdateUserRequestDto — partial update allows all fields optional
        mockMvc.perform(patch(USER_URL + "/{userId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteUser_ReturnsOk() throws Exception {
        mockMvc.perform(delete(USER_URL + "/{userId}", 1))
                .andExpect(status().isOk());
    }
}
