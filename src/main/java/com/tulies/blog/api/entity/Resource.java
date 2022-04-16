package com.tulies.blog.api.entity;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.tulies.blog.api.utils.serializer.PicurlSerializer;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

/**
 * @author 王嘉炀
 * @date 2018/7/13 下午11:07
 */
@Data
@Entity
@DynamicUpdate
@DynamicInsert
@EntityListeners(AuditingEntityListener.class) //监听器自动更新时间
@Table(name = "resource_file")
public class Resource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Long size;
    private String contentType;
    private String relativePath;
    @Transient
    @JsonSerialize(using = PicurlSerializer.class)
    private String url;
    private String apply;
    @CreatedDate
    private Date createTime;
    private Date updateTime;

    public String getUrl(){
        return this.relativePath;
    }

}
