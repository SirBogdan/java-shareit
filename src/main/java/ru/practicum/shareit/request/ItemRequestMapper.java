package ru.practicum.shareit.request;

import ru.practicum.shareit.item.dto.ItemDtoShowRequests;
import ru.practicum.shareit.request.dto.ItemRequestDtoShort;
import ru.practicum.shareit.request.dto.ItemRequestDtoShow;

import java.util.List;

public class ItemRequestMapper {

    public static ItemRequestDtoShort toItemRequestDtoShort(ItemRequest itemRequest) {
        return new ItemRequestDtoShort(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated()
        );
    }

    public static ItemRequestDtoShow toItemRequestDtoShow(ItemRequest itemRequest, List<ItemDtoShowRequests> items) {
        return new ItemRequestDtoShow(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getRequestorId(),
                itemRequest.getCreated(),
                items
        );
    }
}
