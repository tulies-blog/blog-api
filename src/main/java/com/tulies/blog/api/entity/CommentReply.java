package com.tulies.blog.api.entity;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

/**
 * @author 王嘉炀
 * @date 2019-10-13 14:30
 */
@Data
@Entity
@DynamicUpdate
@DynamicInsert
@EntityListeners(AuditingEntityListener.class) //监听器自动更新时间
@Table(name = "comment_replied")
public class CommentReply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String tid;
    private String content;
    private Integer grade = 0;
    // 审核状态
//    @Column(nullable = false, name = "checkStatus", columnDefinition = "tinyint default 1")
//    private Integer checkStatus = 1;
    @Column(nullable = false, name = "status", columnDefinition = "tinyint default 1")
    private Integer status = 1;

    // 用户id
    private String userid;
    // 用户名
    private String username;
    // 用户邮箱
    private String email;
    // 用户个人主页
    private String website;

//    private String relateUserid;
//    private String relateUsername;

    // 评论点赞数
    private Integer diggCount = 0;

    private Integer parentid = 0;
    private Integer rootid = 0;
    @CreatedDate
    private Date createTime;
    private Date updateTime;
}
