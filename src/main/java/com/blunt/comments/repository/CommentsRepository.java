package com.blunt.comments.repository;

import com.blunt.comments.entity.Comments;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentsRepository extends MongoRepository<Comments, ObjectId>, CommentsCustomRepository{
  List<Comments> findAllByPostRefIdAndCommenterId(ObjectId postRefId, ObjectId commenterId);
  Comments findCommentsById(ObjectId id);
}
