package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.user.dto.UserDtoCreate;
import ru.practicum.shareit.user.dto.UserDtoUpdate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final Set<String> emails = new HashSet<>();
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDtoCreate createUser(UserDtoCreate userDtoCreate) {
        User user = UserMapper.fromUserDtoCreate(userDtoCreate);
        checkUserEmail(user.getEmail());
        emails.add(user.getEmail());
        user = userRepository.createUser(user);
        return UserMapper.toUserDtoCreate(user);
    }

    public UserDtoUpdate updateUser(UserDtoUpdate userDtoUpdate) {
        User user = UserMapper.fromUserDtoUpdate(userDtoUpdate);
        if (user.getName() == null) {
            user.setName(userRepository.getUserById(user.getId()).getName());
        }
        if (user.getEmail() != null) {
            checkUserEmail(user.getEmail());
            emails.remove(userRepository.getUserById(user.getId()).getEmail());
            emails.add(user.getEmail());
        } else {
            user.setEmail(userRepository.getUserById(user.getId()).getEmail());
        }
        userRepository.updateUser(user);
        return UserMapper.toUserDtoUpdate(user);
    }

    public UserDtoUpdate getUserById(long userId) {
        User user = userRepository.getUserById(userId);
        return UserMapper.toUserDtoUpdate(user);
    }

    public List<UserDtoUpdate> getAllUsers() {
        return userRepository.getAllUsers().stream().map(UserMapper::toUserDtoUpdate).collect(Collectors.toList());
    }

    public void deleteUser(long userId) {
        emails.remove(getUserById(userId).getEmail());
        userRepository.deleteUser(userId);
    }

    private void checkUserEmail(String email) {
        if (emails.contains(email)) {
            throw new ConflictException(
                    String.format("Ошибка: пользователь с email %s уже существует", email)
            );
        }
    }
}
