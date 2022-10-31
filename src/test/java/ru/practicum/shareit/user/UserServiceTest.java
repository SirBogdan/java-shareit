package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exceptions.ObjectNotFoundException;
import ru.practicum.shareit.user.dto.UserDtoCreate;
import ru.practicum.shareit.user.dto.UserDtoUpdate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceTest {
    private UserService userService;
    private UserRepository userRepository;
    private final List<User> users = new ArrayList<>();

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userService = new UserServiceImpl(userRepository);
        users.add(new User(1L, "user1", "user1@mail.ru"));
        users.add(new User(1L, "user2", "user2@mail.ru"));
        users.add(new User(3L, "user3", "user3@mail.ru"));
    }

    @Test
    void createUserTest() {
        User user = users.get(0);
        when(userRepository.save(any())).thenReturn(user);

        UserDtoCreate expected = userService.createUser(UserMapper.toUserDtoCreate(user));

        assertThat(expected, notNullValue());
        assertThat(expected.getId(), equalTo(user.getId()));
        assertThat(expected.getName(), equalTo(user.getName()));
        assertThat(expected.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    void updateUserTest() {
        User user1 = users.get(0);
        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(user1));
        User user2 = users.get(1);
        user2.setId(user1.getId());

        UserDtoUpdate expected = userService.updateUser(UserMapper.toUserDtoUpdate(user2));

        assertThat(expected, notNullValue());
        assertThat(expected.getId(), equalTo(1L));
        assertThat(expected.getName(), equalTo(user2.getName()));
        assertThat(expected.getEmail(), equalTo(user2.getEmail()));
    }

    @Test
    void updateUserWithNullFieldsTest() {
        User user = users.get(0);
        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(user));

        UserDtoUpdate expected = userService.updateUser(new UserDtoUpdate(1L, null, null));

        assertThat(expected, notNullValue());
        assertThat(expected.getId(), equalTo(1L));
        assertThat(expected.getName(), equalTo(user.getName()));
        assertThat(expected.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    void updateUserWithoutRepositoryTest() {
        User user = users.get(0);
        when(userRepository.save(any())).thenReturn(user);

        assertThrows(ObjectNotFoundException.class, () -> userService.updateUser(UserMapper.toUserDtoUpdate(user)));
    }

    @Test
    void getUserByIdTest() {
        User user = users.get(0);
        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(user));

        UserDtoUpdate expected = userService.getUserById(1L);

        assertThat(expected, notNullValue());
        assertThat(expected.getId(), equalTo(1L));
        assertThat(expected.getName(), equalTo(user.getName()));
        assertThat(expected.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    void getUserByIdWithoutRepositoryTest() {
        final ObjectNotFoundException e = assertThrows(ObjectNotFoundException.class,
                () -> userService.getUserById(1L));
        assertThat(e.getMessage(), equalTo("Ошибка: пользователя с id 1 не существует"));
    }

    @Test
    void checkUserExistsWithoutRepositoryTest() {
        final ObjectNotFoundException e = assertThrows(
                ObjectNotFoundException.class, () -> userService.checkUserExistsById(5L));
        assertThat(e.getMessage(), equalTo("Ошибка: пользователя с id 5 не существует"));
    }

    @Test
    void deleteUserTest() {
        userService.deleteUser(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }
}
