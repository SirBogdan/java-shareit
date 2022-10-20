package ru.practicum.shareit.item.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CommentDtoShow {
    private long id;
    private String text;
    String authorName;
    LocalDateTime created;
}
