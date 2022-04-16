package com.tulies.blog.api.module.app.beans.qo;

import lombok.Data;

@Data
public class AppArticleQO {
    private String title;
    private String tags;
    private Integer categoryId;
    private String categoryUrlName;
    private String niIds;
}
