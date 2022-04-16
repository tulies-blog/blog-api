package com.tulies.blog.api.repository;

import com.tulies.blog.api.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

public interface ArticleRepository extends JpaRepository<Article,String>, QuerydslPredicateExecutor<Article>, JpaSpecificationExecutor {

    //上下线
    @Modifying
    @Query("update Article a set a.status=:status where a.id=:id")
    int changeStatus(@Param("id") String id, @Param("status") Integer status);
}
