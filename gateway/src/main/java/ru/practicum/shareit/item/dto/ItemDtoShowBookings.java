package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.dto.BookingDtoForItemShow;
import ru.practicum.shareit.request.ItemRequest;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ItemDtoShowBookings {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private long ownerId;
    private BookingDtoForItemShow lastBooking;
    private BookingDtoForItemShow nextBooking;
    private List<CommentDtoShow> comments;
    private ItemRequest request;
}
