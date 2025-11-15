package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.exceptions.DuplicatedDataException;
import ru.practicum.shareit.user.dto.NewUserRequestDto;
import ru.practicum.shareit.user.model.User;

public interface UserStorage {
    User addUser(NewUserRequestDto newUserRequestDto);

    User getUserById(int userId);

    void checkIfUserExists(NewUserRequestDto newUserRequestDto) throws DuplicatedDataException;
}
