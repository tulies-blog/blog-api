package com.tulies.blog.api.repository;

import com.tulies.blog.api.entity.CommentDigg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CommentDiggRepository extends JpaRepository<CommentDigg, Integer>, JpaSpecificationExecutor {
    CommentDigg findByUidAndCommentId(String uid, Integer commentId);

    void deleteByUidAndCommentId(String uid, Integer commentId);

}
