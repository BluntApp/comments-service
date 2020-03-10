package com.blunt.comments.mapper;

import com.blunt.comments.dto.CommentsDto;
import com.blunt.comments.entity.Comments;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper
public interface CommentsMapper {

  CommentsDto commentsToCommentsDto(Comments comments);

  List<CommentsDto> commentsListToCommentsDtoList(List<Comments> comments);

  List<Comments> commentsDtoListToCommentsList(List<CommentsDto> commentsDto);

  Comments commentsDtoToComments(CommentsDto commentsDto);
}
