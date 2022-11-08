package ru.practicum.shareit.booking;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoBodyUrl;
import ru.practicum.shareit.booking.dto.BookingDtoShow;
import ru.practicum.shareit.exceptions.ObjectNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.request.ItemRequest;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Transactional
@SpringBootTest
public class BookingServiceTest {
    private BookingService bookingService;
    private BookingRepository bookingRepository;
    private UserService userService;
    private ItemRepository itemRepository;
    private Item item;
    private User booker;
    private User owner;
    private Booking lastBooking;
    private Booking nextBooking;
    private BookingDtoBodyUrl nextBookingDtoBodyUrl;

    @BeforeEach
    void setUp() {
        bookingRepository = mock(BookingRepository.class);
        userService = mock(UserService.class);
        ItemService itemService = mock(ItemService.class);
        itemRepository = mock(ItemRepository.class);
        bookingService = new BookingServiceImpl(bookingRepository, userService, itemService, itemRepository);
        booker = new User(2L, "user2", "user2@mail.ru");
        owner = new User(1L, "user1", "user1@mail.ru");
        ItemRequest itemRequest = new ItemRequest(1L, "description1", booker.getId(), LocalDateTime.now());
        item = new Item(1L, "ВещьТест", "Описание вещи", true, owner, itemRequest);
        lastBooking = new Booking(1L, LocalDateTime.now().minusDays(10), LocalDateTime.now().minusDays(5),
                item, booker, BookingStatus.APPROVED);
        nextBooking = new Booking(2L, LocalDateTime.now().plusDays(5), LocalDateTime.now().plusDays(10),
                item, booker, BookingStatus.WAITING);
        nextBookingDtoBodyUrl = new BookingDtoBodyUrl(nextBooking.getItem().getId(),
                nextBooking.getStart(), nextBooking.getEnd());
    }

    @Test
    void createBookingTest() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        when(userService.getUserById(anyLong())).thenReturn(UserMapper.toUserDtoUpdate(booker));
        when(bookingRepository.save(any())).thenReturn(nextBooking);

        BookingDtoShow expected = bookingService.createBooking(booker.getId(), nextBookingDtoBodyUrl);

        assertThat(expected, notNullValue());
        assertThat(expected.getId(), equalTo(nextBooking.getId()));
        assertThat(expected.getStatus(), equalTo(nextBooking.getStatus()));
        assertThat(expected.getItem().getName(), equalTo(nextBooking.getItem().getName()));
    }

    @Test
    void validateBookingWithoutRepositoryTest() {
        ObjectNotFoundException e = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.createBooking(booker.getId(), nextBookingDtoBodyUrl));
        assertThat(e.getMessage(), equalTo("Ошибка: вещь с id 1 не существует"));
    }

    @Test
    void validateBookingAvailableTest() {
        item.setAvailable(false);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));

        assertThrows(ValidationException.class,
                () -> bookingService.createBooking(booker.getId(), nextBookingDtoBodyUrl));
    }

    @Test
    void validateBookingStartTest() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        nextBookingDtoBodyUrl.setStart(LocalDateTime.now().minusDays(3));

        assertThrows(ValidationException.class,
                () -> bookingService.createBooking(booker.getId(), nextBookingDtoBodyUrl));
    }

    @Test
    void validateBookingEndTest() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        nextBookingDtoBodyUrl.setEnd(LocalDateTime.now().minusDays(3));

        assertThrows(ValidationException.class,
                () -> bookingService.createBooking(booker.getId(), nextBookingDtoBodyUrl));
    }

    @Test
    void validateBookingStartAfterEndTest() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        nextBookingDtoBodyUrl.setStart(LocalDateTime.now().plusDays(10));
        nextBookingDtoBodyUrl.setEnd(LocalDateTime.now().plusDays(5));

        assertThrows(ValidationException.class,
                () -> bookingService.createBooking(booker.getId(), nextBookingDtoBodyUrl));
    }


    @Test
    void confirmBookingTest() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.ofNullable(nextBooking));
        when(bookingRepository.save(any())).thenReturn(nextBooking);

        BookingDtoShow expected = bookingService.confirmBooking(owner.getId(), nextBooking.getId(), true);

        assertThat(expected, notNullValue());
        assertThat(expected.getId(), equalTo(nextBooking.getId()));
        assertThat(expected.getStatus(), equalTo(BookingStatus.APPROVED));
        assertThat(expected.getItem().getName(), equalTo(nextBooking.getItem().getName()));
    }

    @Test
    void confirmBookingWithoutRepositoryTest() {
        ObjectNotFoundException e = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.confirmBooking(owner.getId(), nextBooking.getId(), true));
        assertThat(e.getMessage(), equalTo("Ошибка: бронирования с id 2 не существует"));
    }

    @Test
    void confirmBookingAlreadyAvailableTest() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.ofNullable(lastBooking));

        assertThrows(ValidationException.class,
                () -> bookingService.confirmBooking(owner.getId(), lastBooking.getId(), true));
    }

    @Test
    void confirmBookingRejectedTest() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.ofNullable(nextBooking));
        when(bookingRepository.save(any())).thenReturn(nextBooking);

        BookingDtoShow expected = bookingService.confirmBooking(owner.getId(), nextBooking.getId(), false);

        assertThat(expected, notNullValue());
        assertThat(expected.getId(), equalTo(nextBooking.getId()));
        assertThat(expected.getStatus(), equalTo(BookingStatus.REJECTED));
        assertThat(expected.getItem().getName(), equalTo(nextBooking.getItem().getName()));
    }

    @Test
    void getBookingByIdTest() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.ofNullable(nextBooking));

        BookingDtoShow expected = bookingService.getBookingById(booker.getId(), nextBooking.getId());

        assertThat(expected, notNullValue());
        assertThat(expected.getId(), equalTo(nextBooking.getId()));
        assertThat(expected.getStatus(), equalTo(nextBooking.getStatus()));
        assertThat(expected.getItem().getName(), equalTo(nextBooking.getItem().getName()));
    }

    @Test
    void getBookingByIdWithoutRepositoryTest() {
        ObjectNotFoundException e = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.getBookingById(nextBooking.getId(), booker.getId()));
        assertThat(e.getMessage(), equalTo("Ошибка: бронирования с id 2 не существует"));
    }

    @Test
    void getBookingByIdWrongUserTest() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.ofNullable(nextBooking));

        assertThrows(ObjectNotFoundException.class,
                () -> bookingService.getBookingById(3L, nextBooking.getId()));
    }

    @Test
    void getAllBookingsByBookerALLStatusTest() {
        when(bookingRepository.findAllByBooker_IdOrderByStartDesc(anyLong(), any()))
                .thenReturn(List.of(nextBooking, lastBooking));

        List<BookingDtoShow> expected = bookingService.getAllBookingsByBooker(
                booker.getId(), "ALL", 0, 10);

        assertThat(expected, hasItems());
        assertThat(expected.get(0).getId(), equalTo(nextBooking.getId()));
        assertThat(expected.get(1).getItem().getName(), equalTo(lastBooking.getItem().getName()));
    }

    @Test
    void getAllBookingsByBookerCURRENTStatusTest() {
        when(bookingRepository.findAllByStartBeforeAndEndAfterAndBooker_IdOrderByStartDesc(
                any(), any(), anyLong(), any()))
                .thenReturn(Collections.emptyList());

        List<BookingDtoShow> expected = bookingService.getAllBookingsByBooker(
                booker.getId(), "CURRENT", 0, 10);

        assertThat(expected.size(), equalTo(0));
    }

    @Test
    void getAllBookingsByBookerPASTStatusTest() {
        when(bookingRepository.findAllByEndBeforeAndBooker_IdOrderByStartDesc(
                any(), anyLong(), any()))
                .thenReturn(Collections.singletonList(lastBooking));

        List<BookingDtoShow> expected = bookingService.getAllBookingsByBooker(
                booker.getId(), "PAST", 0, 10);

        assertThat(expected.size(), equalTo(1));
        assertThat(expected.get(0).getItem().getName(), equalTo(lastBooking.getItem().getName()));
    }

    @Test
    void getAllBookingsByBookerFUTUREStatusTest() {
        when(bookingRepository.findAllByStartAfterAndBooker_IdOrderByStartDesc(
                any(), anyLong(), any()))
                .thenReturn(Collections.singletonList(nextBooking));

        List<BookingDtoShow> expected = bookingService.getAllBookingsByBooker(
                booker.getId(), "FUTURE", 0, 10);

        assertThat(expected.size(), equalTo(1));
        assertThat(expected.get(0).getItem().getName(), equalTo(nextBooking.getItem().getName()));
    }

    @Test
    void getAllBookingsByBookerWAITINGStatusTest() {
        nextBooking.setStatus(BookingStatus.WAITING);
        when(bookingRepository.findAllByStatusAndBooker_IdOrderByStartDesc(
                any(), anyLong(), any()))
                .thenReturn(Collections.singletonList(nextBooking));

        List<BookingDtoShow> expected = bookingService.getAllBookingsByBooker(
                booker.getId(), "WAITING", 0, 10);

        assertThat(expected.size(), equalTo(1));
        assertThat(expected.get(0).getItem().getName(), equalTo(nextBooking.getItem().getName()));
    }

    @Test
    void getAllBookingsByBookerREJECTEDStatusTest() {
        nextBooking.setStatus(BookingStatus.REJECTED);
        when(bookingRepository.findAllByStatusAndBooker_IdOrderByStartDesc(
                any(), anyLong(), any()))
                .thenReturn(Collections.singletonList(nextBooking));

        List<BookingDtoShow> expected = bookingService.getAllBookingsByBooker(
                booker.getId(), "REJECTED", 0, 10);

        assertThat(expected.size(), equalTo(1));
        assertThat(expected.get(0).getItem().getName(), equalTo(nextBooking.getItem().getName()));
    }

    @Test
    void getAllBookingsByBookerDEFAULTStatusTest() {
        ValidationException e = assertThrows(ValidationException.class,
                () -> bookingService.getAllBookingsByBooker(
                        booker.getId(), "DEFAULT", 0, 10));
        assertThat(e.getMessage(), equalTo("Unknown state: DEFAULT"));
    }

    @Test
    void getAllBookingsByOwnerALLStatusTest() {
        when(bookingRepository.findAllByItem_Owner_IdOrderByStartDesc(anyLong(), any()))
                .thenReturn(List.of(nextBooking, lastBooking));

        List<BookingDtoShow> expected = bookingService.getAllBookingsByOwner(
                booker.getId(), "ALL", 0, 10);

        assertThat(expected, hasItems());
        assertThat(expected.get(0).getId(), equalTo(nextBooking.getId()));
        assertThat(expected.get(1).getItem().getName(), equalTo(lastBooking.getItem().getName()));
    }

    @Test
    void getAllBookingsByOwnerCURRENTStatusTest() {
        when(bookingRepository.findAllByStartBeforeAndEndAfterAndItem_Owner_IdOrderByStartDesc(
                any(), any(), anyLong(), any()))
                .thenReturn(Collections.emptyList());

        List<BookingDtoShow> expected = bookingService.getAllBookingsByOwner(
                booker.getId(), "CURRENT", 0, 10);

        assertThat(expected.size(), equalTo(0));
    }

    @Test
    void getAllBookingsByOwnerPASTStatusTest() {
        when(bookingRepository.findAllByEndBeforeAndItem_Owner_IdOrderByStartDesc(
                any(), anyLong(), any()))
                .thenReturn(Collections.singletonList(lastBooking));

        List<BookingDtoShow> expected = bookingService.getAllBookingsByOwner(
                booker.getId(), "PAST", 0, 10);

        assertThat(expected.size(), equalTo(1));
        assertThat(expected.get(0).getItem().getName(), equalTo(lastBooking.getItem().getName()));
    }

    @Test
    void getAllBookingsByOwnerFUTUREStatusTest() {
        when(bookingRepository.findAllByStartAfterAndItem_Owner_IdOrderByStartDesc(
                any(), anyLong(), any()))
                .thenReturn(Collections.singletonList(nextBooking));

        List<BookingDtoShow> expected = bookingService.getAllBookingsByOwner(
                booker.getId(), "FUTURE", 0, 10);

        assertThat(expected.size(), equalTo(1));
        assertThat(expected.get(0).getItem().getName(), equalTo(nextBooking.getItem().getName()));
    }

    @Test
    void getAllBookingsByOwnerWAITINGStatusTest() {
        nextBooking.setStatus(BookingStatus.WAITING);
        when(bookingRepository.findAllByStatusAndItem_Owner_IdOrderByStartDesc(
                any(), anyLong(), any()))
                .thenReturn(Collections.singletonList(nextBooking));

        List<BookingDtoShow> expected = bookingService.getAllBookingsByOwner(
                booker.getId(), "WAITING", 0, 10);

        assertThat(expected.size(), equalTo(1));
        assertThat(expected.get(0).getItem().getName(), equalTo(nextBooking.getItem().getName()));
    }

    @Test
    void getAllBookingsByOwnerREJECTEDStatusTest() {
        nextBooking.setStatus(BookingStatus.REJECTED);
        when(bookingRepository.findAllByStatusAndItem_Owner_IdOrderByStartDesc(
                any(), anyLong(), any()))
                .thenReturn(Collections.singletonList(nextBooking));

        List<BookingDtoShow> expected = bookingService.getAllBookingsByOwner(
                booker.getId(), "REJECTED", 0, 10);

        assertThat(expected.size(), equalTo(1));
        assertThat(expected.get(0).getItem().getName(), equalTo(nextBooking.getItem().getName()));
    }

    @Test
    void getAllBookingsByOwnerDEFAULTStatusTest() {
        ValidationException e = assertThrows(ValidationException.class,
                () -> bookingService.getAllBookingsByOwner(
                        booker.getId(), "DEFAULT", 0, 10));
        assertThat(e.getMessage(), equalTo("Unknown state: DEFAULT"));
    }

}
