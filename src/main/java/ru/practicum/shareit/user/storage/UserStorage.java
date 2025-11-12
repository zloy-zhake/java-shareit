package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

public interface UserStorage {
    User addUser(User newUser);
}
