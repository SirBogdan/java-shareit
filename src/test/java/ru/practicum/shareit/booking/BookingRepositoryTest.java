package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.CustomPageRequest;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;

@DataJpaTest
public class BookingRepositoryTest {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    private User booker;
    private User owner;
    private LocalDateTime now;
    private Booking lastBooking;
    private Booking nextBooking;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        pageable = CustomPageRequest.of(0, 10);
        owner = userRepository.save(new User(1L, "user1", "user1@mail.ru"));
        booker = userRepository.save(new User(2L, "user2", "user2@mail.ru"));
        Item item = itemRepository.save(
                new Item(1L, "ВещьТест", "Описание вещи", true, owner, null));
        lastBooking = bookingRepository.save(
                new Booking(1L, now.minusDays(10), now.minusDays(5), item, booker, BookingStatus.APPROVED));
        nextBooking = bookingRepository.save(
                new Booking(2L, now.plusDays(5), now.plusDays(10), item, booker, BookingStatus.WAITING));
    }

    @Test
    void findAllByBooker_IdOrderByStartDesc() {
        List<Booking> actual = List.of(nextBooking, lastBooking);

        List<Booking> expected = bookingRepository.findAllByBooker_IdOrderByStartDesc(booker.getId(), pageable);

        assertThat(expected, hasItems());
        assertThat(expected.get(0), equalTo(actual.get(0)));
        assertThat(expected.get(1).getItem().getDescription(), equalTo(actual.get(1).getItem().getDescription()));
    }

    @Test
    void findAllByStartBeforeAndEndAfterAndBooker_IdOrderByStartDesc() {
        List<Booking> actual = Collections.singletonList(nextBooking);

        List<Booking> expected = bookingRepository.findAllByStartBeforeAndEndAfterAndBooker_IdOrderByStartDesc(
                now.plusDays(7), now.plusDays(7), booker.getId(), pageable);

        assertThat(expected, hasItems());
        assertThat(expected.get(0), equalTo(actual.get(0)));
        assertThat(expected.get(0).getItem().getName(), equalTo(actual.get(0).getItem().getName()));
    }

    @Test
    void findAllByEndBeforeAndBooker_IdOrderByStartDescTest() {
        List<Booking> actual = Collections.singletonList(lastBooking);

        List<Booking> expected = bookingRepository.findAllByEndBeforeAndBooker_IdOrderByStartDesc(
                now, booker.getId(), pageable);

        assertThat(expected, hasItems());
        assertThat(expected.get(0), equalTo(actual.get(0)));
        assertThat(expected.get(0).getItem().getName(), equalTo(actual.get(0).getItem().getName()));
    }

    @Test
    void findAllByStartAfterAndBooker_IdOrderByStartDescTest() {
        List<Booking> actual = Collections.singletonList(nextBooking);

        List<Booking> expected = bookingRepository.findAllByStartAfterAndBooker_IdOrderByStartDesc(
                now, booker.getId(), pageable);

        assertThat(expected, hasItems());
        assertThat(expected.get(0), equalTo(actual.get(0)));
        assertThat(expected.get(0).getItem().getName(), equalTo(actual.get(0).getItem().getName()));
    }

    @Test
    void findAllByStatusAndBooker_IdOrderByStartDescTest() {
        List<Booking> actual = Collections.singletonList(nextBooking);

        List<Booking> expected = bookingRepository.findAllByStatusAndBooker_IdOrderByStartDesc(
                BookingStatus.WAITING, booker.getId(), pageable);

        assertThat(expected, hasItems());
        assertThat(expected.get(0), equalTo(actual.get(0)));
        assertThat(expected.get(0).getItem().getName(), equalTo(actual.get(0).getItem().getName()));
    }

    @Test
    void findAllByItem_Owner_IdOrderByStartDescTest() {
        List<Booking> actual = List.of(nextBooking, lastBooking);

        List<Booking> expected = bookingRepository.findAllByItem_Owner_IdOrderByStartDesc(owner.getId(), pageable);

        assertThat(expected, hasItems());
        assertThat(expected.get(0), equalTo(actual.get(0)));
        assertThat(expected.get(1).getItem().getDescription(), equalTo(actual.get(1).getItem().getDescription()));
    }

    @Test
    void findAllByEndBeforeAndItem_Owner_IdOrderByStartDescTest() {
        List<Booking> actual = Collections.singletonList(lastBooking);

        List<Booking> expected = bookingRepository.findAllByEndBeforeAndItem_Owner_IdOrderByStartDesc(
                now, owner.getId(), pageable);

        assertThat(expected, hasItems());
        assertThat(expected.get(0), equalTo(actual.get(0)));
        assertThat(expected.get(0).getItem().getName(), equalTo(actual.get(0).getItem().getName()));
    }

    @Test
    void findAllByStatusAndItem_Owner_IdOrderByStartDescTest() {
        List<Booking> actual = Collections.singletonList(lastBooking);

        List<Booking> expected = bookingRepository.findAllByStatusAndItem_Owner_IdOrderByStartDesc(
                BookingStatus.APPROVED, owner.getId(), pageable);

        assertThat(expected, hasItems());
        assertThat(expected.get(0), equalTo(actual.get(0)));
        assertThat(expected.get(0).getItem().getName(), equalTo(actual.get(0).getItem().getName()));
    }
}
