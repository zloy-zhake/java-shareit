package ru.practicum.shareit.item.storage.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryItemStorageImplTest {

    private InMemoryItemStorageImpl storage;

    @BeforeEach
    void setUp() {
        storage = new InMemoryItemStorageImpl();
    }

    @Test
    void addItem_SetsIdAndReturnsItem() {
        Item item = new Item();
        item.setName("Laptop");
        item.setDescription("A laptop");
        item.setAvailable(true);
        item.setOwnerId(1);

        Item result = storage.addItem(item);

        assertNotNull(result.getId());
        assertEquals(0, result.getId());
        assertEquals("Laptop", result.getName());
    }

    @Test
    void addItem_SecondItem_GetsNextId() {
        Item item1 = new Item();
        item1.setName("Item 1");
        item1.setOwnerId(1);

        Item item2 = new Item();
        item2.setName("Item 2");
        item2.setOwnerId(1);

        storage.addItem(item1);
        Item result2 = storage.addItem(item2);

        assertEquals(1, result2.getId());
    }

    @Test
    void getItemById_ExistingItem_ReturnsItem() {
        Item item = new Item();
        item.setName("Laptop");
        item.setOwnerId(1);

        storage.addItem(item);

        Item result = storage.getItemById(0);

        assertNotNull(result);
        assertEquals("Laptop", result.getName());
    }

    @Test
    void getItemById_NonExisting_ThrowsException() {
        assertThrows(NoSuchElementException.class, () -> storage.getItemById(999));
    }

    @Test
    void updateItem_ExistingItem_UpdatesFields() {
        Item item = new Item();
        item.setName("Old Name");
        item.setOwnerId(1);

        storage.addItem(item);

        item.setName("New Name");
        Item result = storage.updateItem(item);

        assertEquals("New Name", result.getName());
        Item fetched = storage.getItemById(0);
        assertEquals("New Name", fetched.getName());
    }

    @Test
    void getAllItemsFromUser_ReturnsOnlyUserItems() {
        Item item1 = new Item();
        item1.setName("User1 Item");
        item1.setOwnerId(1);

        Item item2 = new Item();
        item2.setName("User2 Item");
        item2.setOwnerId(2);

        Item item3 = new Item();
        item3.setName("User1 Item 2");
        item3.setOwnerId(1);

        storage.addItem(item1);
        storage.addItem(item2);
        storage.addItem(item3);

        List<Item> result = storage.getAllItemsFromUser(1);

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(i -> i.getOwnerId() == 1));
    }

    @Test
    void getAllItemsFromUser_NoItems_ReturnsEmptyList() {
        List<Item> result = storage.getAllItemsFromUser(1);

        assertTrue(result.isEmpty());
    }

    @Test
    void searchAvailableItems_MatchesByName() {
        Item item = new Item();
        item.setName("Laptop Pro");
        item.setDescription("A powerful device");
        item.setAvailable(true);
        item.setOwnerId(1);

        storage.addItem(item);

        List<Item> result = storage.searchAvailableItems("laptop");

        assertEquals(1, result.size());
        assertEquals("Laptop Pro", result.get(0).getName());
    }

    @Test
    void searchAvailableItems_MatchesByDescription() {
        Item item = new Item();
        item.setName("Device");
        item.setDescription("A powerful laptop for work");
        item.setAvailable(true);
        item.setOwnerId(1);

        storage.addItem(item);

        List<Item> result = storage.searchAvailableItems("work");

        assertEquals(1, result.size());
    }

    @Test
    void searchAvailableItems_ExcludesUnavailable() {
        Item item = new Item();
        item.setName("Laptop");
        item.setDescription("A laptop");
        item.setAvailable(false);
        item.setOwnerId(1);

        storage.addItem(item);

        List<Item> result = storage.searchAvailableItems("laptop");

        assertTrue(result.isEmpty());
    }

    @Test
    void searchAvailableItems_NullString_ReturnsEmptyList() {
        Item item = new Item();
        item.setName("Laptop");
        item.setAvailable(true);
        item.setOwnerId(1);
        storage.addItem(item);

        List<Item> result = storage.searchAvailableItems(null);

        assertTrue(result.isEmpty());
    }

    @Test
    void searchAvailableItems_EmptyString_ReturnsEmptyList() {
        Item item = new Item();
        item.setName("Laptop");
        item.setAvailable(true);
        item.setOwnerId(1);
        storage.addItem(item);

        List<Item> result = storage.searchAvailableItems("");

        assertTrue(result.isEmpty());
    }
}
