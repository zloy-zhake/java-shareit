package ru.practicum.shareit.user.storage.impl;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

public class InMemeoryUserStorageImpl implements UserStorage {
    private List<User> userList;

    @Override
    public User addUser(User newUser) {
        return null;
    }

}
