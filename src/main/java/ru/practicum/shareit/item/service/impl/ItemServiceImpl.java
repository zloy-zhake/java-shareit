package ru.practicum.shareit.item.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ItemNotValidException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequestDto;
import ru.practicum.shareit.item.dto.UpdateItemRequestDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.NoSuchElementException;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    public ItemServiceImpl(
            @Qualifier("inMemoryItemStorageImpl") ItemStorage itemStorage,
            @Qualifier("inMemoryUserStorageImpl") UserStorage userStorage
    ) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
    }


    @Override
    public ItemDto addItem(int sharerUserId, NewItemRequestDto newItemRequestDto) {
        log.info(
                "ItemServiceImpl:addItem(): запрос на создание нового предмета {} от пользователя с id={}",
                newItemRequestDto,
                sharerUserId
        );
        validateNewItemRequestDto(newItemRequestDto);
        Item newItem = ItemMapper.newItemRequestDtoToItem(newItemRequestDto);
        if (userStorage.getUserById(sharerUserId) != null) {
            newItem.setOwner(sharerUserId);
        }
        Item createdItem = itemStorage.addItem(newItem);
        log.info("ItemServiceImpl:addItem(): создан новый предмет {}", createdItem);
        return ItemMapper.itemToItemDto(createdItem);
    }

    @Override
    public void validateNewItemRequestDto(NewItemRequestDto newItemRequestDto) {
        if (newItemRequestDto.getName() == null || newItemRequestDto.getName().isBlank()) {
            throw new ItemNotValidException("Имя вещи не может быть пустым или null");
        }
        if (newItemRequestDto.getDescription() == null || newItemRequestDto.getDescription().isBlank()) {
            throw new ItemNotValidException("Описание вещи не может быть пустым или null");
        }
        if (newItemRequestDto.getAvailable() == null) {
            throw new ItemNotValidException("У вещи отсутствует информация о доступности для аренды");
        }
    }

    @Override
    public ItemDto updateItem(int sharerUserId, int itemId, UpdateItemRequestDto updateItemRequestDto) {
        log.info(
                "ItemServiceImpl:updateItem(): запрос на обновление предмета c id={} от пользователя с id={}, новые данные {}",
                itemId,
                sharerUserId,
                updateItemRequestDto
        );

    }
}
