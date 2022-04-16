package com.tulies.blog.api.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

/**
 * @author 王嘉炀
 * @date 2022-05-05 22:05
 */
@Data
@Entity
@EntityListeners(AuditingEntityListener.class) //监听器自动更新时间
@Table(name = "blog_article_digg")
public class ArticleDigg {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    // 内容ID
    private String articleId;
    // 用户ID
    private String uid;
    @CreatedDate
    private Date createTime;
}
