package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.NewUserRequestDto;
import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {

    UserDto addUser(NewUserRequestDto newUserRequestDto);
}
