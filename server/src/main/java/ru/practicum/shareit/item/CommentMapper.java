package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDtoShow;
import ru.practicum.shareit.user.User;

public class CommentMapper {

    public static CommentDtoShow toCommentDtoShow(Comment comment) {
        return new CommentDtoShow(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }

    public static Comment fromCommentDtoShow(CommentDtoShow commentDtoShow, Item item, User user) {
        return new Comment(
                commentDtoShow.getId(),
                commentDtoShow.getText(),
                item,
                user);
    }
}
