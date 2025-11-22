package ru.practicum.shareit.user.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.DuplicatedDataException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@Component
@Slf4j
public class InMemoryUserStorageImpl implements UserStorage {

    private final Map<Integer, User> userMap = new HashMap<>();
    private int currentId = 0;

    @Override
    public User addUser(User newUser) {
        log.info("InMemoryUserStorageImpl:addUser(): запрос на создание нового пользователя {}", newUser);
        checkIfUserExists(newUser);
        int id = getNextId();
        newUser.setId(id);
        userMap.put(id, newUser);
        log.info("InMemoryUserStorageImpl:addUser(): создан новый пользователь {}", newUser);
        return newUser;
    }

    @Override
    public User getUserById(int userId) {
        log.info("InMemoryUserStorageImpl:getUserById(): запрос на получение пользователя с id {}", userId);
        if (!userMap.containsKey(userId)) {
            throw new NoSuchElementException("Пользователя с ID " + userId + " не существует");
        }
        log.info("InMemoryUserStorageImpl:getUserById(): пользователь с id {} найден", userId);
        return userMap.get(userId);
    }

    @Override
    public void checkIfUserExists(User newUser) throws DuplicatedDataException {
        for (User user : userMap.values()) {
            if (newUser.getName().equals(user.getName())) {
                throw new DuplicatedDataException("Пользователь с именем " + newUser.getName() + " уже имеется в базе данных");
            }
            if (newUser.getEmail().equals(user.getEmail()))
                throw new DuplicatedDataException("Пользователь с email " + newUser.getEmail() + " уже имеется в базе данных");
        }
    }

    @Override
    public User updateUser(User updatedUser) {
        if (emailUsedByOtherUser(updatedUser.getEmail(), updatedUser.getId())) {
            throw new DuplicatedDataException("email " + updatedUser.getEmail() + " используется другим пользователем");
        }
        User userToUpdate = getUserById(updatedUser.getId());
        userToUpdate.setName(updatedUser.getName());
        userToUpdate.setEmail(updatedUser.getEmail());
        return userToUpdate;
    }

    @Override
    public void deleteUser(int userId) {
        log.info("InMemoryUserStorageImpl:deleteUser(): запрос на удаление пользователя с id {}", userId);
        userMap.remove(userId);
        log.info("InMemoryUserStorageImpl:deleteUser(): пользователь с id {} удалён", userId);
    }

    private int getNextId() {
        currentId = currentId + 1;
        return currentId;
    }

    private boolean emailUsedByOtherUser(String emailToCheck, int userId) {
        for (User user : userMap.values()) {
            if (userId == user.getId()) {
                continue;
            }
            if (emailToCheck.equals(user.getEmail())) {
                return true;
            }
        }
        return false;
    }
}
