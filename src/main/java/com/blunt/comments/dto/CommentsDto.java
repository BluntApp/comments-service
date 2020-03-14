package com.blunt.comments.dto;

import com.blunt.comments.serializer.ObjectIdSerializer;
import com.blunt.comments.type.Status;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.time.LocalDateTime;
import lombok.Data;
import org.bson.types.ObjectId;

@Data
public class CommentsDto {

  @JsonSerialize(using = ObjectIdSerializer.class)
  private ObjectId id;
  @JsonSerialize(using = ObjectIdSerializer.class)
  private ObjectId postId;
  @JsonSerialize(using = ObjectIdSerializer.class)
  private ObjectId posterId;
  @JsonSerialize(using = ObjectIdSerializer.class)
  private ObjectId postRefId;
  @JsonSerialize(using = ObjectIdSerializer.class)
  private ObjectId commenterId;
  @JsonSerialize(using = ObjectIdSerializer.class)
  private ObjectId replyToId;
  @JsonSerialize(using = ObjectIdSerializer.class)
  private ObjectId replyToCommentId;
  private String comments;
  private String posterName;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime commentedOn;
}
