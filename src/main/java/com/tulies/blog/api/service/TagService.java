package com.tulies.blog.api.service;

import com.tulies.blog.api.beans.base.Pagination;
import com.tulies.blog.api.beans.dto.TagDTO;
import com.tulies.blog.api.beans.qo.TagQO;
import com.tulies.blog.api.entity.Tag;

/**
 * @author 王嘉炀
 * @date 2019-10-12 00:07
 */
public interface TagService {
    Pagination<Tag> findList(Integer pageNum, Integer pageSize, TagQO tagQO, String sorter);

    Tag findById(Integer id);

    void deleteById(Integer id);

    Tag create(TagDTO tagDTO);

    Tag update(TagDTO tagDTO);

    void updateArticleCount(String name);
}
