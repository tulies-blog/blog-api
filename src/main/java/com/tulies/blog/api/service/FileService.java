package com.tulies.blog.api.service;

import com.tulies.blog.api.beans.vo.FileVO;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author 王嘉炀
 * @date 2018/7/24 下午1:12
 */
public interface FileService {

    /**
     * 文件上传，上传到临时目录
     * @param file
     * @return
     */
    FileVO fileUpload(MultipartFile file);


    /**
     * 临时目录拷贝到正式目录
     * @param tempFileName
     * @return 正式路径
     */
    String moveFileToDestination(String tempFileName);


}
