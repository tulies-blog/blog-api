package com.tulies.blog.api.repository;

import com.tulies.blog.api.entity.ArticleDigg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ArticleDiggRepository extends JpaRepository<ArticleDigg, Integer>, JpaSpecificationExecutor {
    ArticleDigg findByUidAndArticleId(String uid, String articleId);

    void deleteByUidAndArticleId(String uid, String articleId);

}
