package com.tulies.blog.api.entity;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

/**
 * @ Author     ：王嘉炀
 * @ Date       ：Created in 00:43 2022/4/6
 * @ Description：评论点赞表
 */

@Data
@DynamicInsert
@EntityListeners(AuditingEntityListener.class) //监听器自动更新时间
@Entity
@Table(name = "comment_digg")
public class CommentDigg {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String uid;
    private Integer commentId;
    @CreatedDate
    private Date createTime;
}
