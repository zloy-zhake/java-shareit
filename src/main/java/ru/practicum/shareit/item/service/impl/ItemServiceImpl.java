package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.DoesNotBelongToUserException;
import ru.practicum.shareit.exceptions.ItemNotValidException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequestDto;
import ru.practicum.shareit.item.dto.UpdateItemRequestDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto addItem(int sharerUserId, NewItemRequestDto newItemRequestDto) {
        log.info(
                "ItemServiceImpl:addItem(): запрос на создание нового предмета {} от пользователя с id={}",
                newItemRequestDto,
                sharerUserId
        );
        validateNewItemRequestDto(newItemRequestDto);
        Item newItem = ItemMapper.newItemRequestDtoToItem(newItemRequestDto);
        if (userRepository.findById(sharerUserId).isPresent()) {
            newItem.setOwnerId(sharerUserId);
        }
        else {
            throw new NoSuchElementException("Пользователя с ID " + sharerUserId + " не существует");
        }
        Item createdItem = itemRepository.save(newItem);
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
        if (newItemRequestDto.getAvailable().isEmpty()) {
            throw new ItemNotValidException("У вещи отсутствует информация о доступности для аренды");
        }
    }

    @Override
    public ItemDto updateItem(int sharerUserId, int itemId, UpdateItemRequestDto updateItemRequestDto) {
        log.info(
                "ItemServiceImpl:updateItem(): запрос на обновление предмета id={} от пользователя id={}, новые данные {}",
                itemId,
                sharerUserId,
                updateItemRequestDto
        );
        checkIfItemBelongsToUser(itemId, sharerUserId);
        Item itemToUpdate = itemRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchElementException("Предмета с ID " + itemId + " не существует"));
        Item updatedItem = ItemMapper.updateItemFields(itemToUpdate, updateItemRequestDto);
        updatedItem = itemRepository.save(updatedItem);
        log.info("ItemServiceImpl:updateItem(): предмет id={} отредактирован, новые данные: {}", itemId, updatedItem);
        return ItemMapper.itemToItemDto(updatedItem);
    }

    @Override
    public ItemDto getItemById(int itemId) {
        log.info("ItemServiceImpl:getItemById(): запрос на получение предмета с id {}", itemId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchElementException("Предмета с ID " + itemId + " не существует"));
        return ItemMapper.itemToItemDto(item);
    }

    @Override
    public List<ItemDto> getAllItemsFromUser(int sharerUserId) {
        log.info("ItemServiceImpl:getAllItemsFromUser(): запрос на получение всех предметов пользователя с id {}", sharerUserId);
        List<Item> itemsOfUser = itemRepository.findAllByOwnerId(sharerUserId);
        return itemsOfUser.stream()
                .map(ItemMapper::itemToItemDto)
                .toList();
    }

    @Override
    public List<ItemDto> searchAvailableItems(String searchString) {
        log.info("ItemServiceImpl:searchAvailableItems(): запрос на поиск доступных предметов по запросу {}", searchString);
        if (searchString == null || searchString.isEmpty()) {
            return new ArrayList<>();
        }
        List<Item> itemSearchResults = itemRepository.searchAvailableItems(searchString);
        return itemSearchResults.stream()
                .map(ItemMapper::itemToItemDto)
                .toList();
    }

    private void checkIfItemBelongsToUser(int itemId, int userId) {
        int ownerId = itemRepository.findById(itemId).
                orElseThrow(() -> new NoSuchElementException("Предмета с ID " + itemId + " не существует"))
                .getOwnerId();
        if (userId != ownerId) {
            throw new DoesNotBelongToUserException(
                    "Предмет ID=%s не принадлежит пользователю ID=%s".formatted(itemId, userId)
            );
        }
    }
}
