package com.tulies.blog.api.module.ms.controller;

import com.tulies.blog.api.beans.base.ApiResult;
import com.tulies.blog.api.beans.base.Pagination;
import com.tulies.blog.api.enums.ResultEnum;
import com.tulies.blog.api.beans.qo.ResourceQO;
import com.tulies.blog.api.beans.vo.FileVO;
import com.tulies.blog.api.config.BaseConfigProperties;
import com.tulies.blog.api.entity.Resource;
import com.tulies.blog.api.exception.AppException;
import com.tulies.blog.api.service.FileService;
import com.tulies.blog.api.service.ResourceService;
import com.tulies.blog.api.utils.ApiResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author 王嘉炀
 * @date 2019-08-19 22:40
 */
@Api(tags = "资源管理")
@Slf4j
@RestController
@RequestMapping("/ms/resources")
public class MsResourceController {

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private FileService fileService;

    @Autowired
    private BaseConfigProperties baseConfigProperties;

    @ApiOperation(value = "资源列表")
    @GetMapping
    public ApiResult list(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                          @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize,
                          ResourceQO resourceQo,
                          String sorter){
        Pagination<Resource> resourcePageVO = this.resourceService.findList(pageNum - 1, pageSize, resourceQo, sorter);
        return ApiResultUtil.success(resourcePageVO);
    }

    @ApiOperation(value = "删除资源")
    @DeleteMapping("/{id}")
    public ApiResult delete(@PathVariable Long id){
        // 先查询下当前这个活动信息
        Resource record = resourceService.findById(id);
        if (record == null) {
            throw new AppException(ResultEnum.DATA_NOT_EXIT);
        }
        resourceService.deleteById(id);
        return ApiResultUtil.success();
    }

    @ApiOperation(value = "上传资源")
    @CrossOrigin
    @PostMapping("/upload")
    @ResponseBody
    public ApiResult fileUploadFormal(@RequestParam("file") MultipartFile multipartFile) throws IOException {
        log.info("文件信息：{},{},{},{},{}",
                multipartFile.getSize(),
                multipartFile.getOriginalFilename(),
                multipartFile.getContentType());

        FileVO fileVO = fileService.fileUpload(multipartFile);
        String realPath = fileService.moveFileToDestination(fileVO.getBaseUrl());

        String fileUrlHost = baseConfigProperties.getFileUrlHost();
        log.info("fileUrlHost={}", realPath);

        // 返回baseUrl+url
//        fileDTO.setBaseUrl(realPath);
//        fileDTO.setUrl(fileUrlHost+realPath);
        Resource resource = new Resource();
        resource.setName(multipartFile.getOriginalFilename());
        resource.setSize(multipartFile.getSize());
        resource.setRelativePath(realPath);
        resource.setContentType(multipartFile.getContentType());
        Resource result = resourceService.create(resource);
        return ApiResultUtil.success(result);
    }



}
