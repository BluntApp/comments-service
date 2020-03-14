package com.blunt.comments.dto;

import com.blunt.comments.serializer.ObjectIdSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import org.bson.types.ObjectId;

@Data
public class PostDto {
  @JsonSerialize(using = ObjectIdSerializer.class)
  private ObjectId posterId;
  @JsonSerialize(using = ObjectIdSerializer.class)
  private ObjectId contentId;
  private boolean isCommentPublic;
  private String posterName;
}
