package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDtoShort;

import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                                    @RequestBody ItemRequestDtoShort itemRequestDtoShort) {
        return itemRequestClient.createItemRequest(userId, itemRequestDtoShort);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemRequestsByUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestClient.getAllItemRequestsByUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequests(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @PositiveOrZero @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemRequestClient.getAllItemRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @PathVariable("requestId") Long requestId) {
        return itemRequestClient.getItemRequestById(userId, requestId);
    }
}
