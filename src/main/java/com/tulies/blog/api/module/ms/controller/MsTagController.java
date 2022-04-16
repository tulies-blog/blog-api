package com.tulies.blog.api.module.ms.controller;

import com.tulies.blog.api.beans.base.ApiResult;
import com.tulies.blog.api.beans.base.Pagination;
import com.tulies.blog.api.beans.dto.TagDTO;
import com.tulies.blog.api.beans.qo.TagQO;
import com.tulies.blog.api.entity.Tag;
import com.tulies.blog.api.enums.ResultEnum;
import com.tulies.blog.api.exception.AppException;
import com.tulies.blog.api.service.FileService;
import com.tulies.blog.api.service.TagService;
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
 * @date 2019-10-13 15:45
 */
@Api(tags = "标签管理")
@Slf4j
@RestController
@RequestMapping("/ms/tags")
public class MsTagController {
    @Autowired
    private TagService tagService;
    @Autowired
    private FileService fileService;

    @ApiOperation(value = "标签列表")
    @GetMapping
    public ApiResult list(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                          @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize,
                          TagQO tagQO,
                          String sorter) {
        Pagination<Tag> pagination = this.tagService.findList(pageNum - 1, pageSize, tagQO, sorter);
        return ApiResultUtil.success(pagination);
    }

    @ApiOperation(value = "删除标签")
    @DeleteMapping("/{id}")
    public ApiResult delete(@PathVariable Integer id) {

        // 先查询下当前这个活动信息
        Tag record = tagService.findById(id);
        if (record == null) {
            throw new AppException(ResultEnum.DATA_NOT_EXIT);
        }

        tagService.deleteById(id);
        return ApiResultUtil.success();
    }

    @ApiOperation(value = "创建标签")
    @PostMapping()
    public ApiResult create(@RequestBody @Valid TagDTO tagDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.error("【创建标签】参数不正确，tagDTO={}", tagDTO);
            throw new AppException(ResultEnum.PARAM_ERROR.getCode(),
                    bindingResult.getFieldError().getDefaultMessage());
        }

        if (StringUtils.isNotBlank(tagDTO.getCover()) && !tagDTO.getCover().startsWith("http") && !tagDTO.getCover().startsWith("//")) {
            //图片转换
            String realPath = fileService.moveFileToDestination(tagDTO.getCover());
//            baseConfigProperties.getFileUrlHost() +
            tagDTO.setCover(realPath);
        }

        return ApiResultUtil.success(tagService.create(tagDTO));
    }

    @ApiOperation(value = "更新标签")
    @PutMapping("/{id}")
    public ApiResult update(@PathVariable Integer id, @RequestBody @Valid TagDTO tagDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.error("【更新标签】参数不正确，tagDTO={}", tagDTO);
            throw new AppException(ResultEnum.PARAM_ERROR.getCode(),
                    bindingResult.getFieldError().getDefaultMessage());
        }
        tagDTO.setId(id);
        if (StringUtils.isNotBlank(tagDTO.getCover())
                && !tagDTO.getCover().startsWith("http")
                && !tagDTO.getCover().startsWith("//")
                && !tagDTO.getCover().startsWith("/")) {
            //临时路径的图片，说明是编辑下更改了图片的
            String realPath = fileService.moveFileToDestination(tagDTO.getCover());
            tagDTO.setCover(realPath);
        } else {
            tagDTO.setCover(null);
        }
        return ApiResultUtil.success(tagService.update(tagDTO));
    }
}
