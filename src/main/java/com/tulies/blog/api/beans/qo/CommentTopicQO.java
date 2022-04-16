package com.tulies.blog.api.beans.qo;

import lombok.Data;

@Data
public class CommentTopicQO {
    private Integer id;
    private String tid;
    private String title;
    private String url;
    private String type;
    private String status;
    private String checkMode;

}
