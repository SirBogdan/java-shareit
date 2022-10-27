package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDtoBodyUrl;
import ru.practicum.shareit.booking.dto.BookingDtoShow;

import java.util.List;

public interface BookingService {
    BookingDtoShow createBooking(long userId, BookingDtoBodyUrl bookingDtoCreate);

    BookingDtoShow confirmBooking(long ownerId, long bookingId, Boolean approve);

    BookingDtoShow getBookingById(long userId, long bookingId);

    List<BookingDtoShow> getAllBookingsByBooker(Long bookerId, String state, int from, int size);

    List<BookingDtoShow> getAllBookingsByOwner(long ownerId, String state, int from, int size);
}
