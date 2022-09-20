package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.ObjectNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
public class UserRepositoryInMem implements UserRepository {
    private final HashMap<Long, User> users = new HashMap<>();
    private long idCreator = 1;

    public User createUser(User user) {
        user.setId(idCreator);
        idCreator++;
        users.put(user.getId(), user);
        return user;
    }

    public User updateUser(User user) {
        checkUserExistsById(user.getId());
        users.put(user.getId(), user);
        return user;
    }

    public User getUserById(long userId) {
        checkUserExistsById(userId);
        return users.get(userId);
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public void deleteUser(long userId) {
        checkUserExistsById(userId);
        users.remove(userId);
    }

    public void checkUserExistsById(long userId) {
        if (!users.containsKey(userId)) {
            throw new ObjectNotFoundException(String.format
                    ("Ошибка: пользователя с id %d не существует", userId));
        }
    }
}
