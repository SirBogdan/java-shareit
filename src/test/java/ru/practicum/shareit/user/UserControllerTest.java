package ru.practicum.shareit.user;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDtoCreate;
import ru.practicum.shareit.user.dto.UserDtoUpdate;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;
    ObjectMapper mapper = new ObjectMapper();
    private User user;

    @BeforeEach
    void setUp() {
        mapper.registerModule(new JavaTimeModule());
        user = new User(1L, "user1", "user1@mail.ru");
    }

    @Test
    void createUserTest() throws Exception {
        UserDtoCreate userDtoCreate = UserMapper.toUserDtoCreate(user);
        when(userService.createUser(any())).thenReturn(userDtoCreate);

        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDtoCreate))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDtoCreate.getId()))
                .andExpect(jsonPath("$.name").value(userDtoCreate.getName()));
    }

    @Test
    void updateUserTest() throws Exception {
        UserDtoUpdate userDtoUpdate = UserMapper.toUserDtoUpdate(user);
        when(userService.updateUser(any())).thenReturn(userDtoUpdate);

        mockMvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userDtoUpdate))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDtoUpdate.getId()))
                .andExpect(jsonPath("$.name").value(userDtoUpdate.getName()));
    }

    @Test
    void getUserByIdTest() throws Exception {
        UserDtoUpdate userDtoUpdate = UserMapper.toUserDtoUpdate(user);
        when(userService.getUserById(anyLong())).thenReturn(userDtoUpdate);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDtoUpdate.getId()))
                .andExpect(jsonPath("$.name").value(userDtoUpdate.getName()));
    }

    @Test
    void getAllUsersTest() throws Exception {
        UserDtoUpdate userDtoUpdate = UserMapper.toUserDtoUpdate(user);
        when(userService.getAllUsers()).thenReturn(Collections.singletonList(userDtoUpdate));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(userDtoUpdate.getId()))
                .andExpect(jsonPath("$[0].name").value(userDtoUpdate.getName()));
    }

    @Test
    void deleteUserTest() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
    }
}
