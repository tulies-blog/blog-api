package com.tulies.blog.api.service.impl;

import com.tulies.blog.api.beans.base.Pagination;
import com.tulies.blog.api.beans.dto.LoginDTO;
import com.tulies.blog.api.beans.qo.UserQO;
import com.tulies.blog.api.beans.vo.UserVO;
import com.tulies.blog.api.config.constant.RedisConstant;
import com.tulies.blog.api.converter.PageResultConverter;
import com.tulies.blog.api.entity.User;
import com.tulies.blog.api.enums.ResultEnum;
import com.tulies.blog.api.exception.AppException;
import com.tulies.blog.api.repository.UserRepository;
import com.tulies.blog.api.service.UserService;
import com.tulies.blog.api.utils.CommUtil;
import com.tulies.blog.api.utils.KeyUtil;
import com.tulies.blog.api.utils.MD5Util;
import com.tulies.blog.api.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author 王嘉炀
 * @date 2019-10-12 00:07
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 根据id查询文章
     *
     * @param id
     * @return
     */
    @Override
    public User findById(Integer id) {
        Optional<User> record = userRepository.findById(id);
        if (!record.isPresent()) {
            return null;
        }

        return record.get();
    }

    /**
     * 根据id删除
     *
     * @param id
     */
    @Override
    @Transactional
    public void deleteById(Integer id) {
        userRepository.changeStatus(id, -1);
    }


    @Override
    @Transactional
    public void changeStatus(Integer id, Integer status) {
        userRepository.changeStatus(id, status);
    }

    @Override
    public Pagination<User> findList(Integer pageNum, Integer pageSize, UserQO userQO, String sorter) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        if (StringUtils.isNotBlank(sorter)) {
            sort = CommUtil.formatSorter(sorter);
        }
        Pageable pageable = PageRequest.of(pageNum, pageSize, sort);

        Specification<User> specification = (Specification<User>) (root, criteriaQuery, criteriaBuilder) -> {

            List<Predicate> predicateList = new ArrayList<>();
            //根据id 查询
            if (userQO.getId() != null) {
                predicateList.add(criteriaBuilder.equal(root.get("id").as(Long.class), userQO.getId()));
            }

            //根据uid 查询
            if (userQO.getUid() != null) {
                predicateList.add(criteriaBuilder.equal(root.get("uid").as(Long.class), userQO.getUid()));
            }

            //根据username 模糊匹配
            if (StringUtils.isNotBlank(userQO.getUsername())) {
                predicateList.add(criteriaBuilder.equal(root.get("username").as(String.class), userQO.getUsername()));
            }
            //根据nuckname 模糊匹配
            if (StringUtils.isNotBlank(userQO.getNickname())) {
                predicateList.add(criteriaBuilder.like(root.get("nickname").as(String.class), "%" + userQO.getNickname() + "%"));
            }

            // 根据状态查询
            if (StringUtils.isNotBlank(userQO.getStatus())) {
                String[] statusArr = userQO.getStatus().split(",");
                if (statusArr.length > 1) {
                    CriteriaBuilder.In<Integer> in = criteriaBuilder.in(root.get("status"));
                    for (int i = 0; i < statusArr.length; i++) {
                        in.value(Integer.valueOf(statusArr[i]));
                    }
                    predicateList.add(in);
                } else {
                    predicateList.add(criteriaBuilder.equal(root.get("status").as(Integer.class), userQO.getStatus()));
                }
            } else {
                predicateList.add(criteriaBuilder.notEqual(root.get("status").as(Integer.class), -1));
            }

            Predicate[] pre = new Predicate[predicateList.size()];
            criteriaQuery.where(predicateList.toArray(pre));
            return criteriaBuilder.and(predicateList.toArray(pre));
        };

        Page<User> page = userRepository.findAll(specification, pageable);
        Pagination<User> pageVO = PageResultConverter.convert(page);
        return pageVO;
    }


    @Override
    public UserVO findByUserToken(String userToken) {
        // 从redis中去获取用户信息。
        if (StringUtils.isBlank(userToken)) {
            throw new AppException(ResultEnum.TOKEN_UNAVAILABLE);
        }
        String redisKey = String.format(RedisConstant.TOKEN_PREFIX, userToken);
//        System.out.println(redisUtil.get(redisKey));

        UserVO userVO = (UserVO) redisUtil.get(redisKey);
//        log.info("userDTO:{}",userDTO);
//        if( userDTO == null ){
//            throw new AppException(ResultEnum.TOKEN_UNAVAILABLE);
//        }
        // cookie中缓存token
//        CookeUtil.set(response, CookieConstant.USER_TOKEN, userToken,-1);
        return userVO;
    }

    @Override
    public UserVO login(LoginDTO loginDTO) {
        UserQO uo = new UserQO();
        uo.setUsername(loginDTO.getUsername());
        Pagination<User> userPageVO = this.findList(0, 1, uo, null);
        if (userPageVO.getTotal() < 1) {
            throw new AppException(ResultEnum.USER_NOT_EXIST);
        }
        User user = userPageVO.getList().get(0);
        log.info("密码:{}", MD5Util.md5(loginDTO.getPassword() + user.getSalt()));
        if (!MD5Util.md5(loginDTO.getPassword() + user.getSalt()).equals(user.getPassword())) {
            throw new AppException(ResultEnum.ACCOUNT_PASSWORD_MISMATCH);
        }
        // 未授权，非法操作
        if (user.getAdmin() == null || user.getAdmin() != 1) {
            throw new AppException(ResultEnum.ILLEGAL_OPERATION.getCode(), "非法操作，管理员并未授权，请联系管理员");
        }

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);

        String userToken = KeyUtil.genUserToken();

        userVO.setUserToken(userToken);
        String redisKey = String.format(RedisConstant.TOKEN_PREFIX, userToken);

        //缓存一天
        redisUtil.set(redisKey, userVO, 86400);
        return userVO;
    }


    @Override
    public UserVO queryUserInfo(String userToken) {
        String redisKey = String.format(RedisConstant.TOKEN_PREFIX, userToken);
        UserVO userVO = (UserVO) redisUtil.get(redisKey);
        return userVO;
    }
}
