package ru.practicum.shareit.user.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserDtoCreate {
    private long id;
    private String name;
    private String email;
}
