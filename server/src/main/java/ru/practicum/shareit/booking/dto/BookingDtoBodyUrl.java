package ru.practicum.shareit.booking.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BookingDtoBodyUrl {
    private long itemId;
    private LocalDateTime start;
    private LocalDateTime end;
}
