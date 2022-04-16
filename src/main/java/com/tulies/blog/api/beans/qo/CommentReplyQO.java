package com.tulies.blog.api.beans.qo;

import lombok.Data;

/**
 * @author 王嘉炀
 * @date 2019-10-13 14:30
 */
@Data
public class CommentReplyQO {
    private Integer id;
    private String tid;
    private String content;
    private Integer grade;
    // 审核状态
    private Integer checkStatus;
    private String status;

    private String userid;
    private String username;

//    private String relateUserid;
//    private String relateUsername;

    // 评论点赞数
    private Integer parentid;
    private Integer rootid;
}
