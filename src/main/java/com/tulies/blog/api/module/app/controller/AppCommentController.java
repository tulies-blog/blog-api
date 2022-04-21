package com.tulies.blog.api.module.app.controller;

import com.tulies.blog.api.annotation.CurrentUser;
import com.tulies.blog.api.beans.base.ApiResult;
import com.tulies.blog.api.beans.base.Pagination;
import com.tulies.blog.api.beans.base.UserBean;
import com.tulies.blog.api.beans.dto.CommentReplyDTO;
import com.tulies.blog.api.beans.dto.CommentTopicDTO;
import com.tulies.blog.api.beans.qo.CommentReplyQO;
import com.tulies.blog.api.beans.vo.CommentReplyVO;
import com.tulies.blog.api.entity.CommentReply;
import com.tulies.blog.api.entity.CommentTopic;
import com.tulies.blog.api.enums.CommentEnum;
import com.tulies.blog.api.enums.ResultEnum;
import com.tulies.blog.api.exception.AppException;
import com.tulies.blog.api.module.app.beans.dto.AppChildCommentReplyDTO;
import com.tulies.blog.api.module.app.beans.dto.AppCommentReplyDTO;
import com.tulies.blog.api.module.app.beans.vo.AppCommentReplyVO;
import com.tulies.blog.api.service.CommentService;
import com.tulies.blog.api.utils.ApiResultUtil;
import com.tulies.blog.api.utils.BeanUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: 王嘉炀
 * @Description:
 * @Date: Created in 20:03 2022/02/20
 */
@Slf4j
@Api(tags = "评论模块")
@RestController
@RequestMapping("/app")
public class AppCommentController {

    @Autowired
    private CommentService commentService;


    @ApiOperation(value = "初始化评论主题")
    @PostMapping("/comment/topic")
    public ApiResult initTopic(@RequestBody @Valid CommentTopicDTO commentTopicDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new AppException(ResultEnum.PARAM_ERROR.getCode(),
                    bindingResult.getFieldError().getDefaultMessage());
        }
        CommentTopic commentTopic = commentService.findTopicByTid(commentTopicDTO.getTid());
        if (commentTopic != null) {
            return ApiResultUtil.success(commentTopic);
        }
        // 如果评论主题不存在，就创建
        return ApiResultUtil.success(commentService.saveTopic(commentTopicDTO));
    }

    @ApiOperation(value = "获取主评论")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tid", value = "评论ID", required = true)
    })
    @GetMapping("/comment/list")
    public ApiResult commentList(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize,
            @RequestParam String tid,
            String sorter,
            @CurrentUser UserBean userBean) {
        CommentReplyQO commentReplyQO = new CommentReplyQO();
        commentReplyQO.setTid(tid);
        commentReplyQO.setGrade(0); // 取主评论
        // 只查询状态正常的评论
        commentReplyQO.setStatus(CommentEnum.REPLY_STATUS_VERIFY_SUCCESS.getCode().toString());
        Pagination<CommentReplyVO> commentReplyVOPagination = this.commentService.findReplyListWithMore(pageNum - 1, pageSize, commentReplyQO, sorter, userBean);
        // TODO 根据主评论查询2条子评论
        List<AppCommentReplyVO> appCommentReplyVOList = commentReplyVOPagination.getList().stream().map(d -> {
            CommentReplyQO cr = new CommentReplyQO();
            cr.setRootid(d.getId());
            cr.setStatus(CommentEnum.REPLY_STATUS_VERIFY_SUCCESS.getCode().toString());
            // 查询子评论信息
            Pagination<CommentReplyVO> commentReplyPagination = this.commentService.findReplyListWithMore(0, 2, cr, "id asc", userBean);
            AppCommentReplyVO appCommentReplyVO = new AppCommentReplyVO();
            BeanUtil.copyProperties(d, appCommentReplyVO);
            appCommentReplyVO.setReplyList(commentReplyPagination.getList());
            appCommentReplyVO.setReplyCount(commentReplyPagination.getTotal().intValue());
            return appCommentReplyVO;
        }).collect(Collectors.toList());

        Pagination<AppCommentReplyVO> appCommentReplyVOPagination = new Pagination<AppCommentReplyVO>();

        BeanUtil.copyProperties(commentReplyVOPagination, appCommentReplyVOPagination, "list");
        appCommentReplyVOPagination.setList(appCommentReplyVOList);
        return ApiResultUtil.success(appCommentReplyVOPagination);
    }

    @ApiOperation(value = "新增主评论")
    @PostMapping("/comment/publish")
    public ApiResult commentPublish(@RequestBody @Valid AppCommentReplyDTO appCommentReplyDTO,
                                    BindingResult bindingResult,
                                    @CurrentUser UserBean userBean) {

        if (bindingResult.hasErrors()) {
            throw new AppException(ResultEnum.PARAM_ERROR.getCode(),
                    bindingResult.getFieldError().getDefaultMessage());
        }

        /** 新增主评论 **/
        CommentReplyDTO commentReplyDTO = new CommentReplyDTO();
        BeanUtils.copyProperties(appCommentReplyDTO, commentReplyDTO);
        commentReplyDTO.setUserid(userBean.getUid());
        CommentReplyVO commentReplyVO = commentService.saveCommentReply(commentReplyDTO);

        /** 增加评论数+1 **/
        commentService.incrementRepliedCount(appCommentReplyDTO.getTid());

        return ApiResultUtil.success(commentReplyVO);
    }

    @ApiOperation(value = "获取子评论")
    @GetMapping("/reply/list")
    public ApiResult replyList(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize,
            @RequestParam Integer rootid,
            @RequestParam(value = "sorter", defaultValue = "id asc") String sorter,
            @CurrentUser UserBean userBean) {
        CommentReplyQO commentReplyQO = new CommentReplyQO();
        commentReplyQO.setRootid(rootid);
//        commentReplyQO.setGrade(0); // 取主评论
        commentReplyQO.setStatus(CommentEnum.REPLY_STATUS_VERIFY_SUCCESS.getCode().toString());

        Pagination<CommentReplyVO> commentReplyVOPagination = this.commentService.findReplyListWithMore(pageNum - 1, pageSize, commentReplyQO, sorter, userBean);

        return ApiResultUtil.success(commentReplyVOPagination);
    }

    @ApiOperation(value = "新增子回复")
    @PostMapping("/reply/publish")
    public ApiResult replyPublish(@RequestBody @Valid AppChildCommentReplyDTO appCommentReplyDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new AppException(ResultEnum.PARAM_ERROR.getCode(),
                    bindingResult.getFieldError().getDefaultMessage());
        }

        // 先查询下父评论详情
        CommentReply parentCommentReply = commentService.findReplyById(appCommentReplyDTO.getParentid());
        if (parentCommentReply == null) {
            throw new AppException(ResultEnum.DATA_NOT_EXIT.getCode(), "回复的评论不存在");
        }
        /** 新增子评论回复 **/
        // rootid 是主评论ID
        int rootid = 0;
        // 如果父评论的rootid为0，说明是直接回复的主评论
        if (parentCommentReply.getRootid() == 0) {
            rootid = parentCommentReply.getId();
        } else {
            rootid = parentCommentReply.getRootid();
        }
        CommentReplyDTO commentReplyDTO = new CommentReplyDTO();
        BeanUtils.copyProperties(appCommentReplyDTO, commentReplyDTO);
        commentReplyDTO.setTid(parentCommentReply.getTid());
        commentReplyDTO.setGrade(parentCommentReply.getGrade() + 1);
        commentReplyDTO.setRootid(rootid);
        CommentReplyVO commentReplyVO = commentService.saveCommentReply(commentReplyDTO);


//        CommentTopic commentTopic = commentService.findTopicByTid(parentCommentReply.getTid());
//        if (commentTopic != null && "article".equals(commentTopic.getType())) {
//            /** 文章评论数+1 **/
//            articleService.updateCommentCount(commentTopic.getTid(), 1);
//        }
        /** 主题的总增加评论数+1 **/
        commentService.incrementRepliedCount(commentReplyDTO.getTid());

        return ApiResultUtil.success(commentReplyVO);
    }


    @ApiOperation(value = "评论点赞/取消点赞")
    @PostMapping("/comment/digg/{commentId}/{isDigg}")
    public ApiResult digg(@PathVariable Integer commentId, @PathVariable Integer isDigg, @CurrentUser UserBean userBean) {
        commentService.digg(userBean.getUid(), commentId, isDigg);
        return ApiResultUtil.success();
    }

}
