package com.tulies.blog.api.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommEnum {

    /**
     * 上下线
     ****/
    STATUS_NEW_BUILD(0, "新建"),//新建
    STATUS_ONLINE(1, "上线"), //上线
    STATUS_OFFLINE(2, "下线/撤回/锁定"),//下线
    STATUS_DELETE(3, "删除"),//删除

    /***搜索***/
    CAN_SEARCHABLE(1, "可被搜索"),//可被搜索
    ;
    private Integer code;
    private String desc;


}
