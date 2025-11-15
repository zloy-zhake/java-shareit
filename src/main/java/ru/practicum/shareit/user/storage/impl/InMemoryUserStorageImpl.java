package ru.practicum.shareit.user.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.DuplicatedDataException;
import ru.practicum.shareit.user.dto.NewUserRequestDto;
import ru.practicum.shareit.user.mapper.UserMapper;
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
    public User addUser(NewUserRequestDto newUserRequestDto) {
        log.info("InMemoryUserStorageImpl:addUser(): запрос на создание нового пользователя {}", newUserRequestDto);
        checkIfUserExists(newUserRequestDto);
        User newUser = UserMapper.newUserRequestDtoToUser(newUserRequestDto);
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
    public void checkIfUserExists(NewUserRequestDto newUserRequestDto) throws DuplicatedDataException {
        for (User user : userMap.values()) {
            if (newUserRequestDto.getName().equals(user.getName())) {
                throw new DuplicatedDataException("Пользователь с именем " + newUserRequestDto.getName() + " уже имеется в базе данных");
            }
            if (newUserRequestDto.getEmail().equals(user.getEmail()))
                throw new DuplicatedDataException("Пользователь с email " + newUserRequestDto.getEmail() + " уже имеется в базе данных");
        }
    }

    private int getNextId() {
        currentId = currentId + 1;
        return currentId;
    }
}
