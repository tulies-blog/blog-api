package com.tulies.blog.api.service;

import com.tulies.blog.api.beans.base.Pagination;
import com.tulies.blog.api.beans.base.UserBean;
import com.tulies.blog.api.beans.dto.CommentReplyDTO;
import com.tulies.blog.api.beans.dto.CommentTopicDTO;
import com.tulies.blog.api.beans.qo.CommentDiggQO;
import com.tulies.blog.api.beans.qo.CommentReplyQO;
import com.tulies.blog.api.beans.qo.CommentTopicQO;
import com.tulies.blog.api.beans.vo.CommentReplyVO;
import com.tulies.blog.api.entity.CommentDigg;
import com.tulies.blog.api.entity.CommentReply;
import com.tulies.blog.api.entity.CommentTopic;

/**
 * @author 王嘉炀
 * @date 2019-10-13 19:55
 */
public interface CommentService {
    Pagination<CommentTopic> findTopicList(Integer pageNum, Integer pageSize, CommentTopicQO commentTopicQO, String sorter);

    CommentTopic findTopicById(Integer id);

    CommentTopic findTopicByTid(String tid);

    void deleteTopicById(Integer id);

    void changeTopicStatus(Integer id, Integer status);

    CommentTopic saveTopic(CommentTopicDTO commentTopicDTO);

    void incrementRepliedCount(String tid);

    Pagination<CommentReply> findReplyList(Integer pageNum, Integer pageSize, CommentReplyQO commentReplyQO, String sorter);


    Pagination<CommentReplyVO> findReplyListWithMore(Integer pageNum, Integer pageSize, CommentReplyQO commentReplyQO, String sorter, UserBean userBean);

    CommentReply findReplyById(Integer id);

    void deleteReplyById(Integer id);

    void changeReplyStatus(Integer id, Integer status);

    CommentReplyVO findReplyByIdWithMore(Integer id);

    CommentReplyVO saveCommentReply(CommentReplyDTO commentReplyDTO);

    // 点赞
    Pagination<CommentDigg> findCommentDiggList(CommentDiggQO commentDiggQO);

    // 评论点赞
    void digg(String uid, Integer commentId, int isDigg);

    void updateDiggCount(Integer commentId, int diff);

}
