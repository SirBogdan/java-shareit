package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.user.User;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ItemDtoCreate {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private User owner;
    private long requestId;

    public ItemDtoCreate(long id, String name, String description, Boolean available, User owner) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.owner = owner;
    }
}
