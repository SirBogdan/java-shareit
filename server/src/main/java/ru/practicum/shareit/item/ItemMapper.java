package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.dto.BookingDtoForItemShow;
import ru.practicum.shareit.item.dto.*;

import java.util.List;

public class ItemMapper {

    public static ItemDtoUpdate toItemDtoUpdate(Item item) {
        return new ItemDtoUpdate(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner(),
                item.getRequest()
        );
    }

    public static ItemDtoCreate toItemDtoCreate(Item item) {
        ItemDtoCreate itemDtoCreate = new ItemDtoCreate(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner()
        );
        if (item.getRequest() != null) itemDtoCreate.setRequestId(item.getRequest().getId());
        return itemDtoCreate;
    }

    public static Item fromItemDtoCreate(ItemDtoCreate itemDto) {
        return new Item(
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                itemDto.getOwner()
        );
    }

    public static ItemDtoShort toItemDtoShort(Item item) {
        return new ItemDtoShort(item.getId(), item.getName());
    }

    public static ItemDtoShowBookings toItemDtoShowBookings(
            Item item, BookingDtoForItemShow lastBooking,
            BookingDtoForItemShow nextBooking, List<CommentDtoShow> comments) {
        return new ItemDtoShowBookings(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner().getId(),
                lastBooking,
                nextBooking,
                comments,
                item.getRequest()
        );
    }

    public static ItemDtoShowRequests toItemDtoShowRequests(Item item) {
        return new ItemDtoShowRequests(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest().getId()
        );
    }
}
