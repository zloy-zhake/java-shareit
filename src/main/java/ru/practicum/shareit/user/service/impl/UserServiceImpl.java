package ru.practicum.shareit.user.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.UserNotValidException;
import ru.practicum.shareit.user.dto.NewUserRequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserStorage;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    public UserServiceImpl(
            @Qualifier("inMemoryUserStorageImpl") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public UserDto addUser(NewUserRequestDto newUserRequestDto) {
        log.info("UserServiceImpl:addUser(): запрос на создание нового пользователя {}", newUserRequestDto);
        validateNewUserRequestDto(newUserRequestDto);
        User createdUser = userStorage.addUser(newUserRequestDto);
        log.info("UserServiceImpl:addUser(): создан новый пользователь {}", createdUser);
        return UserMapper.userToUserDto(createdUser);
    }

    private void validateNewUserRequestDto(NewUserRequestDto newUserRequestDto) {
        if (newUserRequestDto.getName() == null || newUserRequestDto.getName().isBlank()) {
            throw new UserNotValidException("Имя пользователя не может быть пустым или null");
        }
        if (newUserRequestDto.getEmail() == null || newUserRequestDto.getEmail().isBlank()) {
            throw new UserNotValidException("Email не может быть пустым или null");
        }
        if (!newUserRequestDto.getEmail().contains("@")) {
            throw new UserNotValidException("Email должен содержать @");
        }
    }
}
