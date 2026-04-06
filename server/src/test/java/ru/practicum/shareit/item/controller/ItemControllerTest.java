package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exceptions.DoesNotBelongToUserException;
import ru.practicum.shareit.exceptions.ItemNotValidException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @Test
    void addItem_ValidRequest_ReturnsOk() throws Exception {
        NewItemRequestDto requestDto = new NewItemRequestDto();
        requestDto.setName("Test Item");
        requestDto.setDescription("Test Description");
        requestDto.setAvailable(true);

        ItemDto expectedDto = createItemDto(1, "Test Item", "Test Description", true, 100);

        when(itemService.addItem(eq(1), any(NewItemRequestDto.class))).thenReturn(expectedDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Item"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void addItem_InvalidName_ReturnsBadRequest() throws Exception {
        NewItemRequestDto requestDto = new NewItemRequestDto();
        requestDto.setName("");
        requestDto.setDescription("Test Description");
        requestDto.setAvailable(true);

        when(itemService.addItem(eq(1), any(NewItemRequestDto.class)))
                .thenThrow(new ItemNotValidException("Имя вещи не может быть пустым или null"));

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateItem_ValidRequest_ReturnsOk() throws Exception {
        UpdateItemRequestDto requestDto = new UpdateItemRequestDto();
        requestDto.setName("Updated Item");

        ItemDto expectedDto = createItemDto(1, "Updated Item", "Test Description", true, 100);

        when(itemService.updateItem(eq(1), eq(1), any(UpdateItemRequestDto.class))).thenReturn(expectedDto);

        mockMvc.perform(patch("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Item"));
    }

    @Test
    void updateItem_ItemNotBelongToUser_ReturnsForbidden() throws Exception {
        UpdateItemRequestDto requestDto = new UpdateItemRequestDto();
        requestDto.setName("Updated Item");

        when(itemService.updateItem(eq(1), eq(1), any(UpdateItemRequestDto.class)))
                .thenThrow(new DoesNotBelongToUserException("Предмет ID=1 не принадлежит пользователю ID=1"));

        mockMvc.perform(patch("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void getItemById_ExistingItem_ReturnsOk() throws Exception {
        ItemDto expectedDto = createItemDto(1, "Test Item", "Test Description", true, 100);

        when(itemService.getItemById(1, 1)).thenReturn(expectedDto);

        mockMvc.perform(get("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Item"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void getItemById_NonExistingItem_ReturnsNotFound() throws Exception {
        when(itemService.getItemById(999, 1))
                .thenThrow(new NoSuchElementException("Предмета с ID 999 не существует"));

        mockMvc.perform(get("/items/{itemId}", 999)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllItemsFromUser_ReturnsListOfItems() throws Exception {
        ItemDto item1 = createItemDto(1, "Item 1", "Description 1", true, 100);
        ItemDto item2 = createItemDto(2, "Item 2", "Description 2", true, 100);

        when(itemService.getAllItemsFromUser(1)).thenReturn(List.of(item1, item2));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Item 1"))
                .andExpect(jsonPath("$[1].name").value("Item 2"));
    }

    @Test
    void searchAvailableItems_ReturnsMatchingItems() throws Exception {
        ItemDto item = createItemDto(1, "Laptop", "A powerful laptop", true, 100);

        when(itemService.searchAvailableItems("laptop", 1)).thenReturn(List.of(item));

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1)
                        .param("text", "laptop")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Laptop"));
    }

    @Test
    void addComment_ValidRequest_ReturnsComment() throws Exception {
        NewCommentRequestDto requestDto = new NewCommentRequestDto();
        requestDto.setText("Great item!");

        CommentDto expectedDto = new CommentDto();
        expectedDto.setId(1);
        expectedDto.setText("Great item!");
        expectedDto.setAuthorName("Test User");

        when(itemService.addComment(eq(1), eq(1), any(NewCommentRequestDto.class))).thenReturn(expectedDto);

        mockMvc.perform(post("/items/{itemId}/comment", 1)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.text").value("Great item!"))
                .andExpect(jsonPath("$.authorName").value("Test User"));
    }

    private ItemDto createItemDto(int id, String name, String description, boolean available, int ownerId) {
        ItemDto dto = new ItemDto();
        dto.setId(id);
        dto.setName(name);
        dto.setDescription(description);
        dto.setAvailable(available);
        dto.setOwner(ownerId);
        return dto;
    }
}
