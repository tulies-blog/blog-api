package com.tulies.blog.api.beans.qo;

import lombok.Data;

import java.util.Date;

@Data
public class ResourceQO {
    private Long id;
    private String name;
    private Long size;
    private String contentType;
    private String relativePath;
    private String apply;
    private Date createTime;
    private Date updateTime;

}
