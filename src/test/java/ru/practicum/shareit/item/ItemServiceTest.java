package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.CustomPageRequest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exceptions.ObjectNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.CommentDtoShow;
import ru.practicum.shareit.item.dto.ItemDtoCreate;
import ru.practicum.shareit.item.dto.ItemDtoShowBookings;
import ru.practicum.shareit.item.dto.ItemDtoUpdate;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@Transactional
@SpringBootTest
public class ItemServiceTest {
    ItemService itemService;
    private ItemRepository itemRepository;
    private UserService userService;
    private BookingRepository bookingRepository;
    private CommentRepository commentRepository;
    private ItemRequestRepository itemRequestRepository;
    private Item item;
    private User booker;
    private User owner;
    private Comment comment;
    private ItemRequest itemRequest;
    private Booking lastBooking;
    private Booking nextBooking;

    @BeforeEach
    void setUp() {
        itemRepository = mock(ItemRepository.class);
        userService = mock(UserService.class);
        userService = mock(UserService.class);
        bookingRepository = mock(BookingRepository.class);
        commentRepository = mock(CommentRepository.class);
        itemRequestRepository = mock(ItemRequestRepository.class);
        itemService = new ItemServiceImpl(
                itemRepository, userService, bookingRepository, commentRepository, itemRequestRepository);
        booker = new User(2L, "user2", "user2@mail.ru");
        owner = new User(1L, "user1", "user1@mail.ru");
        itemRequest = new ItemRequest(1L, "description1", booker.getId(), LocalDateTime.now());
        item = new Item(1L, "ВещьТест", "Описание вещи", true, owner, itemRequest);
        lastBooking = new Booking(1L, LocalDateTime.now().minusDays(10), LocalDateTime.now().minusDays(5),
                item, booker, BookingStatus.APPROVED);
        nextBooking = new Booking(2L, LocalDateTime.now().plusDays(5), LocalDateTime.now().plusDays(10),
                item, booker, BookingStatus.APPROVED);
        comment = new Comment(1, "Комментарий", item, booker, LocalDateTime.now());
    }

    @Test
    void createItemTest() {
        ItemDtoCreate itemDtoCreate = ItemMapper.toItemDtoCreate(item);
        when(userService.getUserById(anyLong())).thenReturn(UserMapper.toUserDtoUpdate(owner));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.ofNullable(itemRequest));
        when(itemRepository.save(any())).thenReturn(item);

        ItemDtoCreate expected = itemService.createItem(owner.getId(), itemDtoCreate);

        assertThat(expected, notNullValue());
        assertThat(expected.getId(), equalTo(item.getId()));
        assertThat(expected.getName(), equalTo(item.getName()));
        assertThat(expected.getDescription(), equalTo(item.getDescription()));
    }

    @Test
    void createItemWithoutRepositoryTest() {
        ItemDtoCreate itemDtoCreate = ItemMapper.toItemDtoCreate(item);
        when(userService.getUserById(anyLong())).thenReturn(UserMapper.toUserDtoUpdate(owner));
        final ObjectNotFoundException e = assertThrows(ObjectNotFoundException.class, () ->
                itemService.createItem(owner.getId(), itemDtoCreate));

        assertThat(e.getMessage(), equalTo("Ошибка: запрос с id 1 не существует"));
    }

    @Test
    void updateItemTest() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        when(itemRepository.save(any())).thenReturn(item);
        ItemDtoUpdate actual = new ItemDtoUpdate(1L, "Обновленное имя", "Обновленное описание",
                false, null, null);

        ItemDtoUpdate expected = itemService.updateItem(owner.getId(), actual);

        assertThat(expected, notNullValue());
        assertThat(expected.getId(), equalTo(actual.getId()));
        assertThat(expected.getName(), equalTo(actual.getName()));
        assertThat(expected.getDescription(), equalTo(actual.getDescription()));
    }

    @Test
    void updateItemWithNullFieldsTest() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        when(itemRepository.save(any())).thenReturn(item);
        ItemDtoUpdate actual = new ItemDtoUpdate(
                1L, null, null, null, null, null);

        ItemDtoUpdate expected = itemService.updateItem(owner.getId(), actual);

        assertThat(expected, notNullValue());
        assertThat(expected.getId(), equalTo(item.getId()));
        assertThat(expected.getName(), equalTo(item.getName()));
        assertThat(expected.getDescription(), equalTo(item.getDescription()));
    }

    @Test
    void getItemByIdTest() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        when(bookingRepository.findFirstByItem_IdAndItem_Owner_IdAndEndBeforeOrderByEndDesc(
                anyLong(), anyLong(), any())).thenReturn(lastBooking);
        when(bookingRepository.findFirstByItem_IdAndItem_Owner_IdAndStartAfterOrderByEndDesc(
                anyLong(), anyLong(), any())).thenReturn(nextBooking);
        when(commentRepository.findAllByItem_Id(anyInt())).thenReturn(Collections.singletonList(comment));

        ItemDtoShowBookings expected = itemService.getItemById(owner.getId(), item.getId());

        assertThat(expected, notNullValue());
        assertThat(expected.getId(), equalTo(item.getId()));
        assertThat(expected.getName(), equalTo(item.getName()));
        assertThat(expected.getDescription(), equalTo(item.getDescription()));
    }

    @Test
    void getAllItemsByUserTest() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        when(bookingRepository.findFirstByItem_IdAndItem_Owner_IdAndEndBeforeOrderByEndDesc(
                anyLong(), anyLong(), any())).thenReturn(lastBooking);
        when(bookingRepository.findFirstByItem_IdAndItem_Owner_IdAndStartAfterOrderByEndDesc(
                anyLong(), anyLong(), any())).thenReturn(nextBooking);
        when(commentRepository.findAllByItem_Id(anyInt())).thenReturn(Collections.singletonList(comment));
        Pageable pageable = CustomPageRequest.of(0, 10);
        when(itemRepository.findItemsByOwner_Id(owner.getId(), pageable))
                .thenReturn(Collections.singletonList(item));
        ItemDtoShowBookings actual = ItemMapper.toItemDtoShowBookings(item,
                BookingMapper.toBookingDtoForItemShow(lastBooking),
                BookingMapper.toBookingDtoForItemShow(nextBooking),
                Collections.singletonList(CommentMapper.toCommentDtoShow(comment)));

        List<ItemDtoShowBookings> expected = itemService.getAllItemsByUser(1L, 0, 10);

        assertThat(expected, hasItems());
        assertThat(expected.get(0).getId(), equalTo(actual.getId()));
        assertThat(expected.get(0).getDescription(), equalTo(actual.getDescription()));
    }

    @Test
    void deleteItem() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));

        itemService.deleteItem(owner.getId(), item.getId());

        verify(itemRepository, times(1)).findById(item.getId());
    }

    @Test
    void searchItemsTest() {
        Pageable pageable = CustomPageRequest.of(0, 10);
        when(itemRepository.findItemsByText("ние вещ", pageable))
                .thenReturn(Collections.singletonList(item));
        ItemDtoUpdate actual = ItemMapper.toItemDtoUpdate(item);

        List<ItemDtoUpdate> expected = itemService.searchItems("ние вещ", 0, 10);

        assertThat(expected, hasItems());
        assertThat(expected.get(0).getId(), equalTo(actual.getId()));
        assertThat(expected.get(0).getDescription(), equalTo(actual.getDescription()));
    }

    @Test
    void searchItemsWithEmptyTextTest() {
        List<ItemDtoUpdate> expected = itemService.searchItems("", 0, 10);

        assertThat(expected, emptyCollectionOf(ItemDtoUpdate.class));
    }


    @Test
    void createCommentTest() {
        when(userService.getUserById(anyLong())).thenReturn(UserMapper.toUserDtoUpdate(booker));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        when(bookingRepository.findBookingsByBooker_IdAndItem_IdAndStatusAndEndBefore(anyLong(), anyLong(), any(), any()))
                .thenReturn(List.of(nextBooking, lastBooking));
        when(commentRepository.save(any())).thenReturn(comment);


        CommentDtoShow expected = itemService.createComment(
                booker.getId(), item.getId(), CommentMapper.toCommentDtoShow(comment));

        assertThat(expected, notNullValue());
        assertThat(expected.getText(), equalTo(comment.getText()));
        assertThat(expected.getAuthorName(), equalTo(comment.getAuthor().getName()));
    }

    @Test
    void createCommentWithEmptyTextTest() {
        when(userService.getUserById(anyLong())).thenReturn(UserMapper.toUserDtoUpdate(booker));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        CommentDtoShow actual = CommentMapper.toCommentDtoShow(comment);
        actual.setText("");

        assertThrows(ValidationException.class,
                () -> itemService.createComment(booker.getId(), item.getId(), actual));
    }

    @Test
    void createCommentWithNoBookingTest() {
        when(userService.getUserById(anyLong())).thenReturn(UserMapper.toUserDtoUpdate(booker));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        when(bookingRepository.findBookingsByBooker_IdAndItem_IdAndStatusAndEndBefore(anyLong(), anyLong(), any(), any()))
                .thenReturn(Collections.emptyList());


        assertThrows(ValidationException.class,
                () -> itemService.createComment(booker.getId(), item.getId(), CommentMapper.toCommentDtoShow(comment)));
    }

    @Test
    void checkItemBelongToUserTest() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        final ObjectNotFoundException e = assertThrows(
                ObjectNotFoundException.class, () -> itemService.checkItemBelongToUser(3L, 1L));
        assertThat(e.getMessage(), equalTo("Ошибка: вещь с id 1 не принадлежит пользователю с id 3"));
    }

    @Test
    void checkItemBelongToUserWithoutRepositoryTest() {
        final ObjectNotFoundException e = assertThrows(
                ObjectNotFoundException.class, () -> itemService.checkItemBelongToUser(3L, 1L));
        assertThat(e.getMessage(), equalTo("Ошибка: вещь с id 1 не существует"));
    }
}
