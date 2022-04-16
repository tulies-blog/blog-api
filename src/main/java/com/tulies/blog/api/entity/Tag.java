package com.tulies.blog.api.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.tulies.blog.api.utils.serializer.PicurlSerializer;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * @author 王嘉炀
 * @date 2019-10-08 22:16
 */

@Data
@Entity
@DynamicUpdate
@DynamicInsert
@EntityListeners(AuditingEntityListener.class) //监听器自动更新时间
@Table(name = "blog_tag")
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    // 唯一值
    @NotBlank
    private String name;
    private String description;
    @JsonSerialize(using = PicurlSerializer.class)
    private String cover;
    // 文章总数
    private Long articleCount = Long.valueOf(0);

    @Column(nullable = false, columnDefinition = "tinyint default 0")
    private Integer sort;
    @CreatedDate
    private Date createTime;
}
