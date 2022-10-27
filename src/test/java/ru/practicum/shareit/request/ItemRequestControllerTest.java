package ru.practicum.shareit.request;

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
import ru.practicum.shareit.item.dto.ItemDtoShowRequests;
import ru.practicum.shareit.request.dto.ItemRequestDtoShort;
import ru.practicum.shareit.request.dto.ItemRequestDtoShow;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemRequestService itemRequestService;
    ObjectMapper mapper = new ObjectMapper();
    private ItemRequest itemRequest;
    private final List<ItemDtoShowRequests> itemsDtoShowRequests = new ArrayList<>();

    @BeforeEach
    void setUp() {
        mapper.registerModule(new JavaTimeModule());
        User requestor = new User(2L, "user2", "user2@mail.ru");
        itemRequest = new ItemRequest(1L, "description1", requestor.getId(), LocalDateTime.now());
        itemsDtoShowRequests.add(
                new ItemDtoShowRequests(1L, "ВещьТест", "Описание вещи", true, 1L));
    }

    @Test
    void createItemRequestTest() throws Exception {
        ItemRequestDtoShort itemRequestDtoShort = ItemRequestMapper.toItemRequestDtoShort(itemRequest);
        when(itemRequestService.createItemRequest(any(), any())).thenReturn(itemRequestDtoShort);

        mockMvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDtoShort))
                        .header("X-Sharer-User-Id", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestDtoShort.getId()))
                .andExpect(jsonPath("$.description").value(itemRequestDtoShort.getDescription()));
    }

    @Test
    void getAllItemRequestsByUserTest() throws Exception {
        ItemRequestDtoShow itemRequestDtoShow = ItemRequestMapper
                .toItemRequestDtoShow(itemRequest, itemsDtoShowRequests);
        when(itemRequestService.getAllItemRequestsByUser(anyLong()))
                .thenReturn(Collections.singletonList(itemRequestDtoShow));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 2L))
                .andExpect(jsonPath("$[0].id").value(itemRequestDtoShow.getId()))
                .andExpect(jsonPath("$[0].description").value(itemRequestDtoShow.getDescription()))
                .andExpect(jsonPath("$[0].items[0].name")
                        .value(itemRequestDtoShow.getItems().get(0).getName()));
    }

    @Test
    void getAllItemRequestsTest() throws Exception {
        ItemRequestDtoShow itemRequestDtoShow = ItemRequestMapper
                .toItemRequestDtoShow(itemRequest, itemsDtoShowRequests);
        when(itemRequestService.getAllItemRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(Collections.singletonList(itemRequestDtoShow));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 2L)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(jsonPath("$[0].id").value(itemRequestDtoShow.getId()))
                .andExpect(jsonPath("$[0].description").value(itemRequestDtoShow.getDescription()))
                .andExpect(jsonPath("$[0].items[0].name")
                        .value(itemRequestDtoShow.getItems().get(0).getName()));
    }

    @Test
    void getItemRequestByIdTest() throws Exception {
        ItemRequestDtoShow itemRequestDtoShow = ItemRequestMapper
                .toItemRequestDtoShow(itemRequest, itemsDtoShowRequests);
        when(itemRequestService.getItemRequestById(any(), any())).thenReturn(itemRequestDtoShow);

        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestDtoShow.getId()))
                .andExpect(jsonPath("$.description").value(itemRequestDtoShow.getDescription()))
                .andExpect(jsonPath("$.items[0].name").value(itemRequestDtoShow.getItems().get(0).getName()));
    }
}
