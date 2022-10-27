package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDtoCreate;
import ru.practicum.shareit.user.dto.UserDtoUpdate;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserDtoCreate createUser(@RequestBody @Validated UserDtoCreate userDtoCreate) {
        return userService.createUser(userDtoCreate);
    }

    @PatchMapping("/{userId}")
    public UserDtoUpdate updateUser(@RequestBody @Validated UserDtoUpdate userDtoUpdate,
                                    @PathVariable("userId") Long userId) {
        userDtoUpdate.setId(userId);
        return userService.updateUser(userDtoUpdate);
    }

    @GetMapping("/{userId}")
    public UserDtoUpdate getUserById(@PathVariable("userId") long userId) {
        return userService.getUserById(userId);
    }

    @GetMapping
    public List<UserDtoUpdate> getAllUsers() {
        return userService.getAllUsers();
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable("userId") long userId) {
        userService.deleteUser(userId);
    }
}
