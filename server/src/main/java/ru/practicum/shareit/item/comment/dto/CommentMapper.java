package ru.practicum.shareit.item.comment.dto;

import ru.practicum.shareit.item.comment.model.Comment;

public class CommentMapper {
    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto
                .builder()
                .authorName(comment.getAuthor().getName())
                .text(comment.getText())
                .created(comment.getCreated())
                .id(comment.getId())
                .build();
    }
}
