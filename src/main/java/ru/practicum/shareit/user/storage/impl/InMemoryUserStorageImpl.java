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

@Component
@Slf4j
public class InMemoryUserStorageImpl implements UserStorage {

    private final Map<Integer, User> userMap = new HashMap<>();
    private int currentId = 0;

    @Override
    public User addUser(NewUserRequestDto newUserRequestDto) {
        log.info("InMemoryUserStorageImpl:addUser(): запрос на создание нового пользователя {}", newUserRequestDto);
        if (userExists(newUserRequestDto)) {
            throw new DuplicatedDataException("Пользователь " + newUserRequestDto + " уже имеется в базе данных");
        }
        User newUser = UserMapper.newUserRequestDtoToUser(newUserRequestDto);
        int id = getNextId();
        newUser.setId(id);
        userMap.put(id, newUser);
        log.info("InMemoryUserStorageImpl:addUser(): создан новый пользователь {}", newUser);
        return newUser;

    }

    private int getNextId() {
        currentId = currentId + 1;
        return currentId;
    }

    private boolean userExists(NewUserRequestDto newUserRequestDto) {
        for (User user : userMap.values()) {
            if (newUserRequestDto.getName().equals(user.getName())
                    && newUserRequestDto.getEmail().equals(user.getEmail()))
                return true;
        }
        return false;
    }
}
