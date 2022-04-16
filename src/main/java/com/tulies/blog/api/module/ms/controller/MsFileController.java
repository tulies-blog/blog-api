package com.tulies.blog.api.module.ms.controller;

import com.tulies.blog.api.beans.base.ApiResult;
import com.tulies.blog.api.beans.vo.FileVO;
import com.tulies.blog.api.config.BaseConfigProperties;
import com.tulies.blog.api.service.FileService;
import com.tulies.blog.api.utils.ApiResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author 王嘉炀
 * @date 2022/2/19 上午10:12
 */

@Api(tags = "上传管理")
@CrossOrigin
@Controller
@RequestMapping("/ms/file")
public class MsFileController {

    @Autowired
    private FileService fileService;

    @Autowired
    private BaseConfigProperties baseConfigProperties;

    @ApiOperation(value = "上传至临时缓冲区")
    @PostMapping("/upload")
    @ResponseBody
//    @IgnoreSecurity
    public ApiResult fileUpload(@RequestParam("file") MultipartFile multipartFile){
        FileVO fileVO = fileService.fileUpload(multipartFile);
        return ApiResultUtil.success(fileVO);
    }


    @ApiOperation(value = "上传至正式目录")
    @PostMapping("/uploadFormal")
    @ResponseBody
    public ApiResult fileUploadFormal(@RequestParam("file") MultipartFile multipartFile){
        FileVO fileVO = fileService.fileUpload(multipartFile);
        String realPath = fileService.moveFileToDestination(fileVO.getBaseUrl());
        String fileUrlHost = baseConfigProperties.getFileUrlHost();

        // 返回baseUrl+url
        fileVO.setBaseUrl(realPath);
        fileVO.setUrl(fileUrlHost+realPath);
        return ApiResultUtil.success(fileVO);
    }

}
