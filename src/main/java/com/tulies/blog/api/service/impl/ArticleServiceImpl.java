package com.tulies.blog.api.service.impl;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tulies.blog.api.beans.base.Pagination;
import com.tulies.blog.api.beans.dto.ArticleDTO;
import com.tulies.blog.api.beans.qo.ArticleDiggQO;
import com.tulies.blog.api.beans.qo.ArticleQO;
import com.tulies.blog.api.beans.vo.ArticleVO;
import com.tulies.blog.api.converter.PageResultConverter;
import com.tulies.blog.api.entity.*;
import com.tulies.blog.api.enums.ResultEnum;
import com.tulies.blog.api.exception.AppException;
import com.tulies.blog.api.repository.ArticleDiggRepository;
import com.tulies.blog.api.repository.ArticleRepository;
import com.tulies.blog.api.service.ArticleService;
import com.tulies.blog.api.service.CategoryService;
import com.tulies.blog.api.service.TagService;
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

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 王嘉炀
 * @date 2019-10-08 22:30
 */
@Slf4j
@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private ArticleDiggRepository articleDiggRepository;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private TagService tagService;

    @Autowired
    EntityManager entityManager;

    JPAQueryFactory jpaQueryFactory;

    @PostConstruct
    public void initFactory() {
        jpaQueryFactory = new JPAQueryFactory(entityManager);
    }


    /**
     * 查询列表
     *
     * @param pageNum
     * @param pageSize
     * @param articleQo
     * @return
     */
    @Override
    public Pagination<ArticleVO> findList(Integer pageNum, Integer pageSize, ArticleQO articleQo, String sorter) {

        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        if (StringUtils.isNotBlank(sorter)) {
            sort = CommUtil.formatSorter(sorter);
        }
        Pageable pageable = PageRequest.of(pageNum, pageSize, sort);
        Specification<Article> specification = (Specification<Article>) (root, criteriaQuery, criteriaBuilder) -> {

            List<Predicate> predicateList = new ArrayList<>();
            //根据actid 查询
            if (articleQo.getId() != null) {
                predicateList.add(criteriaBuilder.equal(root.get("id").as(Long.class), articleQo.getId()));
            }
            //根据title模糊匹配
            if (StringUtils.isNotBlank(articleQo.getTitle())) {
                predicateList.add(criteriaBuilder.like(root.get("title").as(String.class), "%" + articleQo.getTitle() + "%"));
            }

            //根据tags模糊匹配,目前只是单标签匹配
            if (StringUtils.isNotBlank(articleQo.getTags())) {
                predicateList.add(criteriaBuilder.like(root.get("tags").as(String.class), "%," + articleQo.getTags() + ",%"));
            }

            if (articleQo.getCategoryId() != null) {
                predicateList.add(criteriaBuilder.equal(root.get("categoryId").as(Integer.class), articleQo.getCategoryId()));
            }

            // 根据状态查询
            if (StringUtils.isNotBlank(articleQo.getStatus())) {
                String[] statusArr = articleQo.getStatus().split(",");
                if (statusArr.length > 1) {
                    CriteriaBuilder.In<Integer> in = criteriaBuilder.in(root.get("status"));
                    for (int i = 0; i < statusArr.length; i++) {
                        in.value(Integer.valueOf(statusArr[i]));
                    }
                    predicateList.add(in);
                } else {
                    predicateList.add(criteriaBuilder.equal(root.get("status").as(Integer.class), articleQo.getStatus()));
                }
            } else {
                predicateList.add(criteriaBuilder.notEqual(root.get("status").as(Integer.class), -1));
            }
//            //根据创建时间查询
//            if(StringUtils.isNotBlank(articleQo.getCreateTime())){
//                predicateList.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createTime").as(String.class),articleQo.getCreateTime()));
//            }
//            //根据更新时间查询
//            if(StringUtils.isNotBlank(articleQo.getUpdateTime())){
//                predicateList.add(criteriaBuilder.greaterThanOrEqualTo(root.get("updateTime").as(String.class),articleQo.getUpdateTime()));
//            }

            Predicate[] pre = new Predicate[predicateList.size()];
            criteriaQuery.where(predicateList.toArray(pre));
            return criteriaBuilder.and(predicateList.toArray(pre));
        };
        Page<Article> actinfoPage = articleRepository.findAll(specification, pageable);
        Pagination<Article> articlePagination = PageResultConverter.convert(actinfoPage);
        // 反查一下全部的分类
        List<Category> categoryList = categoryService.findAll();
        Map<Integer, Category> categoryMap = new HashMap<Integer, Category>();
        for (Category category : categoryList) {
            categoryMap.put(category.getId(), category);
        }
        // 下面开始拼装
        List<ArticleVO> articleVOList = articlePagination.getList().stream().map(d -> {
            ArticleVO articleVO = new ArticleVO();
            BeanUtil.copyProperties(d, articleVO, "content");
            articleVO.setCategory(categoryMap.get(d.getCategoryId()));
            return articleVO;
        }).collect(Collectors.toList());
        Pagination<ArticleVO> articleVOPagination = new Pagination<ArticleVO>();
        BeanUtil.copyProperties(articlePagination, articleVOPagination, "list");
        articleVOPagination.setList(articleVOList);
        return articleVOPagination;
    }

    @Override
    public long count(ArticleQO articleQo) {
        Specification<Article> specification = (Specification<Article>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicateList = new ArrayList<>();
            //根据actid 查询
            if (StringUtils.isNotBlank(articleQo.getId())) {
                predicateList.add(criteriaBuilder.equal(root.get("id").as(String.class), articleQo.getId()));
            }
            //根据title模糊匹配
            if (StringUtils.isNotBlank(articleQo.getTitle())) {
                predicateList.add(criteriaBuilder.like(root.get("title").as(String.class), "%" + articleQo.getTitle() + "%"));
            }

            //根据tags模糊匹配,目前只是单标签匹配
            if (StringUtils.isNotBlank(articleQo.getTags())) {
                predicateList.add(criteriaBuilder.like(root.get("tags").as(String.class), "%," + articleQo.getTags() + ",%"));
            }

            if (articleQo.getCategoryId() != null) {
                predicateList.add(criteriaBuilder.equal(root.get("categoryId").as(Integer.class), articleQo.getCategoryId()));
            }

            // 根据状态查询
            if (StringUtils.isNotBlank(articleQo.getStatus())) {
                String[] statusArr = articleQo.getStatus().split(",");
                if (statusArr.length > 1) {
                    CriteriaBuilder.In<Integer> in = criteriaBuilder.in(root.get("status"));
                    for (int i = 0; i < statusArr.length; i++) {
                        in.value(Integer.valueOf(statusArr[i]));
                    }
                    predicateList.add(in);
                } else {
                    predicateList.add(criteriaBuilder.equal(root.get("status").as(Integer.class), articleQo.getStatus()));
                }
            } else {
                predicateList.add(criteriaBuilder.notEqual(root.get("status").as(Integer.class), -1));
            }
            Predicate[] pre = new Predicate[predicateList.size()];
            criteriaQuery.where(predicateList.toArray(pre));
            return criteriaBuilder.and(predicateList.toArray(pre));
        };
        return articleRepository.count(specification);
    }

    //    https://www.jianshu.com/p/2b68af9aa0f5
    @Override
    public Pagination<ArticleVO> findByQueryDSL(Integer pageNum, Integer pageSize, ArticleQO articleQo, String sorter) {

        log.info("articleQO={}", articleQo);
        QArticle qArticle = QArticle.article;
        QCategory qCategory = QCategory.category;

        List<OrderSpecifier> orderSpecifierList = CommUtil.getSortedColumns(qArticle, "id desc");
        if (StringUtils.isNotBlank(sorter)) {
            orderSpecifierList = CommUtil.getSortedColumns(qArticle, sorter);
        }

        Pageable pageable = PageRequest.of(pageNum, pageSize);
        JPAQuery<Article> jpaQuery = jpaQueryFactory.selectFrom(qArticle);
        com.querydsl.core.types.Predicate predicate = qArticle.isNotNull().or(qArticle.isNull());

        if (StringUtils.isNotBlank(articleQo.getId())) {
            predicate = ExpressionUtils.and(predicate, qArticle.id.eq(articleQo.getId()));
        }

        //根据neId模糊匹配
        if (StringUtils.isNotBlank(articleQo.getNeId())) {
            predicate = ExpressionUtils.and(predicate, qArticle.id.ne(articleQo.getNeId()));
        }
        //根据niIds模糊匹配
        if (StringUtils.isNotBlank(articleQo.getNiIds())) {
            predicate = ExpressionUtils.and(predicate, qArticle.id.notIn(articleQo.getNiIds().split(",")));
        }

        //根据title模糊匹配
        if (StringUtils.isNotBlank(articleQo.getTitle())) {
            predicate = ExpressionUtils.and(predicate, qArticle.title.like("%" + articleQo.getTitle() + "%"));
        }

        // 是否在列表中显示
        if (articleQo.getIsShow() != null) {
            predicate = ExpressionUtils.and(predicate, qArticle.isShow.eq(articleQo.getIsShow()));
        }
        // 是否原创
        if (articleQo.getIsOriginal() != null) {
            predicate = ExpressionUtils.and(predicate, qArticle.isOriginal.eq(articleQo.getIsOriginal()));
        }
        // 根据tags模糊匹配,目前只是单标签匹配
        if (StringUtils.isNotBlank(articleQo.getTags())) {
            String[] tagsArr = articleQo.getTags().split(",");
            if (tagsArr.length > 1) {
                com.querydsl.core.types.Predicate predicate2 = null;
                for (String tag : tagsArr) {
                    if (predicate2 == null) {
                        predicate2 = qArticle.tags.like("%," + tag + ",%");
                    } else {
                        predicate2 = ExpressionUtils.or(predicate2, qArticle.tags.like("%," + tag + ",%"));
                    }
                }
                predicate = ExpressionUtils.and(predicate, predicate2);
            } else {
                predicate = ExpressionUtils.and(predicate, qArticle.tags.like("%," + articleQo.getTags() + ",%"));
            }
        }
        // 跟分类ID查询
        if (articleQo.getCategoryId() != null) {
            predicate = ExpressionUtils.and(predicate, qArticle.categoryId.eq(articleQo.getCategoryId()));
        }
        if (StringUtils.isNotBlank(articleQo.getCategoryUrlName())) {
            jpaQuery.leftJoin(qCategory).on(qArticle.categoryId.eq(qCategory.id));
            predicate = ExpressionUtils.and(predicate, qCategory.urlName.eq(articleQo.getCategoryUrlName()));
        }
        // 根据状态查询
        if (StringUtils.isNotBlank(articleQo.getStatus())) {
            String[] statusArr = articleQo.getStatus().split(",");
            if (statusArr.length > 1) {
                predicate = ExpressionUtils.and(predicate, qArticle.status.in(Arrays.stream(statusArr).map(s -> Integer.valueOf(s)).collect(Collectors.toList())));
            } else {
                predicate = ExpressionUtils.and(predicate, qArticle.status.eq(Integer.valueOf(articleQo.getStatus())));
            }
        } else {
            predicate = ExpressionUtils.and(predicate, qArticle.status.ne(-1));
        }
        jpaQuery.where(predicate);
        orderSpecifierList.forEach(orderSpecifier -> jpaQuery.orderBy(orderSpecifier));
        List<Article> articleList = jpaQuery.offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        long total = jpaQuery.fetchCount();

        // 反查一下全部的分类
        List<Category> categoryList = categoryService.findAll();
        Map<Integer, Category> categoryMap = new HashMap<Integer, Category>();
        for (Category category : categoryList) {
            categoryMap.put(category.getId(), category);
        }
        // 下面开始拼装
        List<ArticleVO> articleVOList = articleList.stream().map(d -> {
            ArticleVO articleVO = new ArticleVO();
            BeanUtil.copyProperties(d, articleVO, "content");
            articleVO.setCategory(categoryMap.get(d.getCategoryId()));
            return articleVO;
        }).collect(Collectors.toList());
        Pagination<ArticleVO> articleVOPagination = Pagination.create(articleVOList, pageNum + 1, pageSize, total);
        return articleVOPagination;
    }


    @Override
    public Article findById(String id) {
        Optional<Article> record = articleRepository.findById(id);
        if (!record.isPresent()) {
            return null;
        }
        return record.get();
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        this.changeStatus(id, -1);
    }

    @Override
    @Transactional
    public Article create(ArticleDTO articleForm) {
        // 新增文章基础信息
        Article article = new Article();
        // 处理下tags
        String tags = articleForm.getTags();
        tags = tags.replace(";", ",");
        tags = tags.replace("；", ",");
        tags = tags.replace("，", ",");
        if (!tags.startsWith(",")) tags = "," + tags;
        if (!tags.endsWith(",")) tags += ",";
        articleForm.setTags(tags);

        BeanUtils.copyProperties(articleForm, article);
        Date nowDate = new Date();
        article.setCreateTime(nowDate);
        article.setUpdateTime(nowDate);
        Article articleResult = articleRepository.save(article);

        // 同步文章数
        syncArticleCount(articleForm.getTags() + "," + article.getTags(), article.getCategoryId());

        return articleResult;
    }


    @Override
    @Transactional
    public Article update(ArticleDTO articleDTO) {
        Article article = this.findById(articleDTO.getId());
        String tagsOld = article.getTags();
        Integer categoryIdOld = article.getCategoryId();
        // 处理下tags
        if (StringUtils.isNotBlank(articleDTO.getTags())) {
            String tags = articleDTO.getTags();
            tags = tags.replace(";", ",");
            tags = tags.replace("；", ",");
            tags = tags.replace("，", ",");
            if (!tags.startsWith(",")) tags = "," + tags;
            if (!tags.endsWith(",")) tags += ",";
            articleDTO.setTags(tags);
        }
        BeanUtil.copyProperties(articleDTO, article);
        Date nowDate = new Date();
        article.setUpdateTime(nowDate);

        // 同步文章数
        if (!tagsOld.equals(article.getTags()))
            syncArticleCount(tagsOld + "," + article.getTags(), null);

        if (categoryIdOld != article.getCategoryId()) {
            syncArticleCount(null, categoryIdOld);
            syncArticleCount(null, article.getCategoryId());
        }
        return articleRepository.save(article);
    }


    private void syncArticleCount(String tags, Integer categoryId) {

        // 根据tags要去更新每个tag的文件数
        if (StringUtils.isNotBlank(tags)) {
            Arrays.stream(tags.split(",")).collect(Collectors.toSet()).forEach(tag -> {
                if (StringUtils.isNotBlank(tag)) {
                    tagService.updateArticleCount(tag);
                }
            });
        }

        //根据category去更新每个category的数字。
        if (categoryId != null) {
//            Arrays.stream(tags.split(",")).forEach(tag -> tagService.updateArticleCount(tag));
            categoryService.updateArticleCount(categoryId);
        }
    }

//    @Override
//    @Transactional
//    public Article updateDiggCount(String id, int diff) {
//        Article article = this.findById(id);
//        if (article == null) {
//            throw new AppException(ResultEnum.DATA_NOT_EXIT);
//        }
//        article.setDiggCount(article.getDiggCount() + diff);
//        return articleRepository.save(article);
//    }

    @Override
    @Transactional
    public Article updateCommentCount(String id, int diff) {
        Article article = this.findById(id);
        if (article == null) {
            throw new AppException(ResultEnum.DATA_NOT_EXIT);
        }
        article.setCommentCount(article.getCommentCount() + diff);
        return articleRepository.save(article);
    }

    @Override
    @Transactional
    public void visit(String id) {
        Article article = this.findById(id);
        if (article == null) {
            throw new AppException(ResultEnum.DATA_NOT_EXIT);
        }
        article.setPv(article.getPv() + 1);
        articleRepository.save(article);
    }


    @Override
    @Transactional
    public void changeStatus(String id, Integer status) {
        // 先查询下当前这个活动信息
        Article article = this.findById(id);
        if (article == null) {
            throw new AppException(ResultEnum.DATA_NOT_EXIT);
        }
        articleRepository.changeStatus(id, status);

        // 同步文章数
        syncArticleCount(article.getTags(), article.getCategoryId());

    }

    @Override
    public ArticleDigg findDigg(String uid, String article) {
        return this.articleDiggRepository.findByUidAndArticleId(uid, article);
    }

    @Override
    public Pagination<ArticleDigg> findDiggList(ArticleDiggQO articleDiggQO) {
        Pageable pageable = PageRequest.of(articleDiggQO.getPageNum() - 1, articleDiggQO.getPageSize());
        Specification<ArticleDigg> specification = (Specification<ArticleDigg>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicateList = new ArrayList<>();
            //根据articleId
            if (StringUtils.isNotBlank(articleDiggQO.getArticleId())) {
                String[] cidArr = articleDiggQO.getArticleId().split(",");
                if (cidArr.length > 1) {
                    CriteriaBuilder.In<String> in = criteriaBuilder.in(root.get("articleId"));
                    for (int i = 0; i < cidArr.length; i++) {
                        in.value(cidArr[i]);
                    }
                    predicateList.add(in);
                } else {
                    predicateList.add(criteriaBuilder.equal(root.get("articleId").as(String.class), articleDiggQO.getArticleId()));
                }
            }
            //根据uid
            if (StringUtils.isNotBlank(articleDiggQO.getUid())) {
                predicateList.add(criteriaBuilder.equal(root.get("uid").as(String.class), articleDiggQO.getUid()));
            }
            Predicate[] pre = new Predicate[predicateList.size()];
            criteriaQuery.where(predicateList.toArray(pre));
            return criteriaBuilder.and(predicateList.toArray(pre));
        };
        Page<ArticleDigg> articleDiggPage = articleDiggRepository.findAll(specification, pageable);
        Pagination<ArticleDigg> articleDiggPagination = PageResultConverter.convert(articleDiggPage);
        return articleDiggPagination;
    }


    @Override
    @Transactional
    public void digg(String uid, String articleId, int isDigg) {
        // 查询是否存在，
        ArticleDigg articleDigg = this.articleDiggRepository.findByUidAndArticleId(uid, articleId);
        if ((articleDigg == null && isDigg == 0) || (articleDigg != null && isDigg == 1)) {
            return;
        }
        log.info("走了这边吗,articleDigg={}", articleDigg);
        if (isDigg == 0) {
            // 取消点赞
            this.articleDiggRepository.deleteByUidAndArticleId(uid, articleId);
        } else {
            articleDigg = new ArticleDigg();
            articleDigg.setUid(uid);
            articleDigg.setArticleId(articleId);
            log.info("又走了这边吗，articleDigg={}", articleDigg);
            this.articleDiggRepository.save(articleDigg);
        }
        log.info("走后又走了这边吗，articleDigg={}", articleDigg);

        // 2、更新文章的总点赞数
        this.updateDiggCount(articleId, isDigg == 0 ? -1 : 1);
    }


    @Override
    @Transactional
    public void updateDiggCount(String articleId, int diff) {
        Article article = this.findById(articleId);
        if (article == null) {
            throw new AppException(ResultEnum.DATA_NOT_EXIT);
        }
        article.setDiggCount((article.getDiggCount() + diff >= 0) ? article.getDiggCount() + diff : 0);
        articleRepository.save(article);
    }


}
