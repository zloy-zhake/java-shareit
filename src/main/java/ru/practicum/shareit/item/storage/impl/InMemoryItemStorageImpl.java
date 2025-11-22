package ru.practicum.shareit.item.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;

import java.util.HashMap;
import java.util.Map;

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

    private int getNextId() {
        currentId = currentId + 1;
        return currentId;
    }
}
