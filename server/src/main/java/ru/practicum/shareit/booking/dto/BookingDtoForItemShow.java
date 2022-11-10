package ru.practicum.shareit.booking.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BookingDtoForItemShow {
    private long id;
    private long bookerId;
    private LocalDateTime start;
    private LocalDateTime end;
}
