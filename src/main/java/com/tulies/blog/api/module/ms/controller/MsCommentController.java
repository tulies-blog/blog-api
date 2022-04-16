package com.tulies.blog.api.module.ms.controller;

import com.tulies.blog.api.beans.base.ApiResult;
import com.tulies.blog.api.beans.base.Pagination;
import com.tulies.blog.api.beans.qo.CommentReplyQO;
import com.tulies.blog.api.beans.qo.CommentTopicQO;
import com.tulies.blog.api.beans.vo.CommentReplyVO;
import com.tulies.blog.api.entity.CommentReply;
import com.tulies.blog.api.entity.CommentTopic;
import com.tulies.blog.api.enums.ResultEnum;
import com.tulies.blog.api.exception.AppException;
import com.tulies.blog.api.service.CommentService;
import com.tulies.blog.api.utils.ApiResultUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author 王嘉炀
 * @date 2022-02-19 20:32
 */
@Api(tags = "评论管理")
@Slf4j
@RestController
@RequestMapping("/ms/comment")
public class MsCommentController {
    @Autowired
    private CommentService commentService;

    @GetMapping("/topics")
    public ApiResult listTopic(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                               @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize,
                               CommentTopicQO commentTopicQO,
                               String sorter) {
        Pagination<CommentTopic> pageVO = this.commentService.findTopicList(pageNum - 1, pageSize, commentTopicQO, sorter);
        return ApiResultUtil.success(pageVO);
    }


    @DeleteMapping("/topics/{id}")
    public ApiResult deleteTopic(@PathVariable Integer id) {
        CommentTopic record = commentService.findTopicById(id);
        if (record == null) {
            throw new AppException(ResultEnum.DATA_NOT_EXIT);
        }
        commentService.deleteTopicById(id);
        ApiResult resultVO = ApiResultUtil.success();
        return resultVO;
    }

    @PatchMapping("/topics/{id}/status/{status}")
    public ApiResult changeTopicStatus(@PathVariable Integer id, @PathVariable Integer status) {
        // 先查询下当前这个活动信息，判断下状态，是否是可以删除的情况。
        CommentTopic commentTopic = commentService.findTopicById(id);
        if (commentTopic == null) {
            throw new AppException(ResultEnum.DATA_NOT_EXIT);
        }
        commentService.changeTopicStatus(id, status);
        return ApiResultUtil.success();
    }

    @GetMapping("/replys")
    public ApiResult listReply(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                               @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize,
                               CommentReplyQO commentReplyQO,
                               String sorter) {
        Pagination<CommentReply> pageVO = this.commentService.findReplyList(pageNum - 1, pageSize, commentReplyQO, sorter);
        return ApiResultUtil.success(pageVO);
    }


    @DeleteMapping("/replys/{id}")
    public ApiResult deleteReply(@PathVariable Integer id) {
        // 先查询下当前这个活动信息
        CommentReply record = commentService.findReplyById(id);
        if (record == null) {
            throw new AppException(ResultEnum.DATA_NOT_EXIT);
        }

        commentService.deleteReplyById(id);
        ApiResult resultVO = ApiResultUtil.success();
        return resultVO;
    }

    @PatchMapping("/replys/{id}/status/{status}")
    public ApiResult changeReplyStatus(@PathVariable Integer id, @PathVariable Integer status) {

        // 先查询下当前这个活动信息，判断下状态，是否是可以删除的情况。
        CommentReply commentReply = commentService.findReplyById(id);
        if (commentReply == null) {
            throw new AppException(ResultEnum.DATA_NOT_EXIT);
        }
        commentService.changeReplyStatus(id, status);
        return ApiResultUtil.success();
    }

    @GetMapping("/replys/{id}")
    public ApiResult infoReply(@PathVariable Integer id) {
        CommentReplyVO commentReplyVO = this.commentService.findReplyByIdWithMore(id);
        if (commentReplyVO == null) {
            throw new AppException(ResultEnum.DATA_NOT_EXIT.getCode(), ResultEnum.DATA_NOT_EXIT.getMessage());
        }
        return ApiResultUtil.success(commentReplyVO);
    }


}
