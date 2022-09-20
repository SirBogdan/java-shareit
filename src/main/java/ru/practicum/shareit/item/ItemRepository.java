package ru.practicum.shareit.item;

import java.util.List;

public interface ItemRepository {

    Item createItem(Item item);

    Item updateItem(Item item);

    Item getItemById(long itemId);

    List<Item> getAllItemsByUser(long userId);

    void deleteItem(long itemId);

    List<Item> searchItems(String text);
}
