package com.tulies.blog.api.beans.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tulies.blog.api.entity.Category;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 王嘉炀
 * @date 2019-10-08 22:16
 */

@Data
public class CategoryNodeVO {
    private Integer id;
    private String name;
    private String urlName;
    private Integer status;
    private Integer sort;
    private Long articleCount;
    private Integer parentId;
    private List<CategoryNodeVO> children;
    private CategoryNodeVO parent;


    @JsonIgnore
    public static List<CategoryNodeVO> findChild(List<CategoryNodeVO> categoryNodeVOList, Integer parentId, CategoryNodeVO parent) {
        CategoryNodeVO newvo = new CategoryNodeVO();
        if (parent != null) {
            BeanUtils.copyProperties(parent, newvo);
            newvo.setChildren(null);
        }
        List<CategoryNodeVO> categoryNodeVOS = categoryNodeVOList.stream().filter(p -> p.getParentId().equals(parentId)).collect(Collectors.toList());
        if (categoryNodeVOS == null || categoryNodeVOS.size() <= 0) {
            return null;
        }
        List<CategoryNodeVO> result = categoryNodeVOS.stream().map(p -> {
            p.setChildren(findChild(categoryNodeVOList, p.getId(), p));
            if (parent != null) {
                p.setParent(newvo);
            } else {
                p.setParent(null);
            }
            return p;
        }).collect(Collectors.toList());
        return result;
    }

    @JsonIgnore
    public static List<CategoryNodeVO> tree(List<Category> categoryList, Integer rootId) {
        List<CategoryNodeVO> categoryNodeVOS = categoryList.stream().map((p) -> {
            CategoryNodeVO pVO = new CategoryNodeVO();
            BeanUtils.copyProperties(p, pVO);
            return pVO;
        }).collect(Collectors.toList());

        List<CategoryNodeVO> categoryNodeVOList = findChild(categoryNodeVOS, rootId, null);

        return categoryNodeVOList;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
