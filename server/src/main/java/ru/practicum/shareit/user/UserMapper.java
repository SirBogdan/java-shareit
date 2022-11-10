package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDtoCreate;
import ru.practicum.shareit.user.dto.UserDtoShort;
import ru.practicum.shareit.user.dto.UserDtoUpdate;

public class UserMapper {

    public static UserDtoCreate toUserDtoCreate(User user) {
        return new UserDtoCreate(user.getId(), user.getName(), user.getEmail());
    }

    public static User fromUserDtoCreate(UserDtoCreate userDtoCreate) {
        return new User(userDtoCreate.getName(), userDtoCreate.getEmail());
    }

    public static UserDtoUpdate toUserDtoUpdate(User user) {
        return new UserDtoUpdate(user.getId(), user.getName(), user.getEmail());
    }

    public static User fromUserDtoUpdate(UserDtoUpdate userDtoUpdate) {
        return new User(userDtoUpdate.getId(), userDtoUpdate.getName(), userDtoUpdate.getEmail());
    }

    public static UserDtoShort toUserDtoShort(User user) {
        return new UserDtoShort(user.getId(), user.getName());
    }
}
