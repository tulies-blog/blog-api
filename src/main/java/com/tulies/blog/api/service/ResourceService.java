package com.tulies.blog.api.service;

import com.tulies.blog.api.beans.base.Pagination;
import com.tulies.blog.api.beans.qo.ResourceQO;
import com.tulies.blog.api.entity.Resource;

public interface ResourceService {
    Resource findById(Long id);
    void deleteById(Long id);

//    查询列表
    Pagination<Resource> findList(Integer pageNum, Integer pageSize, ResourceQO resourceQo, String sorter);

//    新增文件
    Resource create(Resource Resource);
}
