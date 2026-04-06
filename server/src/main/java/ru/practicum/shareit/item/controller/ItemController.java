package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto addItem(
            @RequestHeader("X-Sharer-User-Id") int sharerUserId,
            @RequestBody NewItemRequestDto newItemRequestDto
    ) {
        log.info("Server ItemController:addItem(): запрос на создание нового предмета {} от пользователя с id={}",
                newItemRequestDto, sharerUserId);
        return itemService.addItem(sharerUserId, newItemRequestDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(
            @RequestHeader("X-Sharer-User-Id") int sharerUserId,
            @PathVariable int itemId,
            @RequestBody UpdateItemRequestDto updateItemRequestDto
    ) {
        log.info("Server ItemController:updateItem(): запрос на обновление предмета id={} от пользователя id={}",
                itemId, sharerUserId);
        return itemService.updateItem(sharerUserId, itemId, updateItemRequestDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(
            @RequestHeader("X-Sharer-User-Id") int requestingUserId,
            @PathVariable int itemId
    ) {
        log.info("Server ItemController:getItemById(): запрос на получение предмета с id {}", itemId);
        return itemService.getItemById(itemId, requestingUserId);
    }

    @GetMapping
    public List<ItemDto> getAllItemsFromUser(
            @RequestHeader("X-Sharer-User-Id") int sharerUserId
    ) {
        log.info("Server ItemController:getAllItemsFromUser(): запрос на получение всех предметов пользователя с id {}",
                sharerUserId);
        return itemService.getAllItemsFromUser(sharerUserId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchAvailableItems(
            @RequestHeader("X-Sharer-User-Id") int requestingUserId,
            @RequestParam("text") String searchString
    ) {
        log.info("Server ItemController:searchAvailableItems(): запрос на поиск доступных предметов по запросу {}",
                searchString);
        return itemService.searchAvailableItems(searchString, requestingUserId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(
            @RequestHeader("X-Sharer-User-Id") int sharerUserId,
            @PathVariable int itemId,
            @RequestBody NewCommentRequestDto newCommentRequestDto
    ) {
        log.info("Server ItemController:addComment(): запрос на добавление комментария к вещи с ID={} от пользователя с ID={}",
                itemId, sharerUserId);
        return itemService.addComment(sharerUserId, itemId, newCommentRequestDto);
    }
}
