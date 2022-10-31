package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.CustomPageRequest;
import ru.practicum.shareit.booking.dto.BookingDtoBodyUrl;
import ru.practicum.shareit.booking.dto.BookingDtoShow;
import ru.practicum.shareit.exceptions.ObjectNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;
    private final ItemRepository itemRepository;

    @Transactional
    public BookingDtoShow createBooking(long userId, BookingDtoBodyUrl bookingDtoCreate) {
        validateBooking(userId, bookingDtoCreate);
        Booking booking = BookingMapper.fromBookingDtoBodyUrl(bookingDtoCreate);
        booking.setItem(itemRepository.findById(bookingDtoCreate.getItemId()).orElseThrow(
                () -> new ObjectNotFoundException(String.format(
                        "Ошибка: вещь с id %d не существует", bookingDtoCreate.getItemId()))));
        booking.setBooker(UserMapper.fromUserDtoUpdate(userService.getUserById(userId)));
        booking.setStatus(BookingStatus.WAITING);
        booking = bookingRepository.save(booking);
        return BookingMapper.toBookingDtoShow(booking);
    }

    @Transactional
    public BookingDtoShow confirmBooking(long ownerId, long bookingId, Boolean approved) {
        userService.checkUserExistsById(ownerId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new ObjectNotFoundException(String.format(
                        "Ошибка: бронирования с id %d не существует", bookingId))
        );
        itemService.checkItemBelongToUser(ownerId, booking.getItem().getId());
        if (approved && booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new ValidationException(String.format(
                    "Ошибка: бронирование с id %d уже подтверждено", bookingId));
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        }
        if (!approved) {
            booking.setStatus(BookingStatus.REJECTED);
        }
        bookingRepository.save(booking);
        return BookingMapper.toBookingDtoShow(booking);
    }

    public BookingDtoShow getBookingById(long userId, long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new ObjectNotFoundException(String.format(
                        "Ошибка: бронирования с id %d не существует", bookingId))
        );
        if (userId != booking.getBooker().getId() && userId != booking.getItem().getOwner().getId()) {
            throw new ObjectNotFoundException(String.format(
                    "Ошибка: пользователь с id %d не имеет доступа к бронированию с id %d", userId, bookingId));
        }
        return BookingMapper.toBookingDtoShow(booking);
    }

    public List<BookingDtoShow> getAllBookingsByBooker(Long bookerId, String state, int from, int size) {
        userService.checkUserExistsById(bookerId);
        List<Booking> bookings;
        LocalDateTime now = LocalDateTime.now();
        Pageable pageable = CustomPageRequest.of(from, size);
        switch (state) {
            case "ALL":
                bookings = bookingRepository.findAllByBooker_IdOrderByStartDesc(bookerId, pageable);
                break;
            case "CURRENT":
                bookings = bookingRepository.findAllByStartBeforeAndEndAfterAndBooker_IdOrderByStartDesc(
                        now, now, bookerId, pageable);
                break;
            case "PAST":
                bookings = bookingRepository.findAllByEndBeforeAndBooker_IdOrderByStartDesc(now, bookerId, pageable);
                break;
            case "FUTURE":
                bookings = bookingRepository.findAllByStartAfterAndBooker_IdOrderByStartDesc(now, bookerId, pageable);
                break;
            case "WAITING":
                bookings = bookingRepository.findAllByStatusAndBooker_IdOrderByStartDesc(
                        BookingStatus.WAITING, bookerId, pageable);
                break;
            case "REJECTED":
                bookings = bookingRepository.findAllByStatusAndBooker_IdOrderByStartDesc(
                        BookingStatus.REJECTED, bookerId, pageable);
                break;
            default:
                throw new ValidationException("Unknown state: " + state);
        }
        return bookings.stream().map(BookingMapper::toBookingDtoShow).collect(Collectors.toList());
    }

    public List<BookingDtoShow> getAllBookingsByOwner(long ownerId, String state, int from, int size) {
        userService.checkUserExistsById(ownerId);
        List<Booking> bookings;
        LocalDateTime now = LocalDateTime.now();
        Pageable pageable = CustomPageRequest.of(from, size);
        switch (state) {
            case "ALL":
                bookings = bookingRepository.findAllByItem_Owner_IdOrderByStartDesc(ownerId, pageable);
                break;
            case "CURRENT":
                bookings = bookingRepository.findAllByStartBeforeAndEndAfterAndItem_Owner_IdOrderByStartDesc(
                        now, now, ownerId, pageable);
                break;
            case "PAST":
                bookings = bookingRepository.findAllByEndBeforeAndItem_Owner_IdOrderByStartDesc(now, ownerId, pageable);
                break;
            case "FUTURE":
                bookings = bookingRepository.findAllByStartAfterAndItem_Owner_IdOrderByStartDesc(now, ownerId, pageable);
                break;
            case "WAITING":
                bookings = bookingRepository.findAllByStatusAndItem_Owner_IdOrderByStartDesc(
                        BookingStatus.WAITING, ownerId, pageable);
                break;
            case "REJECTED":
                bookings = bookingRepository.findAllByStatusAndItem_Owner_IdOrderByStartDesc(
                        BookingStatus.REJECTED, ownerId, pageable);
                break;
            default:
                throw new ValidationException("Unknown state: " + state);
        }
        return bookings.stream().map(BookingMapper::toBookingDtoShow).collect(Collectors.toList());
    }

    private void validateBooking(long userId, BookingDtoBodyUrl bookingDtoBodyUrl) {
        Item item = itemRepository.findById(bookingDtoBodyUrl.getItemId()).orElseThrow(
                () -> new ObjectNotFoundException(String.format(
                        "Ошибка: вещь с id %d не существует", bookingDtoBodyUrl.getItemId())));
        userService.checkUserExistsById(userId);
        if (!item.getAvailable()) {
            throw new ValidationException(String.format(
                    "Ошибка: вещь с id %d занята", item.getId()));
        }
        if (bookingDtoBodyUrl.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Ошибка: указанное время старта бронирования прошло");
        }
        if (bookingDtoBodyUrl.getEnd().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Ошибка: указанное время окончания бронирования прошло");
        }
        if (bookingDtoBodyUrl.getStart().isAfter(bookingDtoBodyUrl.getEnd())) {
            throw new ValidationException("Ошибка: " +
                    "время окончания бронирования не может быть раньше времени начала бронирования");
        }
        if (item.getOwner().getId() == userId) {
            throw new ObjectNotFoundException("Ошибка: нельзя бронировать вещь у самого себя");
        }
    }
}
