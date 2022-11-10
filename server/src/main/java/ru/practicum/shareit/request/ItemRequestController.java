package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDtoShort;
import ru.practicum.shareit.request.dto.ItemRequestDtoShow;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @Autowired
    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    public ItemRequestDtoShort createItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @RequestBody ItemRequestDtoShort itemRequestDtoShort) {
        return itemRequestService.createItemRequest(userId, itemRequestDtoShort);
    }

    @GetMapping
    public List<ItemRequestDtoShow> getAllItemRequestsByUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestService.getAllItemRequestsByUser(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoShow> getAllItemRequests(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(name = "from", defaultValue = "0") Integer from,
            @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemRequestService.getAllItemRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoShow getItemRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @PathVariable("requestId") Long requestId) {
        return itemRequestService.getItemRequestById(userId, requestId);
    }
}
