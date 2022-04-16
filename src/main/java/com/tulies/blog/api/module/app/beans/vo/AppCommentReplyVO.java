package com.tulies.blog.api.module.app.beans.vo;

import com.tulies.blog.api.beans.vo.CommentReplyVO;
import com.tulies.blog.api.entity.CommentReply;
import com.tulies.blog.api.entity.CommentTopic;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;
import java.util.List;

@Data
public class AppCommentReplyVO {
    private Integer id;
    private String tid;
    private String content;
    private Integer grade;
    // 审核状态
    private Integer checkStatus;
    private Integer status;

    private String userid;
    private String username;
    // 用户邮箱 敏感信息 不要返回出去
//    private String email;
    // 用户个人主页
    private String website;

    private String relateUserid;
    private String relateUsername;

    // 评论点赞数
    private Integer diggCount = 0;
    // 用户行为，冗余字段
    private Integer isDigg = 0;

    private Integer parentid;
    private Integer rootid;

    private Date createTime;
    private Date updateTime;

    private CommentTopic topic;
    private CommentReply parent;
    private CommentReply root;

    // 总公共有多少回复数
    private int replyCount;
    private List<CommentReplyVO> replyList;


    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }


}
