package ru.practicum.shareit.item.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ItemDtoShowRequests {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private long requestId;
}
