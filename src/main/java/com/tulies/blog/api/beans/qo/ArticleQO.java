package com.tulies.blog.api.beans.qo;

import lombok.Data;

@Data
public class ArticleQO {
    private String id;
    private String title;
    private String status;
    private String tags;
    private Integer isOriginal;
    private Integer isShow;
    private Integer categoryId;
    private String categoryUrlName;

    // 不在这些ids中
    private String niIds;
    private String neId;
}
