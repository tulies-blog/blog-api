package com.tulies.blog.api.service;

import com.tulies.blog.api.beans.base.Pagination;
import com.tulies.blog.api.beans.dto.LoginDTO;
import com.tulies.blog.api.beans.qo.UserQO;
import com.tulies.blog.api.beans.vo.UserVO;
import com.tulies.blog.api.entity.User;

/**
 * @author 王嘉炀
 * @date 2019-10-12 00:07
 */
public interface UserService {
    Pagination<User> findList(Integer pageNum, Integer pageSize, UserQO userQO, String sorter);

    User findById(Integer id);

    void deleteById(Integer id);

    //上下线
    void changeStatus(Integer id, Integer status);


    UserVO findByUserToken(String userToken);


    //根据token获取用户信息
    UserVO queryUserInfo(String userToken);

    UserVO login(LoginDTO loginDTO);
}
