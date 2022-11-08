package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.ObjectNotFoundException;
import ru.practicum.shareit.user.dto.UserDtoCreate;
import ru.practicum.shareit.user.dto.UserDtoUpdate;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Transactional
    public UserDtoCreate createUser(UserDtoCreate userDtoCreate) {
        User user = UserMapper.fromUserDtoCreate(userDtoCreate);
        user = userRepository.save(user);
        return UserMapper.toUserDtoCreate(user);
    }

    @Transactional
    public UserDtoUpdate updateUser(UserDtoUpdate userDtoUpdate) {
        User user = UserMapper.fromUserDtoUpdate(userDtoUpdate);
        User userFromDb = userRepository.findById(user.getId()).orElseThrow(
                () -> new ObjectNotFoundException(String.format(
                        "Ошибка: пользователя с id %d не существует", user.getId())));
        if (user.getName() == null) {
            user.setName(userFromDb.getName());
        }
        if (user.getEmail() == null) {
            user.setEmail(userFromDb.getEmail());
        }
        userRepository.save(user);
        return UserMapper.toUserDtoUpdate(user);
    }

    public UserDtoUpdate getUserById(long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ObjectNotFoundException(String.format(
                        "Ошибка: пользователя с id %d не существует", userId)));
        return UserMapper.toUserDtoUpdate(user);
    }

    public List<UserDtoUpdate> getAllUsers() {
        return userRepository.findAll().stream().map(UserMapper::toUserDtoUpdate).collect(Collectors.toList());
    }

    @Transactional
    public void deleteUser(long userId) {
        userRepository.deleteById(userId);
    }

    public void checkUserExistsById(long userId) {
        userRepository.findById(userId).orElseThrow(
                () -> new ObjectNotFoundException(String.format(
                        "Ошибка: пользователя с id %d не существует", userId)));
    }
}
