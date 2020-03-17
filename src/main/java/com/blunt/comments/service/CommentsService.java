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
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

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
    commentsDto.setPosterName(postDto.getPosterName());
    Comments comments = commentsMapper.commentsDtoToComments(commentsDto);
    if(Objects.nonNull(commentsDto.getReplyToCommentId())){
      comments.setReplyToId(getReplyToId(commentsDto.getReplyToCommentId()));
    }
    commentsRepository.save(comments);
    return new ResponseEntity<>(commentsMapper.commentsToCommentsDto(comments), HttpStatus.OK);
  }

  private ObjectId getReplyToId(ObjectId replyToCommentId) {
    Comments comments = commentsRepository.findCommentsById(replyToCommentId);
    return comments.getCommenterId();
  }

  private void validateReply(String bluntId, CommentsDto commentsDto, PostDto postDto) {
    if(!bluntId.equals(postDto.getPosterId().toString()) && (Objects.nonNull(commentsDto.getReplyToCommentId()))){
      Comments comments = commentsRepository.findCommentsById(commentsDto.getReplyToCommentId());
      if(!bluntId.equals(comments.getCommenterId().toString())){
        throw new BluntException(BluntConstant.NOT_AUTHORIZE_TO_COMMENTS,
            HttpStatus.UNAUTHORIZED.value(),
            HttpStatus.UNAUTHORIZED);
      }
    }
  }

  public ResponseEntity<Object> getComments(String bluntId, String postId) {
    PostDto postDto = validatePost(bluntId, postId);
    List<CommentsDto> commentsDtoList = new ArrayList<>();
    if(bluntId.equals(postDto.getPosterId().toString()) || postDto.isCommentPublic()){
      List<Comments> commentsList = commentsRepository.findAllByPostRefId(postDto.getContentId());
      filterComments(commentsList, new ObjectId(bluntId));
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
    commentsDtoList.sort(new Comparator<CommentsDto>() {
      @Override
      public int compare(CommentsDto o1, CommentsDto o2) {
        return o2.getCommentedOn().compareTo(o1.getCommentedOn());
      }
    });
    return new ResponseEntity<>(commentsDtoList, HttpStatus.OK);
  }

  private void filterComments(List<Comments> commentsList, ObjectId bluntId) {
    for(Comments comments: commentsList) {
      boolean myPostNotMyComment = bluntId.equals(comments.getPosterId()) && !(bluntId.equals(comments.getCommenterId())) ;
      boolean notMyPostAndNotMyComment = !bluntId.equals(comments.getPosterId()) && !bluntId.equals(comments.getCommenterId());
      boolean isPosterComment = comments.getPosterId().equals(comments.getCommenterId());
      if(myPostNotMyComment || (!isPosterComment && notMyPostAndNotMyComment) ){
        comments.setCommenterId(null);
      }
    }
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
    postDto.setPosterName((String) postDtoMap.get("posterName"));
    return postDto;
  }

  private ResponseEntity<Object> getPostDetails(String bluntId, String postId) {
    return publishServiceProxyClient.getPost(bluntId, postId);
  }
}


