package ru.practicum.shareit.item;

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
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.CommentDtoShow;
import ru.practicum.shareit.item.dto.ItemDtoCreate;
import ru.practicum.shareit.item.dto.ItemDtoShowBookings;
import ru.practicum.shareit.item.dto.ItemDtoUpdate;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest(ItemController.class)
public class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemService itemService;
    private final ObjectMapper mapper = new ObjectMapper();
    private Item item;
    private User booker;
    private Comment comment;
    private ItemDtoShowBookings itemDtoShowBookings;

    @BeforeEach
    void setUp() {
        mapper.registerModule(new JavaTimeModule());
        User owner = new User(1L, "user1", "user1@mail.ru");
        booker = new User(2L, "user2", "user2@mail.ru");
        item = new Item(1L, "item", "itemDescription", true, owner, null);
        Booking lastBooking = new Booking(1L, LocalDateTime.now().minusDays(10), LocalDateTime.now().minusDays(5),
                item, booker, BookingStatus.APPROVED);
        comment = new Comment(1, "Комментарий", item, booker, LocalDateTime.now());
        itemDtoShowBookings = ItemMapper.toItemDtoShowBookings(item,
                BookingMapper.toBookingDtoForItemShow(lastBooking), null,
                Collections.singletonList(CommentMapper.toCommentDtoShow(comment)));
    }

    @Test
    void createItemTest() throws Exception {
        ItemDtoCreate itemDtoCreate = ItemMapper.toItemDtoCreate(item);
        when(itemService.createItem(anyLong(), any())).thenReturn(itemDtoCreate);

        mockMvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDtoCreate))
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDtoCreate.getId()))
                .andExpect(jsonPath("$.name").value(itemDtoCreate.getName()));
    }

    @Test
    void updateItemTest() throws Exception {
        ItemDtoUpdate itemDtoUpdate = ItemMapper.toItemDtoUpdate(item);
        when(itemService.updateItem(anyLong(), any())).thenReturn(itemDtoUpdate);

        mockMvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(itemDtoUpdate))
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDtoUpdate.getId()))
                .andExpect(jsonPath("$.name").value(itemDtoUpdate.getName()));
    }

    @Test
    void getItemByIdTest() throws Exception {
        when(itemService.getItemById(anyLong(), anyLong())).thenReturn(itemDtoShowBookings);

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDtoShowBookings.getId()))
                .andExpect(jsonPath("$.name").value(itemDtoShowBookings.getName()))
                .andExpect(jsonPath("$.lastBooking").exists());
    }

    @Test
    void getAllItemsByUserTest() throws Exception {
        when(itemService.getAllItemsByUser(anyLong(), anyInt(), anyInt()))
                .thenReturn(Collections.singletonList(itemDtoShowBookings));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemDtoShowBookings.getId()))
                .andExpect(jsonPath("$[0].name").value(itemDtoShowBookings.getName()))
                .andExpect(jsonPath("$[0].lastBooking").exists());
    }

    @Test
    void deleteItemTest() throws Exception {
        mockMvc.perform(delete("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void searchItemsTest() throws Exception {
        ItemDtoUpdate itemDtoUpdate = ItemMapper.toItemDtoUpdate(item);
        when(itemService.searchItems(anyString(), anyInt(), anyInt()))
                .thenReturn(Collections.singletonList(itemDtoUpdate));

        mockMvc.perform(get("/items/search")
                        .param("text", "текст поиска")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(itemDtoUpdate.getName()))
                .andExpect(jsonPath("$[0].description").value(itemDtoUpdate.getDescription()));
    }

    @Test
    void createCommentTest() throws Exception {
        CommentDtoShow commentDtoShow = CommentMapper.toCommentDtoShow(comment);
        when(itemService.createComment(anyLong(), anyLong(), any())).thenReturn(commentDtoShow);

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", booker.getId())
                        .content(mapper.writeValueAsString(commentDtoShow))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value(commentDtoShow.getText()))
                .andExpect(jsonPath("$.authorName").value(commentDtoShow.getAuthorName()));
    }
}
