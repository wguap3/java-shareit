package ru.practicum.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.item.dto.CommentDto;
import ru.practicum.item.model.Comment;
import ru.practicum.item.model.Item;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(source = "author.name", target = "authorName")
    CommentDto toDto(Comment comment);

    default Comment fromDto(CommentDto commentDto, Item item, User author) {
        if (commentDto == null && item == null && author == null) {
            return null;
        }

        Comment comment = new Comment();
        if (commentDto != null) {
            comment.setText(commentDto.getText());
        }
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());

        return comment;
    }
}
