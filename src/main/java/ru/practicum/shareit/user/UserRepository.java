package ru.practicum.shareit.user;

import java.util.List;

public interface UserRepository {

    User createUser(User user);

    User updateUser(User user);

    User getUserById(long userId);

    List<User> getAllUsers();

    void deleteUser(long userId);

    void checkUserExistsById(long userId);
}
