package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.dto.NewUserRequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public class UserMapper {

    public static UserDto userToUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        return userDto;
    }

    public static User newUserRequestDtoToUser(NewUserRequestDto newUserRequestDto) {
        User user = new User();
        user.setName(newUserRequestDto.getName());
        user.setEmail(newUserRequestDto.getEmail());
        return user;
    }

}
