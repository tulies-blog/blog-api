package com.tulies.blog.api.service;

import com.tulies.blog.api.beans.base.Pagination;
import com.tulies.blog.api.beans.dto.ArticleDTO;
import com.tulies.blog.api.beans.qo.ArticleDiggQO;
import com.tulies.blog.api.beans.qo.ArticleQO;
import com.tulies.blog.api.beans.vo.ArticleVO;
import com.tulies.blog.api.entity.Article;
import com.tulies.blog.api.entity.ArticleDigg;

/**
 * @author 王嘉炀
 * @date 2019-10-08 22:29
 */
public interface ArticleService {
    Pagination<ArticleVO> findByQueryDSL(Integer pageNum, Integer pageSize, ArticleQO articleQo, String sorter);

    // 查询列表
    Pagination<ArticleVO> findList(Integer pageNum, Integer pageSize, ArticleQO articleQo, String sorter);

    long count(ArticleQO articleQo);

    Article findById(String id);

    void deleteById(String id);

    Article create(ArticleDTO articleForm);

    Article update(ArticleDTO articleForm);


    Article updateCommentCount(String id, int diff);

    void changeStatus(String id, Integer status);


    // 点赞
    ArticleDigg findDigg(String uid, String article);

    Pagination<ArticleDigg> findDiggList(ArticleDiggQO diggQO);

    void digg(String uid, String articleId, int isDigg);

    void updateDiggCount(String articleId, int diff);

    // 访问量+1
    void visit(String id);

}
