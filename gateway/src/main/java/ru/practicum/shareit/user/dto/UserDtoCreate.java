package ru.practicum.shareit.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserDtoCreate {
    private long id;
    @NotBlank
    private String name;
    @NotBlank
    @Email
    private String email;
}
