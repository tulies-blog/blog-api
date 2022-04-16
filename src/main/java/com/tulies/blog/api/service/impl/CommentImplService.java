package com.tulies.blog.api.service.impl;

import com.tulies.blog.api.beans.base.Pagination;
import com.tulies.blog.api.beans.base.UserBean;
import com.tulies.blog.api.beans.dto.CommentReplyDTO;
import com.tulies.blog.api.beans.dto.CommentTopicDTO;
import com.tulies.blog.api.beans.qo.CommentDiggQO;
import com.tulies.blog.api.beans.qo.CommentReplyQO;
import com.tulies.blog.api.beans.qo.CommentTopicQO;
import com.tulies.blog.api.beans.vo.CommentReplyVO;
import com.tulies.blog.api.converter.PageResultConverter;
import com.tulies.blog.api.entity.CommentDigg;
import com.tulies.blog.api.entity.CommentReply;
import com.tulies.blog.api.entity.CommentTopic;
import com.tulies.blog.api.enums.CommentEnum;
import com.tulies.blog.api.enums.ResultEnum;
import com.tulies.blog.api.exception.AppException;
import com.tulies.blog.api.repository.CommentDiggRepository;
import com.tulies.blog.api.repository.CommentReplyRepository;
import com.tulies.blog.api.repository.CommentTopicRepository;
import com.tulies.blog.api.service.ArticleService;
import com.tulies.blog.api.service.CommentService;
import com.tulies.blog.api.utils.BeanUtil;
import com.tulies.blog.api.utils.CommUtil;
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
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author 王嘉炀
 * @date 2019-10-13 19:55
 */
@Slf4j
@Service
public class CommentImplService implements CommentService {

    @Autowired
    private CommentTopicRepository commentTopicRepository;
    @Autowired
    private CommentReplyRepository commentReplyRepository;
    @Autowired
    private CommentDiggRepository commentDiggRepository;
    @Autowired
    private ArticleService articleService;

    @Override
    public CommentTopic findTopicById(Integer id) {
        Optional<CommentTopic> record = commentTopicRepository.findById(id);
        if (!record.isPresent()) {
            return null;
        }
        return record.get();
    }

    @Override
    public CommentTopic findTopicByTid(String tid) {
        CommentTopic commentTopic = commentTopicRepository.findByTid(tid);
        return commentTopic;
    }

    @Override
    @Transactional
    public void deleteTopicById(Integer id) {
        commentTopicRepository.changeStatus(id, -1);
    }

    @Override
    @Transactional
    public void changeTopicStatus(Integer id, Integer status) {
        commentTopicRepository.changeStatus(id, status);
    }

    @Override
    @Transactional
    public CommentTopic saveTopic(CommentTopicDTO commentTopicDTO) {
        CommentTopic commentTopic = new CommentTopic();
        BeanUtils.copyProperties(commentTopicDTO, commentTopic);
        return commentTopicRepository.save(commentTopic);
    }

    // 评论主题的评论数+1
    @Override
    @Transactional
    public void incrementRepliedCount(String tid) {
        CommentTopic commentTopic = this.findTopicByTid(tid);
        commentTopic.setRepliedCount(commentTopic.getRepliedCount() + 1);
        commentTopicRepository.save(commentTopic);

        if (commentTopic != null) {
            if ("article".equals(commentTopic.getType())) {
                /** 文章评论数+1 **/
                try {
                    articleService.updateCommentCount(commentTopic.getTid(), 1);
                } catch (AppException appException) {
                    appException.printStackTrace();
                }
            }
        }
    }

    @Override
    public Pagination<CommentTopic> findTopicList(Integer pageNum, Integer pageSize, CommentTopicQO commentTopicQO, String sorter) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        if (StringUtils.isNotBlank(sorter)) {
            sort = CommUtil.formatSorter(sorter);
        }
        Pageable pageable = PageRequest.of(pageNum, pageSize, sort);

        Specification<CommentTopic> specification = (Specification<CommentTopic>) (root, criteriaQuery, criteriaBuilder) -> {

            List<Predicate> predicateList = new ArrayList<>();
            //根据id 查询
            if (commentTopicQO.getId() != null) {
                predicateList.add(criteriaBuilder.equal(root.get("id").as(Integer.class), commentTopicQO.getId()));
            }

            //根据tid 查询
            if (StringUtils.isNotBlank(commentTopicQO.getTid())) {
                predicateList.add(criteriaBuilder.equal(root.get("tid").as(String.class), commentTopicQO.getTid()));
            }

            //根据username 模糊匹配
            if (StringUtils.isNotBlank(commentTopicQO.getTitle())) {
                predicateList.add(criteriaBuilder.like(root.get("title").as(String.class), "%" + commentTopicQO.getTitle() + "%"));
            }
            // 审核状态
            if (StringUtils.isNotBlank(commentTopicQO.getCheckMode())) {
                String[] checkModeArr = commentTopicQO.getCheckMode().split(",");
                if (checkModeArr.length > 1) {
                    CriteriaBuilder.In<Integer> in = criteriaBuilder.in(root.get("checkMode"));
                    for (int i = 0; i < checkModeArr.length; i++) {
                        in.value(Integer.valueOf(checkModeArr[i]));
                    }
                    predicateList.add(in);
                } else {
                    predicateList.add(criteriaBuilder.equal(root.get("checkMode").as(Integer.class), commentTopicQO.getCheckMode()));
                }
            }

            // 状态，
            if (StringUtils.isNotBlank(commentTopicQO.getStatus())) {
                String[] statusArr = commentTopicQO.getStatus().split(",");
                if (statusArr.length > 1) {
                    CriteriaBuilder.In<Integer> in = criteriaBuilder.in(root.get("status"));
                    for (int i = 0; i < statusArr.length; i++) {
                        in.value(Integer.valueOf(statusArr[i]));
                    }
                    predicateList.add(in);
                } else {
                    predicateList.add(criteriaBuilder.equal(root.get("status").as(Integer.class), commentTopicQO.getStatus()));
                }
            } else {
                predicateList.add(criteriaBuilder.notEqual(root.get("status").as(Integer.class), -1));
            }

            Predicate[] pre = new Predicate[predicateList.size()];
            criteriaQuery.where(predicateList.toArray(pre));
            return criteriaBuilder.and(predicateList.toArray(pre));

        };
        Page<CommentTopic> actinfoPage = commentTopicRepository.findAll(specification, pageable);
        Pagination<CommentTopic> pageVO = PageResultConverter.convert(actinfoPage);
        return pageVO;
    }


    /***
     * 这下面是 REPLY 的东西
     */

    @Override
    public Pagination<CommentReply> findReplyList(Integer pageNum, Integer pageSize, CommentReplyQO commentReplyQO, String sorter) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        if (StringUtils.isNotBlank(sorter)) {
            sort = CommUtil.formatSorter(sorter);
        }
        Pageable pageable = PageRequest.of(pageNum, pageSize, sort);
        Specification<CommentReply> specification = (Specification<CommentReply>) (root, criteriaQuery, criteriaBuilder) -> {

            List<Predicate> predicateList = new ArrayList<>();
            //根据id 查询
            if (commentReplyQO.getId() != null) {
                predicateList.add(criteriaBuilder.equal(root.get("id").as(Integer.class), commentReplyQO.getId()));
            }
            //根据parentId 查询
            if (commentReplyQO.getParentid() != null) {
                predicateList.add(criteriaBuilder.equal(root.get("parentid").as(Integer.class), commentReplyQO.getParentid()));
            }
            //根据parentId 查询
            if (commentReplyQO.getRootid() != null) {
                predicateList.add(criteriaBuilder.equal(root.get("rootid").as(Integer.class), commentReplyQO.getRootid()));
            }
            //根据grade 查询
            if (commentReplyQO.getGrade() != null) {
                predicateList.add(criteriaBuilder.equal(root.get("grade").as(Integer.class), commentReplyQO.getGrade()));
            }
            //根据tid 查询
            if (StringUtils.isNotBlank(commentReplyQO.getTid())) {
                predicateList.add(criteriaBuilder.equal(root.get("tid").as(String.class), commentReplyQO.getTid()));
            }
            //根据userid 查询
            if (StringUtils.isNotBlank(commentReplyQO.getUserid())) {
                predicateList.add(criteriaBuilder.equal(root.get("userid").as(String.class), commentReplyQO.getUserid()));
            }
            //根据username 模糊匹配
            if (StringUtils.isNotBlank(commentReplyQO.getUsername())) {
                predicateList.add(criteriaBuilder.equal(root.get("username").as(String.class), commentReplyQO.getUsername()));
            }
            // 根据状态查询
            if (StringUtils.isNotBlank(commentReplyQO.getStatus())) {
                String[] statusArr = commentReplyQO.getStatus().split(",");
                if (statusArr.length > 1) {
                    CriteriaBuilder.In<Integer> in = criteriaBuilder.in(root.get("status"));
                    for (int i = 0; i < statusArr.length; i++) {
                        in.value(Integer.valueOf(statusArr[i]));
                    }
                    predicateList.add(in);
                } else {
                    predicateList.add(criteriaBuilder.equal(root.get("status").as(Integer.class), commentReplyQO.getStatus()));
                }
            } else {
                predicateList.add(criteriaBuilder.notEqual(root.get("status").as(Integer.class), -1));
            }
            //根据评论内容 模糊匹配
            if (StringUtils.isNotBlank(commentReplyQO.getContent())) {
                predicateList.add(criteriaBuilder.like(root.get("content").as(String.class), commentReplyQO.getContent()));
            }
            Predicate[] pre = new Predicate[predicateList.size()];
            criteriaQuery.where(predicateList.toArray(pre));
            return criteriaBuilder.and(predicateList.toArray(pre));

        };
        Page<CommentReply> commentReplyPage = commentReplyRepository.findAll(specification, pageable);
        Pagination<CommentReply> commentReplyPagination = PageResultConverter.convert(commentReplyPage);


        return commentReplyPagination;
    }

    @Override
    public Pagination<CommentDigg> findCommentDiggList(CommentDiggQO commentDiggQO) {
        Pageable pageable = PageRequest.of(commentDiggQO.getPageNum() - 1, commentDiggQO.getPageSize());
        Specification<CommentDigg> specification = (Specification<CommentDigg>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicateList = new ArrayList<>();
            //根据articleId
            if (StringUtils.isNotBlank(commentDiggQO.getCommentId())) {
                String[] cidArr = commentDiggQO.getCommentId().split(",");
                if (cidArr.length > 1) {
                    CriteriaBuilder.In<Integer> in = criteriaBuilder.in(root.get("commentId"));
                    for (int i = 0; i < cidArr.length; i++) {
                        in.value(Integer.valueOf(cidArr[i]));
                    }
                    predicateList.add(in);
                } else {
                    predicateList.add(criteriaBuilder.equal(root.get("commentId").as(Integer.class), commentDiggQO.getCommentId()));
                }
            }
            //根据uid
            if (StringUtils.isNotBlank(commentDiggQO.getUid())) {
                predicateList.add(criteriaBuilder.equal(root.get("uid").as(String.class), commentDiggQO.getUid()));
            }
            Predicate[] pre = new Predicate[predicateList.size()];
            criteriaQuery.where(predicateList.toArray(pre));
            return criteriaBuilder.and(predicateList.toArray(pre));
        };
        Page<CommentDigg> actinfoPage = commentDiggRepository.findAll(specification, pageable);
        Pagination<CommentDigg> commentDiggPagination = PageResultConverter.convert(actinfoPage);
        return commentDiggPagination;
    }

    @Override
    public Pagination<CommentReplyVO> findReplyListWithMore(Integer pageNum, Integer pageSize, CommentReplyQO commentReplyQO, String sorter, UserBean userBean) {

        Pagination<CommentReply> commentReplyPagination = this.findReplyList(pageNum, pageSize, commentReplyQO, sorter);
        // TODO 需要查询一些实体信息，这边效率特别差 需要sql优化
        List<CommentReplyVO> commentReplyVOList = commentReplyPagination.getList().stream().map(d -> {
            CommentReplyVO commentReplyVO = new CommentReplyVO();
            BeanUtils.copyProperties(d, commentReplyVO);
            // 查询parent
            if (d.getParentid() != 0) {
                commentReplyVO.setParent(this.findReplyById(d.getParentid()));
            }
            //  查询root
            if (d.getRootid() != 0) {
                commentReplyVO.setRoot(this.findReplyById(d.getRootid()));
            }
            //  查询主题
            if (StringUtils.isNotBlank(d.getTid())) {
                commentReplyVO.setTopic(this.findTopicByTid(d.getTid()));
            }
            return commentReplyVO;
        }).collect(Collectors.toList());

//        commentReplyVOPagination.setList(commentReplyVOList);

        // 根据查到的数据，再去查下用户点赞行为数据。
        if (commentReplyVOList.size() > 0 && userBean != null) {
            String ids = commentReplyVOList.stream().map(v -> v.getId().toString()).collect(Collectors.joining(","));
            CommentDiggQO commentDiggQO = new CommentDiggQO();
            commentDiggQO.setUid(userBean.getUid());
            commentDiggQO.setCommentId(ids);
            Pagination<CommentDigg> commentDiggPagination = this.findCommentDiggList(commentDiggQO);

            List<Integer> idList = commentDiggPagination.getList().stream().map(v -> v.getCommentId()).collect(Collectors.toList());

//            for (ArticleInteract articleInteract : articleInteractPagination.getList()) {
//                articleInteractMap.put(articleInteract.getArticleId(), articleInteract);
//            }
            // 下面开始拼装
            commentReplyVOList = commentReplyVOList.stream().map(d -> {
                d.setIsDigg(idList.contains(d.getId()) ? 1 : 0);
                return d;
            }).collect(Collectors.toList());
        }

        // 最后组装
        Pagination<CommentReplyVO> commentReplyVOPagination = new Pagination<CommentReplyVO>();
        BeanUtil.copyProperties(commentReplyPagination, commentReplyVOPagination, "list");
        commentReplyVOPagination.setList(commentReplyVOList);
        return commentReplyVOPagination;
    }

    @Override
    public CommentReply findReplyById(Integer id) {
        Optional<CommentReply> record = commentReplyRepository.findById(id);
        if (!record.isPresent()) {
            return null;
        }
        return record.get();
    }

    @Override
    @Transactional
    public void deleteReplyById(Integer id) {
        commentReplyRepository.changeStatus(id, -1);

    }

    @Override
    @Transactional
    public void changeReplyStatus(Integer id, Integer status) {
        commentReplyRepository.changeStatus(id, status);

    }

    @Override
    public CommentReplyVO findReplyByIdWithMore(Integer id) {
        Optional<CommentReply> record = commentReplyRepository.findById(id);
        if (!record.isPresent()) {
            return null;
        }
        CommentReply commentReply = record.get();
        CommentReplyVO commentReplyVO = new CommentReplyVO();
        BeanUtils.copyProperties(commentReply, commentReplyVO);

        // 查主题信息
        if (StringUtils.isNotBlank(commentReply.getTid())) {
            CommentTopic commentTopic = commentTopicRepository.findByTid(commentReply.getTid());
            commentReplyVO.setTopic(commentTopic);
        }
        // 查parent信息
        if (commentReply.getParentid() != null) {
            CommentReply parent = this.findReplyById(commentReply.getParentid());
            commentReplyVO.setParent(parent);
        }
        //查root信息
        if (commentReply.getRootid() != null) {
            CommentReply root = this.findReplyById(commentReply.getRootid());
            commentReplyVO.setRoot(root);
        }
        return commentReplyVO;
    }

    //    { text: '删除', value: -1 },
    //    { text: '待审核', value: 0 },
    //    { text: '正常', value: 1 },
    //    { text: '未通过', value: 2 },
    @Override
    @Transactional
    public CommentReplyVO saveCommentReply(CommentReplyDTO commentReplyDTO) {
        CommentTopic commentTopic = this.findTopicByTid(commentReplyDTO.getTid());
        if (commentTopic == null) {
            throw new AppException(ResultEnum.DATA_NOT_EXIT);
        }

        if (commentTopic.getStatus() != CommentEnum.TOPIC_STATUS_ONLINE.getCode()) {
            throw new AppException(ResultEnum.ILLEGAL_OPERATION.getCode(), "当前帖子禁止评论");
        }

        CommentReply commentReply = new CommentReply();
        BeanUtils.copyProperties(commentReplyDTO, commentReply);
        if (commentReply.getRootid() == null) {
            commentReply.setRootid(0);
        }
        if (commentReply.getGrade() == null) {
            commentReply.setGrade(0);
        }
//        commentReply.setCheckStatus(checkMode);
        log.info("commentTopic===={}", commentTopic);
        // 应该用枚举
        commentReply.setStatus(commentTopic.getCheckMode() == CommentEnum.TOPIC_POST_CHECK.getCode() ? CommentEnum.REPLY_STATUS_VERIFY_SUCCESS.getCode() : CommentEnum.REPLY_STATUS_WILL_VERIFY.getCode());
        commentReply.setDiggCount(0);
        Date nowDate = new Date();
        commentReply.setUpdateTime(nowDate);
        commentReply.setCreateTime(nowDate);
        log.info("commentReply===={}", commentReply);

        CommentReply commentReplyResult = commentReplyRepository.save(commentReply);
        CommentReplyVO commentReplyVO = this.findReplyByIdWithMore(commentReplyResult.getId());
        return commentReplyVO;
    }


    @Override
    @Transactional
    public void digg(String uid, Integer commentId, int isDigg) {
        // 查询是否存在，
        CommentDigg commentDigg = this.commentDiggRepository.findByUidAndCommentId(uid, commentId);
        if ((commentDigg == null && isDigg == 0) || (commentDigg != null && isDigg == 1)) {
            return;
        }
        if (isDigg == 0) {
            // 取消点赞
            this.commentDiggRepository.deleteByUidAndCommentId(uid, commentId);
        } else {
            commentDigg = new CommentDigg();
            commentDigg.setUid(uid);
            commentDigg.setCommentId(commentId);
            this.commentDiggRepository.save(commentDigg);
        }
        // 2、更新这条评论的总点赞数
        this.updateDiggCount(commentId, isDigg == 0 ? -1 : 1);
    }


    @Override
    @Transactional
    public void updateDiggCount(Integer commentId, int diff) {
        CommentReply commentReply = this.findReplyById(commentId);
        if (commentReply == null) {
            throw new AppException(ResultEnum.DATA_NOT_EXIT);
        }
        commentReply.setDiggCount(commentReply.getDiggCount() + diff);
        commentReplyRepository.save(commentReply);
    }


//    @Override
//    @Transactional
//    public CommentReply updateCommentReply(CommentReplyDTO commentReplyDTO) {
//        CommentReply commentReply = new CommentReply();
//        BeanUtils.copyProperties(commentReplyDTO, commentReply);
//        Date nowDate = new Date();
//        commentReply.setUpdateTime(nowDate);
//        commentReply.setCreateTime(nowDate);
//        CommentReply commentReplyResult =commentReplyRepository.save(commentReply);
//        return commentReplyResult;
//    }

}
