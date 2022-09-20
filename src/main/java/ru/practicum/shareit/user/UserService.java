package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDtoCreate;
import ru.practicum.shareit.user.dto.UserDtoUpdate;

import java.util.List;

public interface UserService {

    UserDtoCreate createUser(UserDtoCreate userDtoCreate);

    UserDtoUpdate updateUser(UserDtoUpdate userDtoUpdate);

    UserDtoUpdate getUserById(long userId);

    List<UserDtoUpdate> getAllUsers();

    void deleteUser(long userId);
}
