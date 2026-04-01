package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.util.HttpHeaderConstants;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDto addItemRequest(
            @RequestHeader(HttpHeaderConstants.X_SHARER_USER_ID) int requesterId,
            @RequestBody NewItemRequestDto newItemRequestDto
    ) {
        log.info("ItemRequestController:addItemRequest(): запрос на создание нового запроса вещи {}", newItemRequestDto);
        return itemRequestService.addItemRequest(requesterId, newItemRequestDto);
    }
}
