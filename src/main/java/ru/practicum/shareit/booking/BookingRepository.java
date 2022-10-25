package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBooker_IdOrderByStartDesc(long bookerId);

    List<Booking> findAllByStartBeforeAndEndAfterAndBooker_IdOrderByStartDesc(
            LocalDateTime start, LocalDateTime end, long bookerId);

    List<Booking> findAllByEndBeforeAndBooker_IdOrderByStartDesc(LocalDateTime end, long bookerId);

    List<Booking> findAllByStartAfterAndBooker_IdOrderByStartDesc(LocalDateTime start, long bookerId);

    List<Booking> findAllByStatusAndBooker_IdOrderByStartDesc(BookingStatus bookingStatus, long bookerId);

    List<Booking> findAllByItem_Owner_IdOrderByStartDesc(long ownerId);

    List<Booking> findAllByStartBeforeAndEndAfterAndItem_Owner_IdOrderByStartDesc(
            LocalDateTime start, LocalDateTime end, long ownerId);

    List<Booking> findAllByEndBeforeAndItem_Owner_IdOrderByStartDesc(LocalDateTime end, long ownerId);

    List<Booking> findAllByStartAfterAndItem_Owner_IdOrderByStartDesc(LocalDateTime start, long ownerId);

    List<Booking> findAllByStatusAndItem_Owner_IdOrderByStartDesc(BookingStatus bookingStatus, long ownerId);

    Booking findFirstByItem_IdAndItem_Owner_IdAndEndBeforeOrderByEndDesc(
            long itemId, long ownerId, LocalDateTime localDateTime);

    Booking findFirstByItem_IdAndItem_Owner_IdAndStartAfterOrderByEndDesc(
            long itemId, long ownerId, LocalDateTime localDateTime);

    List<Booking> findBookingsByBooker_IdAndItem_IdAndStatusAndEndBefore(
            long userId, long itemId, BookingStatus bookingStatus, LocalDateTime localDateTime);
}
