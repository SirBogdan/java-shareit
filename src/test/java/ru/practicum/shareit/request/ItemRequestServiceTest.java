package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDtoShort;
import ru.practicum.shareit.request.dto.ItemRequestDtoShow;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Transactional
@SpringBootTest
public class ItemRequestServiceTest {
    private ItemRequestRepository itemRequestRepository;
    private ItemRepository itemRepository;
    private ItemRequestService itemRequestService;

    private ItemRequest itemRequest;
    private Item item;

    @BeforeEach
    void setUp() {
        itemRequestRepository = mock(ItemRequestRepository.class);
        UserService userService = mock(UserService.class);
        itemRepository = mock(ItemRepository.class);
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userService, itemRepository);
        User requestor = new User(2L, "user2", "user2@mail.ru");
        User owner = new User(1L, "user1", "user1@mail.ru");
        itemRequest = new ItemRequest(1L, "description1", requestor.getId(), LocalDateTime.now());
        item = new Item(1L, "ВещьТест", "Описание вещи", true, owner, itemRequest);
    }

    @Test
    void createItemRequestTest() {
        when(itemRequestRepository.save(any())).thenReturn(itemRequest);

        ItemRequestDtoShort expected = itemRequestService.createItemRequest(
                2L, new ItemRequestDtoShort(1, itemRequest.getDescription(), null));

        assertThat(1L, equalTo(expected.getId()));
        assertThat(expected.getCreated(), notNullValue());
    }

    @Test
    void getAllItemRequestsByUserTest() {
        when(itemRequestRepository.findAllByRequestorId(anyLong())).thenReturn(Collections.singletonList(itemRequest));
        when(itemRepository.findAllByRequest_Id(anyLong())).thenReturn(Collections.singletonList(item));
        ItemRequestDtoShow actual = ItemRequestMapper.toItemRequestDtoShow(
                itemRequest, Collections.singletonList(ItemMapper.toItemDtoShowRequests(item)));

        List<ItemRequestDtoShow> expected = itemRequestService.getAllItemRequestsByUser(2L);

        assertThat(expected, hasItems());
        assertThat(expected.get(0).getId(), equalTo(actual.getId()));
        assertThat(expected.get(0).getDescription(), equalTo(actual.getDescription()));
        assertThat(expected.get(0).getItems().get(0).getName(), equalTo(actual.getItems().get(0).getName()));
    }

    @Test
    void getAllItemRequestsTest() {
        when(itemRequestRepository.findAll(ArgumentMatchers.<Pageable>any()))
                .thenReturn(new PageImpl<>(Collections.singletonList(itemRequest)));
        when(itemRepository.findAllByRequest_Id(anyLong())).thenReturn(Collections.singletonList(item));
        ItemRequestDtoShow actual = ItemRequestMapper.toItemRequestDtoShow(
                itemRequest, Collections.singletonList(ItemMapper.toItemDtoShowRequests(item)));

        List<ItemRequestDtoShow> expected = itemRequestService.getAllItemRequests(1L, 0, 10);

        assertThat(expected, hasItems());
        assertThat(expected.get(0).getId(), equalTo(actual.getId()));
        assertThat(expected.get(0).getDescription(), equalTo(actual.getDescription()));
        assertThat(expected.get(0).getItems().get(0).getName(), equalTo(actual.getItems().get(0).getName()));
    }

    @Test
    void getItemRequestByIdTest() {
        when(itemRequestRepository.findById(any())).thenReturn(Optional.ofNullable(itemRequest));
        when(itemRepository.findAllByRequest_Id(anyLong())).thenReturn(Collections.singletonList(item));

        ItemRequestDtoShow expected = itemRequestService.getItemRequestById(2L, 1L);

        assertThat(expected.getId(), equalTo(itemRequest.getId()));
        assertThat(expected.getDescription(), equalTo(itemRequest.getDescription()));
        assertThat(expected.getItems().get(0).getName(), equalTo(item.getName()));
    }
}
