package ru.practicum.shareit.request;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * TODO Sprint add-item-requests.
 */

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor

public class ItemRequest {
    private long id;
    private String description;
    private long requestorId;
    private LocalDateTime created;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemRequest that = (ItemRequest) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
