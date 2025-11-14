package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.NewUserRequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.impl.UserServiceImpl;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users")
@Slf4j
public class UserController {

    private final UserServiceImpl userService;

    @PostMapping
    public UserDto addUser(NewUserRequestDto newUserRequestDto) {
        log.info("UserController:addUser(): запрос на создание нового пользователя {}", newUserRequestDto);
        return userService.addUser(newUserRequestDto);
    }
}
