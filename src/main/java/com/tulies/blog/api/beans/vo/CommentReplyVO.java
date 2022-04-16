package com.tulies.blog.api.beans.vo;

import com.tulies.blog.api.entity.CommentReply;
import com.tulies.blog.api.entity.CommentTopic;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

@Data
public class CommentReplyVO {
    private Integer id;
    private String tid;
    private String content;
    private Integer grade;
    // 审核状态
    private Integer checkStatus;
    private Integer status;

    private String userid;
    private String username;
    private String website;
    private String email;


    private Integer parentid;
    private Integer rootid;

    private Date createTime;
    private Date updateTime;

    private CommentTopic topic;
    private CommentReply parent;
    private CommentReply root;

    // 评论点赞数
    private Integer diggCount = 0;
    // 用户行为，冗余字段
    private Integer isDigg = 0;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }


}
