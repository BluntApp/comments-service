package com.blunt.comments.repository;

import com.blunt.comments.entity.Comments;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CommentsCustomRepositoryImpl implements CommentsCustomRepository {

  private final MongoTemplate mongoTemplate;


  @Override
  public List<Comments> findAllByPosterIdAndPostRefIdAndReplyToId(ObjectId posterId,
      ObjectId postRefId, ObjectId replyToId) {
    Criteria criteria = new Criteria();
    criteria.andOperator(Criteria.where("commenterId").is(posterId),
        Criteria.where("postRefId").is(postRefId),
        Criteria.where("replyToId").in(replyToId,null));
    Query query = new Query(criteria);
//    addProjections(query);
    return mongoTemplate.find(query, Comments.class);
  }

  @Override
  public List<Comments> findAllByPostRefId(ObjectId postRefId) {
    Criteria criteria = new Criteria();
    criteria.orOperator(
        Criteria.where("postRefId").is(postRefId)
    );
    Query query = new Query(criteria);
//    addProjections(query);
    return mongoTemplate.find(query, Comments.class);
  }

  private void addProjections(Query query) {
    query.fields()
        .include("_id")
        .include("postId")
        .include("posterId")
        .include("postRefId")
        .include("posterName")
        .include("replyToId")
        .include("replyToCommentId")
        .include("comments")
        .include("commentedOn");
  }
}
