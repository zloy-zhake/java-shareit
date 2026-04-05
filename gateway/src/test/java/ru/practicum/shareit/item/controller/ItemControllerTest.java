package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.dto.NewCommentRequestDto;
import ru.practicum.shareit.item.dto.NewItemRequestDto;
import ru.practicum.shareit.item.dto.UpdateItemRequestDto;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    private static final String ITEMS_URL = "/items";
    private static final long USER_ID = 1L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemClient itemClient;

    @Test
    void addItem_ValidRequest_ReturnsOk() throws Exception {
        NewItemRequestDto requestDto = new NewItemRequestDto();
        requestDto.setName("Test Item");
        requestDto.setDescription("Test Description");
        requestDto.setAvailable(true);

        mockMvc.perform(post(ITEMS_URL)
                        .header("X-Sharer-User-Id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());
    }

    @Test
    void addItem_BlankName_ReturnsBadRequest() throws Exception {
        String body = "{\"name\":\"\",\"description\":\"Desc\",\"available\":true}";

        mockMvc.perform(post(ITEMS_URL)
                        .header("X-Sharer-User-Id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addItem_BlankDescription_ReturnsBadRequest() throws Exception {
        String body = "{\"name\":\"Item\",\"description\":\"\",\"available\":true}";

        mockMvc.perform(post(ITEMS_URL)
                        .header("X-Sharer-User-Id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addItem_MissingHeader_ReturnsBadRequest() throws Exception {
        NewItemRequestDto requestDto = new NewItemRequestDto();
        requestDto.setName("Item");
        requestDto.setDescription("Desc");
        requestDto.setAvailable(true);

        mockMvc.perform(post(ITEMS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateItem_ValidRequest_ReturnsOk() throws Exception {
        UpdateItemRequestDto requestDto = new UpdateItemRequestDto();
        requestDto.setName("Updated Item");

        mockMvc.perform(patch(ITEMS_URL + "/{itemId}", 1)
                        .header("X-Sharer-User-Id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());
    }

    @Test
    void updateItem_AllFields_ReturnsOk() throws Exception {
        UpdateItemRequestDto requestDto = new UpdateItemRequestDto();
        requestDto.setName("Updated");
        requestDto.setDescription("Updated desc");
        requestDto.setAvailable(false);

        mockMvc.perform(patch(ITEMS_URL + "/{itemId}", 1)
                        .header("X-Sharer-User-Id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());
    }

    @Test
    void getItemById_ReturnsOk() throws Exception {
        mockMvc.perform(get(ITEMS_URL + "/{itemId}", 1)
                        .header("X-Sharer-User-Id", USER_ID))
                .andExpect(status().isOk());
    }

    @Test
    void getAllItemsFromUser_ReturnsOk() throws Exception {
        mockMvc.perform(get(ITEMS_URL)
                        .header("X-Sharer-User-Id", USER_ID))
                .andExpect(status().isOk());
    }

    @Test
    void searchAvailableItems_ReturnsOk() throws Exception {
        mockMvc.perform(get(ITEMS_URL + "/search")
                        .header("X-Sharer-User-Id", USER_ID)
                        .param("text", "laptop"))
                .andExpect(status().isOk());
    }

    @Test
    void addComment_ValidRequest_ReturnsOk() throws Exception {
        NewCommentRequestDto requestDto = new NewCommentRequestDto();
        requestDto.setText("Great item!");

        mockMvc.perform(post(ITEMS_URL + "/{itemId}/comment", 1)
                        .header("X-Sharer-User-Id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());
    }

    @Test
    void addComment_BlankText_ReturnsBadRequest() throws Exception {
        String body = "{\"text\":\"\"}";

        mockMvc.perform(post(ITEMS_URL + "/{itemId}/comment", 1)
                        .header("X-Sharer-User-Id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }
}
