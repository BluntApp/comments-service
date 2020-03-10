package com.blunt.comments.service;

import com.blunt.comments.dto.CommentsDto;
import com.blunt.comments.dto.PostDto;
import com.blunt.comments.entity.Comments;
import com.blunt.comments.error.BluntException;
import com.blunt.comments.mapper.CommentsMapper;
import com.blunt.comments.proxy.PublishServiceProxyClient;
import com.blunt.comments.repository.CommentsRepository;
import com.blunt.comments.util.BluntConstant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentsService {

  private final CommentsRepository commentsRepository;
  private final CommentsMapper commentsMapper;
  private final PublishServiceProxyClient publishServiceProxyClient;

  public ResponseEntity<Object> addComments(String bluntId, CommentsDto commentsDto) {
    PostDto postDto = validatePost(bluntId, commentsDto.getPostId().toString());
    validateReply(bluntId, commentsDto, postDto);
    commentsDto.setCommentedOn(LocalDateTime.now());
    commentsDto.setCommenterId(new ObjectId(bluntId));
    commentsDto.setPosterId(postDto.getPosterId());
    commentsDto.setPostRefId(postDto.getContentId());
    Comments comments = commentsMapper.commentsDtoToComments(commentsDto);
    commentsRepository.save(comments);
    return new ResponseEntity<>(commentsMapper.commentsToCommentsDto(comments), HttpStatus.OK);
  }

  private void validateReply(String bluntId, CommentsDto commentsDto, PostDto postDto) {
    if(!bluntId.equals(postDto.getPosterId().toString()) &&
        !commentsDto.getReplyToId().equals(postDto.getPosterId())){
        throw new BluntException(BluntConstant.NOT_AUTHORIZE_TO_COMMENTS,
            HttpStatus.UNAUTHORIZED.value(),
            HttpStatus.UNAUTHORIZED);
    }
  }

  public ResponseEntity<Object> getComments(String bluntId, String postId) {
    PostDto postDto = validatePost(bluntId, postId);
    List<CommentsDto> commentsDtoList = new ArrayList<>();
    if(bluntId.equals(postDto.getPosterId().toString())){
      List<Comments> commentsList = commentsRepository.findAllByPostRefId(postDto.getContentId());
      filterPublicCommentersId(commentsList, bluntId);
      commentsDtoList = commentsMapper.commentsListToCommentsDtoList(commentsList);
    } else if(postDto.isCommentPublic()){
      List<Comments> commentsList = commentsRepository.findAllByPostRefId(postDto.getContentId());
      filterPublicCommentersId(commentsList, bluntId);
      commentsDtoList = commentsMapper.commentsListToCommentsDtoList(commentsList);
    } else{
      List<Comments> commentsByPoster = commentsRepository
          .findAllByPosterIdAndPostRefIdAndReplyToId(postDto.getPosterId(), postDto.getContentId(),
              new ObjectId(bluntId));
      List<Comments> commentsByBlunt = commentsRepository
          .findAllByPostRefIdAndCommenterId(postDto.getContentId(), new ObjectId(bluntId));
      commentsByBlunt.addAll(commentsByPoster);
      commentsDtoList = commentsMapper.commentsListToCommentsDtoList(commentsByBlunt);
    }
    return new ResponseEntity<>(commentsDtoList, HttpStatus.OK);
  }

  private void filterPublicCommentersId(List<Comments> commentsList, String bluntId) {
    commentsList.parallelStream().map(comments -> {
      if(!comments.getCommenterId().equals(new ObjectId(bluntId))){
        comments.setCommenterId(null);
      }
      return comments;
    });
  }

  private PostDto validatePost(String bluntId, String postId) {
    ResponseEntity<Object> postDtoResponseEntity = getPostDetails(bluntId, postId);
    LinkedHashMap<String, Object> postDtoMap = (LinkedHashMap) postDtoResponseEntity.getBody();
    if(ObjectUtils.isEmpty(postDtoMap)){
      throw new BluntException(BluntConstant.NOT_AUTHORIZE_TO_COMMENTS,
          HttpStatus.UNAUTHORIZED.value(),
          HttpStatus.UNAUTHORIZED);
    }
    PostDto postDto = new PostDto();
    postDto.setCommentPublic((Boolean) postDtoMap.get("commentPublic"));
    postDto.setContentId(new ObjectId((String) postDtoMap.get("contentId")));
    postDto.setPosterId(new ObjectId((String) postDtoMap.get("posterId")));
    return postDto;
  }

  private ResponseEntity<Object> getPostDetails(String bluntId, String postId) {
    return publishServiceProxyClient.getPost(bluntId, postId);
  }
}


