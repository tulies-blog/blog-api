package com.tulies.blog.api.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.tulies.blog.api.utils.serializer.PicurlSerializer;
import com.tulies.blog.api.utils.serializer.TagsSerializer;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

/**
 * @author 王嘉炀
 * @date 2019-10-08 22:05
 */
@Data
@Entity
@DynamicUpdate
@DynamicInsert
@EntityListeners(AuditingEntityListener.class) //监听器自动更新时间
@Table(name = "blog_article")
public class Article {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @GenericGenerator(name = "custom_id", strategy = "com.tulies.blog.api.utils.generation.CustomGenerationId" )
    @GeneratedValue(generator = "custom_id")
    private String id;
    private String title;
//    private String slogan;
    private String description;
    @JsonSerialize(using = PicurlSerializer.class)
    private String poster;
    @JsonSerialize(using = TagsSerializer.class)
    private String tags;
    @Column(nullable=false,name="status",columnDefinition="tinyint default 1")
    private Integer status =1;
    private Integer categoryId;
    private String content;
    // 是否原创
    @Column(nullable=false,name="is_original",columnDefinition="tinyint default 1")
    private Integer isOriginal=1;
    @Column(nullable=false,name="is_top",columnDefinition="tinyint default 0")
    private Integer isTop=0;
    @Column(nullable=false,name="is_show",columnDefinition="tinyint default 1")
    private Integer isShow=1;
    @Column(nullable=false,name="digg_count",columnDefinition="tinyint default 0")
    private Integer diggCount=0;
    @Column(nullable=false,name="comment_count",columnDefinition="tinyint default 0")
    private Integer commentCount=0;
    @Column(nullable=false,name="pv",columnDefinition="tinyint default 1")
    private Integer pv=0;
    @CreatedDate
    private Date createTime;
    private Date updateTime;

}
