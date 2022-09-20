package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.ItemDtoCreate;
import ru.practicum.shareit.item.dto.ItemDtoUpdate;
import ru.practicum.shareit.user.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    public ItemDtoCreate createItem(long userId, ItemDtoCreate itemDto) {
        userRepository.checkUserExistsById(userId);
        itemDto.setOwner(userRepository.getUserById(userId));
        Item item = itemRepository.createItem(ItemMapper.fromItemDtoCreate(itemDto));
        return ItemMapper.toItemDtoCreate(item);
    }

    public ItemDtoUpdate updateItem(long userId, ItemDtoUpdate itemDto) {
        userRepository.checkUserExistsById(userId);
        checkItemBelongToUser(userId, itemDto.getId());
        Item updatedItem = itemRepository.updateItem(ItemMapper.fromItemDtoUpdate(itemDto));
        return ItemMapper.toItemDtoUpdate(updatedItem);
    }

    public ItemDtoUpdate getItemById(long itemId) {
        Item item = itemRepository.getItemById(itemId);
        return ItemMapper.toItemDtoUpdate(item);
    }

    public List<ItemDtoUpdate> getAllItemsByUser(long userId) {
        userRepository.checkUserExistsById(userId);
        return itemRepository.getAllItemsByUser(userId).stream()
                .map(ItemMapper::toItemDtoUpdate)
                .collect(Collectors.toList());
    }

    public void deleteItem(long userId, long itemId) {
        userRepository.checkUserExistsById(userId);
        checkItemBelongToUser(userId, itemId);
        itemRepository.deleteItem(itemId);
    }

    public List<ItemDtoUpdate> searchItems(String text) {
        if (text.isEmpty() || text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.searchItems(text).stream()
                .map(ItemMapper::toItemDtoUpdate)
                .collect(Collectors.toList());
    }

    private void checkItemBelongToUser(long userId, long itemId) {
        if (itemRepository.getItemById(itemId).getOwner().getId() != userId) {
            throw new ObjectNotFoundException(
                    String.format("Ошибка: вещь с id %d не принадлежит пользователю с id %d",
                            itemId, userId)
            );
        }
    }
}
