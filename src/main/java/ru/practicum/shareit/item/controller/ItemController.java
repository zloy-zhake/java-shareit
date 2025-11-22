package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequestDto;
import ru.practicum.shareit.item.dto.UpdateItemRequestDto;
import ru.practicum.shareit.item.service.ItemService;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto addItem(
            @RequestHeader("X-Sharer-User-Id") int sharerUserId,
            @RequestBody NewItemRequestDto newItemRequestDto
    ) {
        log.info("ItemController:addItem(): запрос на создание нового предмета {}", newItemRequestDto);
        return itemService.addItem(sharerUserId, newItemRequestDto);
    }

    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto updateItem(
            @RequestHeader("X-Sharer-User-Id") int sharerUserId,
            @PathVariable int itemId,
            @RequestBody UpdateItemRequestDto updateItemRequestDto
    ) {
        log.info("ItemController:updateItem(): запрос на обновление предмета {}", newItemRequestDto);
        return itemService.updateItem(sharerUserId, itemId, updateItemRequestDto);
    }
}
