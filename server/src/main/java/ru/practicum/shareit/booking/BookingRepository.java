package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBooker_IdOrderByStartDesc(long bookerId, Pageable pageable);

    List<Booking> findAllByStartBeforeAndEndAfterAndBooker_IdOrderByStartDesc(
            LocalDateTime start, LocalDateTime end, long bookerId, Pageable pageable);

    List<Booking> findAllByEndBeforeAndBooker_IdOrderByStartDesc(LocalDateTime end, long bookerId, Pageable pageable);

    List<Booking> findAllByStartAfterAndBooker_IdOrderByStartDesc(LocalDateTime start, long bookerId, Pageable pageable);

    List<Booking> findAllByStatusAndBooker_IdOrderByStartDesc(BookingStatus bookingStatus, long bookerId, Pageable pageable);

    List<Booking> findAllByItem_Owner_IdOrderByStartDesc(long ownerId, Pageable pageable);

    List<Booking> findAllByStartBeforeAndEndAfterAndItem_Owner_IdOrderByStartDesc(
            LocalDateTime start, LocalDateTime end, long ownerId, Pageable pageable);

    List<Booking> findAllByEndBeforeAndItem_Owner_IdOrderByStartDesc(LocalDateTime end, long ownerId, Pageable pageable);

    List<Booking> findAllByStartAfterAndItem_Owner_IdOrderByStartDesc(LocalDateTime start, long ownerId, Pageable pageable);

    List<Booking> findAllByStatusAndItem_Owner_IdOrderByStartDesc(BookingStatus bookingStatus, long ownerId, Pageable pageable);

    Booking findFirstByItem_IdAndItem_Owner_IdAndEndBeforeOrderByEndDesc(
            long itemId, long ownerId, LocalDateTime localDateTime);

    Booking findFirstByItem_IdAndItem_Owner_IdAndStartAfterOrderByEndDesc(
            long itemId, long ownerId, LocalDateTime localDateTime);

    List<Booking> findBookingsByBooker_IdAndItem_IdAndStatusAndEndBefore(
            long userId, long itemId, BookingStatus bookingStatus, LocalDateTime localDateTime);
}
