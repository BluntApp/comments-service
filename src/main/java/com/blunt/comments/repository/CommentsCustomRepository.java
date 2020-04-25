package com.blunt.comments.repository;

import com.blunt.comments.entity.Comments;
import java.util.List;
import org.bson.types.ObjectId;

public interface CommentsCustomRepository {
  List<Comments> findAllByPosterIdAndPostRefIdAndReplyToId(ObjectId posterId, ObjectId postRefId, ObjectId replyToId);
  List<Comments> findAllByPostRefId(ObjectId postRefId);
}
