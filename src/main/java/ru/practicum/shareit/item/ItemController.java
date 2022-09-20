package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDtoCreate;
import ru.practicum.shareit.item.dto.ItemDtoUpdate;

import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDtoCreate createItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                    @RequestBody @Validated ItemDtoCreate itemDto) {
        return itemService.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDtoUpdate updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                    @RequestBody ItemDtoUpdate itemDto, @PathVariable("itemId") Long itemId) {
        itemDto.setId(itemId);
        return itemService.updateItem(userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDtoUpdate getItemById(@PathVariable("itemId") Long itemId) {
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public List<ItemDtoUpdate> getAllItemsByUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getAllItemsByUser(userId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable("itemId") Long itemId) {
        itemService.deleteItem(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemDtoUpdate> searchItems(@RequestParam String text) {
        return itemService.searchItems(text);
    }
}
