package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.ObjectNotFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryInMem implements ItemRepository {

    private final HashMap<Long, Item> items = new HashMap<>();
    private long idCreator = 1;

    public Item createItem(Item item) {
        item.setId(idCreator);
        idCreator++;
        items.put(item.getId(), item);
        return item;
    }

    public Item updateItem(Item item) {
        checkItemExistsById(item.getId());
        Item currentItem = getItemById(item.getId());
        if (item.getName() == null) {
            item.setName(currentItem.getName());
        }
        if (item.getDescription() == null) {
            item.setDescription(currentItem.getDescription());
        }
        if (item.getAvailable() == null) {
            item.setAvailable(currentItem.getAvailable());
        }
        item.setOwner(currentItem.getOwner());
        items.put(item.getId(), item);
        return item;
    }

    public Item getItemById(long itemId) {
        checkItemExistsById(itemId);
        return items.get(itemId);
    }

    public List<Item> getAllItemsByUser(long userId) {
        return items.values()
                .stream()
                .filter(item -> item.getOwner().getId() == userId)
                .collect(Collectors.toList());
    }

    public void deleteItem(long itemId) {
        checkItemExistsById(itemId);
        items.remove(itemId);
    }

    public List<Item> searchItems(String text) {
        return items.values()
                .stream()
                .filter(item -> item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
    }

    private void checkItemExistsById(long itemId) {
        if (!items.containsKey(itemId)) {
            throw new ObjectNotFoundException(String.format("Ошибка: вещи с id %d не существует", itemId));
        }
    }
}
