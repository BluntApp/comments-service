package com.blunt.comments.controller;

import com.blunt.comments.dto.CommentsDto;
import com.blunt.comments.service.CommentsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
public class CommentsController {

  private final CommentsService commentsService;

  @GetMapping("test")
  public String followTest() {
    return "Success";
  }

  @PostMapping("add")
  public ResponseEntity<Object> addComments(
      @RequestHeader(name = "BLUNT-ID", required = true) String bluntId,
      @RequestBody CommentsDto commentsDto){
      return commentsService.addComments(bluntId, commentsDto);
  }

  @GetMapping("fetch/{postId}")
  public ResponseEntity<Object> getComments(
      @RequestHeader(name = "BLUNT-ID", required = true) String bluntId,
      @PathVariable String postId){
    return commentsService.getComments(bluntId, postId);
  }

  @PostMapping("replies")
  public ResponseEntity<String> getComments(
      @RequestHeader(name = "BLUNT-ID", required = true) String bluntId,
      @RequestBody CommentsDto commentsDto){
    return commentsService.getReplyToComments(bluntId, commentsDto);
  }


}
