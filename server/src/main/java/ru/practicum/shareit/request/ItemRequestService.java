package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDtoShort;
import ru.practicum.shareit.request.dto.ItemRequestDtoShow;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDtoShort createItemRequest(Long userId, ItemRequestDtoShort itemRequestDtoShort);

    List<ItemRequestDtoShow> getAllItemRequestsByUser(long userId);

    List<ItemRequestDtoShow> getAllItemRequests(long userId, int from, int size);

    ItemRequestDtoShow getItemRequestById(Long userId, Long requestId);
}
