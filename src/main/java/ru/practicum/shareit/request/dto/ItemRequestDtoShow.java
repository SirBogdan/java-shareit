package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.item.dto.ItemDtoShowRequests;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ItemRequestDtoShow {
    private long id;
    private String description;
    private long requestorId;
    private LocalDateTime created;
    private List<ItemDtoShowRequests> items;
}
