package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.ItemRequestClient;
import ru.practicum.shareit.request.dto.NewItemRequestDto;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    private static final String REQUESTS_URL = "/requests";
    private static final long USER_ID = 1L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestClient itemRequestClient;

    @Test
    void addItemRequest_ValidRequest_ReturnsOk() throws Exception {
        NewItemRequestDto requestDto = new NewItemRequestDto();
        requestDto.setDescription("Need a laptop");

        mockMvc.perform(post(REQUESTS_URL)
                        .header("X-Sharer-User-Id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());
    }

    @Test
    void addItemRequest_BlankDescription_ReturnsBadRequest() throws Exception {
        String body = "{\"description\":\"\"}";

        mockMvc.perform(post(REQUESTS_URL)
                        .header("X-Sharer-User-Id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addItemRequest_MissingDescription_ReturnsBadRequest() throws Exception {
        String body = "{}";

        mockMvc.perform(post(REQUESTS_URL)
                        .header("X-Sharer-User-Id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getRequestsOfUser_ReturnsOk() throws Exception {
        mockMvc.perform(get(REQUESTS_URL)
                        .header("X-Sharer-User-Id", USER_ID))
                .andExpect(status().isOk());
    }

    @Test
    void getAllRequests_ReturnsOk() throws Exception {
        mockMvc.perform(get(REQUESTS_URL + "/all")
                        .header("X-Sharer-User-Id", USER_ID))
                .andExpect(status().isOk());
    }

    @Test
    void getRequestById_ReturnsOk() throws Exception {
        mockMvc.perform(get(REQUESTS_URL + "/{requestId}", 1))
                .andExpect(status().isOk());
    }
}
