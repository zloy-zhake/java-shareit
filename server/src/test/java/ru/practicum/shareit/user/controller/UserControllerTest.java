package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exceptions.UserNotValidException;
import ru.practicum.shareit.user.dto.NewUserRequestDto;
import ru.practicum.shareit.user.dto.UpdateUserRequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void addUser_ValidRequest_ReturnsCreated() throws Exception {
        NewUserRequestDto requestDto = new NewUserRequestDto();
        requestDto.setName("Test User");
        requestDto.setEmail("test@example.com");

        UserDto expectedDto = new UserDto();
        expectedDto.setId(1);
        expectedDto.setName("Test User");
        expectedDto.setEmail("test@example.com");

        when(userService.addUser(any(NewUserRequestDto.class))).thenReturn(expectedDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void addUser_InvalidEmail_ReturnsBadRequest() throws Exception {
        NewUserRequestDto requestDto = new NewUserRequestDto();
        requestDto.setName("Test User");
        requestDto.setEmail("invalid-email");

        when(userService.addUser(any(NewUserRequestDto.class)))
                .thenThrow(new UserNotValidException("Email не может быть пустым или null"));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUserById_ExistingUser_ReturnsOk() throws Exception {
        UserDto expectedDto = new UserDto();
        expectedDto.setId(1);
        expectedDto.setName("Test User");
        expectedDto.setEmail("test@example.com");

        when(userService.getUserById(1)).thenReturn(expectedDto);

        mockMvc.perform(get("/users/{userId}", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void getUserById_NonExistingUser_ReturnsNotFound() throws Exception {
        when(userService.getUserById(999))
                .thenThrow(new NoSuchElementException("Пользователя с ID 999 не существует"));

        mockMvc.perform(get("/users/{userId}", 999)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateUser_ValidRequest_ReturnsOk() throws Exception {
        UpdateUserRequestDto requestDto = new UpdateUserRequestDto();
        requestDto.setName("Updated User");

        UserDto expectedDto = new UserDto();
        expectedDto.setId(1);
        expectedDto.setName("Updated User");
        expectedDto.setEmail("test@example.com");

        when(userService.updateUser(eq(1), any(UpdateUserRequestDto.class))).thenReturn(expectedDto);

        mockMvc.perform(patch("/users/{userId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated User"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void updateUser_NonExistingUser_ReturnsNotFound() throws Exception {
        UpdateUserRequestDto requestDto = new UpdateUserRequestDto();
        requestDto.setName("Updated User");

        when(userService.updateUser(eq(999), any(UpdateUserRequestDto.class)))
                .thenThrow(new NoSuchElementException("Пользователя с ID 999 не существует"));

        mockMvc.perform(patch("/users/{userId}", 999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUser_ExistingUser_ReturnsOk() throws Exception {
        doNothing().when(userService).deleteUser(1);

        mockMvc.perform(delete("/users/{userId}", 1))
                .andExpect(status().isOk());
    }
}
