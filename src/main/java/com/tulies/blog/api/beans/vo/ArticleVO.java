package com.tulies.blog.api.beans.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.tulies.blog.api.entity.Category;
import com.tulies.blog.api.utils.serializer.PicurlSerializer;
import com.tulies.blog.api.utils.serializer.TagsSerializer;
import lombok.Data;

import java.util.Date;

@Data
public class ArticleVO {
    private String id;
    private String title;
    private Integer status;
    private String content;
    private String description;
    @JsonSerialize(using = PicurlSerializer.class)
    private String poster;
    @JsonSerialize(using = TagsSerializer.class)
    private String tags;
    private Integer categoryId;
    private Category category;
    private Integer isOriginal;
    private Integer isTop;
    private Integer isShow;
    private Integer commentCount;
    private Integer pv;
    private Integer diggCount = 0;
    private Integer isDigg = 0;
    //    private ArticleInteract userInteract;
    private Date createTime;
    private Date updateTime;
}
