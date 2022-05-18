package com.tulies.blog.api.module.app.controller;

import com.tulies.blog.api.annotation.CurrentUser;
import com.tulies.blog.api.beans.base.ApiResult;
import com.tulies.blog.api.beans.base.Pagination;
import com.tulies.blog.api.beans.base.UserBean;
import com.tulies.blog.api.beans.qo.ArticleDiggQO;
import com.tulies.blog.api.beans.qo.ArticleQO;
import com.tulies.blog.api.beans.qo.TagQO;
import com.tulies.blog.api.beans.vo.ArticleVO;
import com.tulies.blog.api.entity.Article;
import com.tulies.blog.api.entity.ArticleDigg;
import com.tulies.blog.api.entity.Tag;
import com.tulies.blog.api.enums.CommEnum;
import com.tulies.blog.api.enums.ResultEnum;
import com.tulies.blog.api.exception.AppException;
import com.tulies.blog.api.module.app.beans.qo.AppArticleQO;
import com.tulies.blog.api.service.ArticleService;
import com.tulies.blog.api.service.CategoryService;
import com.tulies.blog.api.service.TagService;
import com.tulies.blog.api.utils.ApiResultUtil;
import com.tulies.blog.api.utils.BeanUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 王嘉炀
 * @date 2022-02-18 22:24
 */

@Slf4j
@Api(tags = "文章模块")
@RestController
@RequestMapping("/app")
public class AppArticleController {
    @Autowired
    private ArticleService articleService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private TagService tagService;

    @ApiOperation(value = "文章列表查询")
    @GetMapping("/article/list")
    public ApiResult list(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                          @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                          AppArticleQO appArticleQO,
                          @RequestParam(value = "sorter", defaultValue = "createTime desc") String sorter,
                          @CurrentUser UserBean userBean
    ) {
        ArticleQO articleQO = new ArticleQO();
        log.info("appArticleQO==={}", appArticleQO);
        BeanUtil.copyProperties(appArticleQO, articleQO);
        log.info("articleQO==={}", articleQO);

        // 状态为发布状态的
        articleQO.setStatus(CommEnum.STATUS_ONLINE.getCode().toString());
        // 只取在列表中允许显示的文章
        articleQO.setIsShow(1);
        // 1、查询到文章的列表数据
        Pagination<ArticleVO> articleVOPagination = this.articleService.findByQueryDSL(pageNum - 1, pageSize, articleQO, sorter);


        // 根据查到的数据，再去查下用户行为数据。
        if (articleVOPagination.getList().size() > 0 && userBean != null) {
            String ids = articleVOPagination.getList().stream().map(v -> v.getId()).collect(Collectors.joining(","));
            ArticleDiggQO articleDiggQO = new ArticleDiggQO();
            articleDiggQO.setUid(userBean.getUid());
            articleDiggQO.setArticleId(ids);
            Pagination<ArticleDigg> articleInteractPagination = articleService.findDiggList(articleDiggQO);
            List<String> idList = articleInteractPagination.getList().stream().map(v -> v.getArticleId()).collect(Collectors.toList());

//            Map<String, ArticleDigg> articleInteractMap = new HashMap<String, ArticleDigg>();
//            for (ArticleInteract articleInteract : articleInteractPagination.getList()) {
//                articleInteractMap.put(articleInteract.getArticleId(), articleInteract);
//            }
            List<ArticleVO> articleVOList = articleVOPagination.getList().stream().map(d -> {
                d.setIsDigg(idList.contains(d.getId()) ? 1 : 0);
                return d;
            }).collect(Collectors.toList());
            articleVOPagination.setList(articleVOList);
        }

        return ApiResultUtil.success(articleVOPagination);
    }

    @ApiOperation(value = "相关文章查询")
    @GetMapping("/article/related")
    public ApiResult related(@RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
                             @RequestParam String id) {
        List<ArticleVO> articleVOList = new ArrayList<ArticleVO>();
        Article article = articleService.findById(id);
        if (article == null) {
            return ApiResultUtil.success(articleVOList);
        }
        ArticleQO articleQO = new ArticleQO();
        articleQO.setStatus(CommEnum.STATUS_ONLINE.getCode().toString());
        articleQO.setIsShow(1);
        // 不能包含自己这个文章
        articleQO.setNeId(article.getId());
        // todo 先根据分类 和 tag一起查询，
        articleQO.setCategoryId(article.getCategoryId());
        articleQO.setTags(article.getTags());

        Pagination<ArticleVO> pageVO = this.articleService.findByQueryDSL(0, pageSize, articleQO, "isTop desc,createTime desc");
        if (pageVO.getTotal() == pageSize.longValue()) {
            return ApiResultUtil.success(pageVO.getList());
        }
        articleVOList.addAll(pageVO.getList());
        // todo 不足再根据分类名称查询
        articleQO.setTags(null);
        articleQO.setNiIds(articleVOList.stream().map(a -> a.getId()).collect(Collectors.joining(",")));
        pageVO = this.articleService.findByQueryDSL(0, pageSize - articleVOList.size(), articleQO, "isTop desc,createTime desc");
        articleVOList.addAll(pageVO.getList());
        if (pageVO.getTotal() == pageSize.longValue() - articleVOList.size()) {
            return ApiResultUtil.success(articleVOList);
        }
        // todo 不足再根据分类名称查询
        articleQO.setTags(article.getTags());
        articleQO.setCategoryId(null);
        articleQO.setNiIds(articleVOList.stream().map(a -> a.getId()).collect(Collectors.joining(",")));
        pageVO = this.articleService.findByQueryDSL(0, pageSize - articleVOList.size(), articleQO, "isTop desc,createTime desc");
        articleVOList.addAll(pageVO.getList());
//        return ApiResultUtil.success(articleVOList);
        if (pageVO.getTotal() == pageSize.longValue() - articleVOList.size()) {
            return ApiResultUtil.success(articleVOList);
        }
        // todo 再不足就再根据keyword检索
        // 暂时文章没有加关键字 就先不管了
        // todo 再不足全局搜索点文章。
        articleQO.setTags(null);
        articleQO.setCategoryId(null);
        articleQO.setNiIds(articleVOList.stream().map(a -> a.getId()).collect(Collectors.joining(",")));
        pageVO = this.articleService.findByQueryDSL(0, pageSize - articleVOList.size(), articleQO, "isTop desc,createTime desc");
        articleVOList.addAll(pageVO.getList());
        return ApiResultUtil.success(articleVOList);
    }

    @ApiOperation(value = "文章详情")
    @GetMapping("/article/info/{id}")
    public ApiResult info(@PathVariable String id, @CurrentUser UserBean userBean) {
        Article article = this.articleService.findById(id);
        if (article == null) {
            throw new AppException(ResultEnum.DATA_NOT_EXIT.getCode(), ResultEnum.DATA_NOT_EXIT.getMessage());
        }
        ArticleVO articleVO = new ArticleVO();
        BeanUtil.copyProperties(article, articleVO);
        if (userBean != null) {
            ArticleDigg articleDigg = articleService.findDigg(userBean.getUid(), article.getId());
            articleVO.setIsDigg(articleDigg != null ? 1 : 0);
        }
        return ApiResultUtil.success(articleVO);
    }

    @ApiOperation(value = "文章分类")
    @GetMapping("/category/list")
    public ApiResult categoryList() {
        return ApiResultUtil.success(categoryService.findAll());
    }


    @ApiOperation(value = "分类文章汇总")
    @GetMapping("/category/articleCount")
    public ApiResult categoryArticleCount() {
        return ApiResultUtil.success(categoryService.articleCount());
    }


    @ApiOperation(value = "文章标签列表")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "name", required = true)
//    })
    @GetMapping("/tag/list")
    public ApiResult list(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "20") Integer pageSize,
                          String name,
                          @RequestParam(defaultValue = "sort desc") String sorter) {
        TagQO tagQO = new TagQO();
        tagQO.setName(name);
        tagQO.setStatus(CommEnum.STATUS_ONLINE.getCode());
        Pagination<Tag> pagination = tagService.findList(pageNum - 1, pageSize, tagQO, sorter);
        return ApiResultUtil.success(pagination);
    }

    /**************************
     * 下面是一些文章的用户行为操错
     ***************************/

    @ApiOperation(value = "文章点赞/取消点赞")
    @PostMapping("/article/digg/{articleId}/{isDigg}")
    public ApiResult digg(@PathVariable String articleId, @PathVariable Integer isDigg, @CurrentUser UserBean userBean) {
        articleService.digg(userBean.getUid(), articleId, isDigg);
        return ApiResultUtil.success();
    }

    @ApiOperation(value = "文章访问数增加")
    @PostMapping("/article/visit/{articleId}")
    public ApiResult visit(@PathVariable String articleId, @CurrentUser UserBean userBean) {
        articleService.visit(articleId);
        return ApiResultUtil.success();
    }
}
