package com.tulies.blog.api.entity;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

/**
 * @author 王嘉炀
 * @date 2019-10-08 22:16
 */

@Data
@Entity
@DynamicUpdate
@DynamicInsert
@Table(name = "blog_category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String urlName;
    private Integer sort;
    private Long articleCount = Long.valueOf(0);
    private Integer parentId;
}
