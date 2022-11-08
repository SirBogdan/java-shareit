package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDtoBodyUrl;
import ru.practicum.shareit.booking.dto.BookingDtoForItemShow;
import ru.practicum.shareit.booking.dto.BookingDtoShow;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.UserMapper;

public class BookingMapper {

    public static BookingDtoShow toBookingDtoShow(Booking booking) {
        return new BookingDtoShow(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                ItemMapper.toItemDtoShort(booking.getItem()),
                UserMapper.toUserDtoShort(booking.getBooker()),
                booking.getStatus()
        );
    }

    public static Booking fromBookingDtoBodyUrl(BookingDtoBodyUrl bookingDtoBodyUrl) {
        return new Booking(
                bookingDtoBodyUrl.getStart(),
                bookingDtoBodyUrl.getEnd()
        );
    }

    public static BookingDtoForItemShow toBookingDtoForItemShow(Booking booking) {
        if (booking == null) return null;
        return new BookingDtoForItemShow(
                booking.getId(),
                booking.getBooker().getId(),
                booking.getStart(),
                booking.getEnd()
        );
    }
}
