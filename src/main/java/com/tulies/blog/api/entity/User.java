package com.tulies.blog.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
 * @date 2019-10-13 14:30
 */
@Data
@Entity
@DynamicUpdate
@DynamicInsert
@EntityListeners(AuditingEntityListener.class) //监听器自动更新时间
@Table(name = "sso_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String uid;
    private String nickname;
    private String username;
    @JsonIgnore
    private String password;
    @JsonSerialize(using = PicurlSerializer.class)
    private String avatar;
    @JsonIgnore
    private String salt;
    private Integer status;
    @Column(name="admin",columnDefinition="int default 0")
    private Integer admin;
    private Date lastLoginTime;
    @CreatedDate
    private Date createTime;
    private Date updateTime;
}
