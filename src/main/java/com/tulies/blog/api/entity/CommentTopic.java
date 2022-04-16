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
@Table(name = "comment_topic")
public class CommentTopic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String tid;
    private String title;
    private String url;
    private String type;
    private Integer repliedCount = 0;
    private Integer checkMode = 0;
    private Integer status = 1;
    @CreatedDate
    private Date createTime;
}
