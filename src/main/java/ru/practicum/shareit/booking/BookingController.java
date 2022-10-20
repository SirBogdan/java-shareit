package ru.practicum.shareit.booking;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoBodyUrl;
import ru.practicum.shareit.booking.dto.BookingDtoShow;

import java.util.List;


@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDtoShow createBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                        @RequestBody @Validated BookingDtoBodyUrl bookingDtoCreate) {
        return bookingService.createBooking(userId, bookingDtoCreate);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoShow confirmBooking(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                         @PathVariable("bookingId") long bookingId, @RequestParam Boolean approved) {
        return bookingService.confirmBooking(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoShow getBookingById(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @PathVariable("bookingId") long bookingId) {
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDtoShow> getAllBookingsByBooker(
            @RequestHeader("X-Sharer-User-Id") long bookerId,
            @RequestParam(required = false, defaultValue = "ALL") String state) {
        return bookingService.getAllBookingsByBooker(bookerId, state);
    }

    @GetMapping("/owner")
    public List<BookingDtoShow> getAllBookingsByOwner(
            @RequestHeader("X-Sharer-User-Id") long ownerId,
            @RequestParam(required = false, defaultValue = "ALL") String state) {
        return bookingService.getAllBookingsByOwner(ownerId, state);
    }
}
