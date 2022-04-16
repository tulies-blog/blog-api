package com.tulies.blog.api.module.ms.controller;

import com.tulies.blog.api.beans.base.ApiResult;
import com.tulies.blog.api.beans.base.Pagination;
import com.tulies.blog.api.beans.dto.ArticleDTO;
import com.tulies.blog.api.beans.qo.ArticleQO;
import com.tulies.blog.api.beans.vo.ArticleVO;
import com.tulies.blog.api.config.BaseConfigProperties;
import com.tulies.blog.api.entity.Article;
import com.tulies.blog.api.enums.ResultEnum;
import com.tulies.blog.api.exception.AppException;
import com.tulies.blog.api.service.ArticleService;
import com.tulies.blog.api.service.FileService;
import com.tulies.blog.api.utils.ApiResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author 王嘉炀
 * @date 2022-02-18 22:24
 */
@Api(tags = "文章管理")
@Slf4j
@RestController
@RequestMapping("/ms/articles")
public class MsArticleController {
    @Autowired
    private ArticleService articleService;
    @Autowired
    private FileService fileService;
    @Autowired
    private BaseConfigProperties baseConfigProperties;

    //    @ApiImplicitParam(name = "name",value = "姓名",required = true)
    @ApiOperation(value = "文章列表")
    @GetMapping()
    public ApiResult list(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                          @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize,
                          ArticleQO articleQO,
                          String sorter) {
        Pagination<ArticleVO> pageVO = this.articleService.findByQueryDSL(pageNum - 1, pageSize, articleQO, sorter);
        return ApiResultUtil.success(pageVO);
    }


    @ApiOperation(value = "删除文章")
    @DeleteMapping("/{id}")
    public ApiResult delete(@PathVariable(name = "id") String id) {
        articleService.deleteById(id);
        ApiResult resultVO = ApiResultUtil.success();
        return resultVO;
    }

    @ApiOperation(value = "创建文章")
    @PostMapping
    public ApiResult create(@RequestBody @Valid ArticleDTO articleForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.error("【创建文章】参数不正确，articleForm={}", articleForm);
            throw new AppException(ResultEnum.PARAM_ERROR.getCode(),
                    bindingResult.getFieldError().getDefaultMessage());
        }

        if (StringUtils.isBlank(articleForm.getPoster())) {
//            articleForm.setPoster(baseConfigProperties.getFileUrlHost()+"/images/2018/11/edf1564b-a92f-40c1-903b-7ac2debe3840.jpg");
        } else if (StringUtils.isNotBlank(articleForm.getPoster()) && !articleForm.getPoster().startsWith("http") && !articleForm.getPoster().startsWith("//")) {
            //图片转换
            String realPath = fileService.moveFileToDestination(articleForm.getPoster());
//            baseConfigProperties.getFileUrlHost() +
            articleForm.setPoster(realPath);
        }

        Article create = articleService.create(articleForm);
        return ApiResultUtil.success(create);

    }

    @ApiOperation(value = "更新文章")
    @PutMapping("/{id}")
    public ApiResult update(@PathVariable String id, @RequestBody @Valid ArticleDTO articleForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.error("【更新文章】参数不正确，articleForm={}", articleForm);
            throw new AppException(ResultEnum.PARAM_ERROR.getCode(),
                    bindingResult.getFieldError().getDefaultMessage());
        }
        if (StringUtils.isNotBlank(articleForm.getPoster())
                && !articleForm.getPoster().startsWith("http")
                && !articleForm.getPoster().startsWith("//")
                && !articleForm.getPoster().startsWith("/")) {
            //临时路径的图片，说明是编辑下更改了图片的
            String realPath = fileService.moveFileToDestination(articleForm.getPoster());
            articleForm.setPoster(realPath);
        } else {
            articleForm.setPoster(null);
        }
        articleForm.setId(id);
        Article create = articleService.update(articleForm);
        return ApiResultUtil.success(create);
    }

    @ApiOperation(value = "查询文章详情")
    @GetMapping("/{id}")
    public ApiResult info(@PathVariable(name = "id") String id) {

        Article article = this.articleService.findById(id);
        if (article == null) {
            throw new AppException(ResultEnum.DATA_NOT_EXIT.getCode(), ResultEnum.DATA_NOT_EXIT.getMessage());
        }
        return ApiResultUtil.success(article);
    }

    @ApiOperation(value = "更新文章状态")
    @PatchMapping("/{id}/status/{status}")
    public ApiResult changeStatus(@PathVariable String id, @PathVariable Integer status) {

        articleService.changeStatus(id, status);
        return ApiResultUtil.success();
    }


}
