package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDtoShow;
import ru.practicum.shareit.item.dto.ItemDtoCreate;
import ru.practicum.shareit.item.dto.ItemDtoShowBookings;
import ru.practicum.shareit.item.dto.ItemDtoUpdate;

import java.util.List;

public interface ItemService {

    ItemDtoCreate createItem(long userId, ItemDtoCreate itemDto);

    ItemDtoUpdate updateItem(long userId, ItemDtoUpdate itemDto);

    ItemDtoShowBookings getItemById(long userId, long itemId);

    List<ItemDtoShowBookings> getAllItemsByUser(long userId);

    void deleteItem(long userId, long itemId);

    List<ItemDtoUpdate> searchItems(String text);

    void checkItemBelongToUser(long userId, long itemId);

    CommentDtoShow createComment(long userId, long itemId, CommentDtoShow commentDtoShow);
}
