package com.tulies.blog.api.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommentEnum {

    /**
     * 上下线
     ****/
    REPLY_STATUS_DELETE(-1, "删除"),
    REPLY_STATUS_WILL_VERIFY(0, "待审核"),
    REPLY_STATUS_VERIFY_SUCCESS(1, "正常"),
    REPLY_STATUS_VERIFY_FAIL(2, "未通过"),

    /***主题审核类型****/
    TOPIC_POST_CHECK(0, "先发后审"),
    TOPIC_CHECK_POST(1, "先审后发"),

    /***主题状态****/
    TOPIC_STATUS_DELETE(-1, "删除"),
    TOPIC_STATUS_ONLINE(1, "正常"),
    TOPIC_STATUS_OFFLINE(2, "锁定"),

    ;
    private Integer code;
    private String desc;


}
