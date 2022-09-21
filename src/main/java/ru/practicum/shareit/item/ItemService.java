package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDtoCreate;
import ru.practicum.shareit.item.dto.ItemDtoUpdate;

import java.util.List;

public interface ItemService {

    ItemDtoCreate createItem(long userId, ItemDtoCreate itemDto);

    ItemDtoUpdate updateItem(long userId, ItemDtoUpdate itemDto);

    ItemDtoUpdate getItemById(long itemId);

    List<ItemDtoUpdate> getAllItemsByUser(long userId);

    void deleteItem(long userId, long itemId);

    List<ItemDtoUpdate> searchItems(String text);
}
