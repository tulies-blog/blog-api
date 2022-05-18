package com.tulies.blog.api.service.impl;

import com.tulies.blog.api.beans.base.Pagination;
import com.tulies.blog.api.beans.dto.TagDTO;
import com.tulies.blog.api.beans.qo.ArticleQO;
import com.tulies.blog.api.beans.qo.TagQO;
import com.tulies.blog.api.converter.PageResultConverter;
import com.tulies.blog.api.entity.Tag;
import com.tulies.blog.api.enums.ResultEnum;
import com.tulies.blog.api.exception.AppException;
import com.tulies.blog.api.repository.TagRepository;
import com.tulies.blog.api.service.ArticleService;
import com.tulies.blog.api.service.TagService;
import com.tulies.blog.api.utils.BeanUtil;
import com.tulies.blog.api.utils.CommUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TagServiceImpl implements TagService {
    @Autowired
    private TagRepository tagRepository;

    @Lazy
    @Autowired
    private ArticleService articleService;

    @Override
    public Pagination<Tag> findList(Integer pageNum, Integer pageSize, TagQO tagQO, String sorter) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        if (StringUtils.isNotBlank(sorter)) {
            sort = CommUtil.formatSorter(sorter);
        }
        Pageable pageable = PageRequest.of(pageNum, pageSize, sort);

        Specification<Tag> specification = (Specification<Tag>) (root, criteriaQuery, criteriaBuilder) -> {

            List<Predicate> predicateList = new ArrayList<>();
            //根据id 查询
            if (tagQO.getId() != null) {
                predicateList.add(criteriaBuilder.equal(root.get("id").as(Long.class), tagQO.getId()));
            }

            //根据name 模糊匹配
            if (StringUtils.isNotBlank(tagQO.getName())) {
                predicateList.add(criteriaBuilder.equal(root.get("name").as(String.class), tagQO.getName()));
            }
            Predicate[] pre = new Predicate[predicateList.size()];
            criteriaQuery.where(predicateList.toArray(pre));
            return criteriaBuilder.and(predicateList.toArray(pre));
        };

        Page<Tag> page = tagRepository.findAll(specification, pageable);
        Pagination<Tag> pageVO = PageResultConverter.convert(page);
        return pageVO;
    }

    @Override
    public Tag findById(Integer id) {
        Optional<Tag> record = tagRepository.findById(id);
        if (!record.isPresent()) {
            return null;
        }
        return record.get();
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        tagRepository.deleteById(id);
    }

    @Override
    public Tag create(TagDTO tagDTO) {
        Tag tag = new Tag();
        BeanUtil.copyProperties(tagDTO, tag);
        return tagRepository.save(tag);
    }

    @Override
    public Tag update(TagDTO tagDTO) {
        Tag tag = this.findById(tagDTO.getId());
        if (tag == null) {
            throw new AppException(ResultEnum.DATA_NOT_EXIT.getCode(), ResultEnum.DATA_NOT_EXIT.getMessage());
        }
        BeanUtil.copyProperties(tagDTO, tag,true);
        return tagRepository.save(tag);
    }

    @Override
    public void updateArticleCount(String name) {
        // 先查询这个tag是否存在
        Tag tag = tagRepository.findByName(name);
        if (tag == null) {
            return;
        }
        // 先查询这个文章下面的count
        ArticleQO articleQO = new ArticleQO();
        articleQO.setTags(name);
        articleQO.setStatus("1");
        long count = articleService.count(articleQO);

        // 更新文章count
        tag.setArticleCount(count);
        tagRepository.save(tag);
    }
}
