package ru.practicum.shareit.item.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;

import java.util.*;

@Component
@Slf4j
public class InMemoryItemStorageImpl implements ItemStorage {

    private final Map<Integer, Item> itemMap = new HashMap<>();
    private int currentId = 0;

    @Override
    public Item addItem(Item newItem) {
        log.info("InMemoryItemStorageImpl:addItem(): запрос на создание нового предмета {}", newItem);
        int id = getNextId();
        newItem.setId(id);
        itemMap.put(id, newItem);
        log.info("InMemoryItemStorageImpl:addItem(): создан новый предмет {}", newItem);
        return newItem;
    }

    @Override
    public Item getItemById(int itemId) {
        log.info("InMemoryItemStorageImpl:getItemById(): запрос на получение предмета с id {}", itemId);
        if (!itemMap.containsKey(itemId)) {
            throw new NoSuchElementException("Предмета с ID " + itemId + " не существует");
        }
        log.info("InMemoryItemStorageImpl:getItemById(): Предмет с id {} найден", itemId);
        return itemMap.get(itemId);
    }

    @Override
    public Item updateItem(Item updatedItem) {
        log.info("InMemoryItemStorageImpl:updateItem(): запрос на обновление предмета {}", updatedItem);
        itemMap.put(updatedItem.getId(), updatedItem);
        log.info("InMemoryItemStorageImpl:updateItem(): предмет {} обновлен", updatedItem);
        return updatedItem;
    }

    @Override
    public List<Item> getAllItemsFromUser(int sharerUserId) {
        log.info("InMemoryItemStorageImpl:getAllItemsFromUser(): запрос на получение всех предметов пользователя с id {}", sharerUserId);
        return itemMap.values().stream()
                .filter(item -> item.getOwner() == sharerUserId)
                .toList();
    }

    @Override
    public List<Item> searchAvailableItems(String searchString) {
        log.info("InMemoryItemStorageImpl:searchAvailableItems(): запрос на поиск доступных предметов по запросу {}", searchString);
        if (searchString == null || searchString.isEmpty()) {
            return new ArrayList<>();
        }
        return itemMap.values().stream()
                .filter(Item::isAvailable)
                .filter(item -> {
                    return item.getName().toLowerCase().contains(searchString.toLowerCase())
                            || item.getDescription().toLowerCase().contains(searchString.toLowerCase());
                })
                .toList();
    }

    private int getNextId() {
        return currentId++;
    }
}
