package com.tulies.blog.api.beans.qo;

import lombok.Data;

@Data
public class UserQO {
    private Integer id;
    private String uid;
    private String nickname;
    private String username;
    private String status;
    private String password;

}
