package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestService itemRequestService;

    @Test
    void addItemRequest_ValidRequest_ReturnsCreated() throws Exception {
        NewItemRequestDto requestDto = new NewItemRequestDto();
        requestDto.setDescription("Need a laptop");

        ItemRequestDto expectedDto = createItemRequestDto(1, "Need a laptop", 1);

        when(itemRequestService.addItemRequest(eq(1), any(NewItemRequestDto.class))).thenReturn(expectedDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Need a laptop"))
                .andExpect(jsonPath("$.requesterId").value(1));
    }

    @Test
    void getRequestsOfUser_WithRequests_ReturnsRequests() throws Exception {
        ItemRequestDto request1 = createItemRequestDto(1, "Need a laptop", 1);
        ItemRequestDto request2 = createItemRequestDto(2, "Need a phone", 1);

        when(itemRequestService.getRequestsOfUser(1)).thenReturn(List.of(request1, request2));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("Need a laptop"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].description").value("Need a phone"));
    }

    @Test
    void getRequestsOfUser_NoRequests_ReturnsEmptyList() throws Exception {
        when(itemRequestService.getRequestsOfUser(1)).thenReturn(List.of());

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getAllRequests_ReturnsRequests() throws Exception {
        ItemRequestDto request = createItemRequestDto(1, "Need a laptop", 2);

        when(itemRequestService.getAllRequests(1)).thenReturn(List.of(request));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("Need a laptop"))
                .andExpect(jsonPath("$[0].requesterId").value(2));
    }

    @Test
    void getRequestById_ExistingRequest_ReturnsRequest() throws Exception {
        ItemRequestDto expectedDto = createItemRequestDto(1, "Need a laptop", 1);

        when(itemRequestService.getRequestById(1)).thenReturn(expectedDto);

        mockMvc.perform(get("/requests/{requestId}", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Need a laptop"))
                .andExpect(jsonPath("$.requesterId").value(1));
    }

    @Test
    void getRequestById_NonExistingRequest_ReturnsNotFound() throws Exception {
        when(itemRequestService.getRequestById(999))
                .thenThrow(new NoSuchElementException("Запрос с ID 999 не существует"));

        mockMvc.perform(get("/requests/{requestId}", 999)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    private ItemRequestDto createItemRequestDto(int id, String description, int requesterId) {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(id);
        dto.setDescription(description);
        dto.setRequesterId(requesterId);
        dto.setCreated(LocalDateTime.now());
        dto.setItems(List.of());
        return dto;
    }
}
