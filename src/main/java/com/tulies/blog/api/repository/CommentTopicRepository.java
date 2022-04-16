package com.tulies.blog.api.repository;

import com.tulies.blog.api.entity.CommentTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentTopicRepository extends JpaRepository<CommentTopic,Integer>, JpaSpecificationExecutor {
    //上下线
    @Modifying
    @Query("update CommentTopic a set a.status=:status where a.id=:id")
    int changeStatus(@Param("id") Integer id, @Param("status") Integer status);

    CommentTopic findByTid(String tid);
}
