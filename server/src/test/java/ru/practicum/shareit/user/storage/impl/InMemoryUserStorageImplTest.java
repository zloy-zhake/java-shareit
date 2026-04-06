package ru.practicum.shareit.user.storage.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exceptions.DuplicatedDataException;
import ru.practicum.shareit.user.model.User;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryUserStorageImplTest {

    private InMemoryUserStorageImpl storage;

    @BeforeEach
    void setUp() {
        storage = new InMemoryUserStorageImpl();
    }

    @Test
    void addUser_ValidUser_SetsIdAndReturnsUser() {
        User user = new User();
        user.setName("Alice");
        user.setEmail("alice@example.com");

        User result = storage.addUser(user);

        assertNotNull(result.getId());
        assertEquals(0, result.getId());
        assertEquals("Alice", result.getName());
    }

    @Test
    void addUser_SecondUser_GetsNextId() {
        User user1 = new User();
        user1.setName("Alice");
        user1.setEmail("alice@example.com");

        User user2 = new User();
        user2.setName("Bob");
        user2.setEmail("bob@example.com");

        storage.addUser(user1);
        User result2 = storage.addUser(user2);

        assertEquals(1, result2.getId());
    }

    @Test
    void addUser_DuplicateName_ThrowsException() {
        User user1 = new User();
        user1.setName("Alice");
        user1.setEmail("alice@example.com");
        storage.addUser(user1);

        User user2 = new User();
        user2.setName("Alice");
        user2.setEmail("different@example.com");

        assertThrows(DuplicatedDataException.class, () -> storage.addUser(user2));
    }

    @Test
    void addUser_DuplicateEmail_ThrowsException() {
        User user1 = new User();
        user1.setName("Alice");
        user1.setEmail("alice@example.com");
        storage.addUser(user1);

        User user2 = new User();
        user2.setName("Bob");
        user2.setEmail("alice@example.com");

        assertThrows(DuplicatedDataException.class, () -> storage.addUser(user2));
    }

    @Test
    void getUserById_ExistingUser_ReturnsUser() {
        User user = new User();
        user.setName("Alice");
        user.setEmail("alice@example.com");
        storage.addUser(user);

        User result = storage.getUserById(0);

        assertNotNull(result);
        assertEquals("Alice", result.getName());
    }

    @Test
    void getUserById_NonExisting_ThrowsException() {
        assertThrows(NoSuchElementException.class, () -> storage.getUserById(999));
    }

    @Test
    void updateUser_ExistingUser_UpdatesFields() {
        User user = new User();
        user.setName("Alice");
        user.setEmail("alice@example.com");
        storage.addUser(user);

        user.setName("Alice Updated");
        User result = storage.updateUser(user);

        assertEquals("Alice Updated", result.getName());
        User fetched = storage.getUserById(0);
        assertEquals("Alice Updated", fetched.getName());
    }

    @Test
    void updateUser_EmailUsedByOther_ThrowsException() {
        User user1 = new User();
        user1.setName("Alice");
        user1.setEmail("alice@example.com");
        storage.addUser(user1);

        User user2 = new User();
        user2.setName("Bob");
        user2.setEmail("bob@example.com");
        storage.addUser(user2);

        user2.setEmail("alice@example.com");

        assertThrows(DuplicatedDataException.class, () -> storage.updateUser(user2));
    }

    @Test
    void updateUser_SameEmail_UpdatesSuccessfully() {
        User user = new User();
        user.setName("Alice");
        user.setEmail("alice@example.com");
        storage.addUser(user);

        user.setName("Alice Updated");
        // Same email, same user — should not throw
        assertDoesNotThrow(() -> storage.updateUser(user));
    }

    @Test
    void deleteUser_ExistingUser_RemovesUser() {
        User user = new User();
        user.setName("Alice");
        user.setEmail("alice@example.com");
        storage.addUser(user);

        storage.deleteUser(0);

        assertThrows(NoSuchElementException.class, () -> storage.getUserById(0));
    }

    @Test
    void deleteUser_NonExistingUser_DoesNotThrow() {
        assertDoesNotThrow(() -> storage.deleteUser(999));
    }

    @Test
    void deleteUser_AllowsReuseOfNameAndEmail() {
        User user = new User();
        user.setName("Alice");
        user.setEmail("alice@example.com");
        storage.addUser(user);
        storage.deleteUser(0);

        User newUser = new User();
        newUser.setName("Alice");
        newUser.setEmail("alice@example.com");

        // Should not throw since old user was deleted
        assertDoesNotThrow(() -> storage.addUser(newUser));
    }
}
