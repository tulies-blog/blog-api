package com.tulies.blog.api.beans.vo;

import lombok.Data;

/**
 * @author 王嘉炀
 * @date 2018/7/24 下午1:18
 */
@Data
public class FileVO {
    private String baseUrl;
    private String url; // 临时目录可放预览地址，现网可放正式的发布地址
}
