package com.tulies.blog.api.service.impl;


import com.tulies.blog.api.beans.vo.FileVO;
import com.tulies.blog.api.config.BaseConfigProperties;
import com.tulies.blog.api.service.FileService;
import com.tulies.blog.api.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;


/**
 * @author 王嘉炀
 * @date 2018/7/24 下午1:24
 */
@Service
@Slf4j
public class FileServiceImpl implements FileService {

    @Autowired
    private BaseConfigProperties baseConfigProperties;



    /**
     * 文件上传，上传到临时目录
     * @param file
     * @return
     */
    @Override
    @Transactional
    public FileVO fileUpload(MultipartFile file) {
        if (file.isEmpty()) {
            log.info("文件为空");
        }

        // 获取文件名
        String originalFilename = file.getOriginalFilename();
        // 获取文件的后缀名
        String suffixName = originalFilename.substring(originalFilename.lastIndexOf(".")+1);
        //生成新的文件名称
        String newFileName = UUID.randomUUID().toString().replaceAll("-", "") +"." + suffixName;

        //从配置文件获取临时目录路径
        String tempPath = baseConfigProperties.getUploadTempPrefix();

        //生成新文件
        File dest = new File(tempPath +File.separator+ newFileName);

        // 检测是否存在目录
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }

        try {
            file.transferTo(dest);

            //判断图片大小，超过150k，进行图片压缩
//            if(dest.length()>150*1024){
//                Thumbnails.of(dest.getAbsolutePath()).scale(1f).outputQuality(0.04f).toFile(dest);
//            }

            FileVO fileVO=new FileVO();
            fileVO.setBaseUrl(newFileName);

            return fileVO;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    @Transactional
    public String moveFileToDestination(String tempFileName) {
        if(StringUtils.isBlank(tempFileName)){
            return null;
        }

        //从配置文件获取文件真实路径
        String uploadPathPrefix =baseConfigProperties.getUploadPathPrefix();
        String filePathPrefix= baseConfigProperties.getFilePathPrefix();
        String tempPath = baseConfigProperties.getUploadTempPrefix();

        File tempFile = new File(tempPath + "/" +tempFileName);

        if(!tempFile.exists()){
            return null;
        }

        String newFileName = DateUtil.getStrFromDate("yyyy")+ "/" + DateUtil.getStrFromDate("MM")+ "/" +UUID.randomUUID() + tempFileName.substring(tempFileName.lastIndexOf("."));

        File destFile = new File(uploadPathPrefix +filePathPrefix+ "/" +newFileName);
        try {
            FileUtils.copyFile(tempFile,destFile);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
//        return destFile.getAbsolutePath();
        return filePathPrefix+ "/" +newFileName;


    }




}
