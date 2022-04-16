package com.tulies.blog.api.service.impl;

import com.tulies.blog.api.beans.base.Pagination;
import com.tulies.blog.api.beans.qo.ResourceQO;
import com.tulies.blog.api.converter.PageResultConverter;
import com.tulies.blog.api.entity.Resource;
import com.tulies.blog.api.repository.ResourceRepository;
import com.tulies.blog.api.service.ResourceService;
import com.tulies.blog.api.utils.CommUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ResourceServiceImpl implements ResourceService {

    @Autowired
    private ResourceRepository resourceRepository;

    @Override
    public Resource findById(Long id) {
        /**
         * 这里注意写法
         *
         * 根据ID查询使用的方法是:Optional<T> findById(ID id)-->T t = Optional<T>.get();
         *  Optional<T>是非null的,但是如果查不到的话,它的get方法会报错,no value present;
         * 所以在进行get之前,需要使用Optional.isPresent()方法进行判断
         */
        Optional<Resource> record = resourceRepository.findById(id);
        if(!record.isPresent()){
            return null;
        }

        return record.get();
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        resourceRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Resource create(Resource resource) {
        resource.setCreateTime(new Date());
        resource.setUpdateTime(new Date());
        Resource result = resourceRepository.save(resource);
        return result;
    }

    @Override
    public Pagination<Resource> findList(Integer pageNum, Integer pageSize, ResourceQO resourceQo, String sorter) {
        Sort sort = Sort.by(Sort.Direction.DESC,"id");
        if(StringUtils.isNotBlank(sorter)){
            sort = CommUtil.formatSorter(sorter);
        }
        Pageable pageable = PageRequest.of(pageNum, pageSize, sort);

        Specification<Resource> specification = (Specification<Resource>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicateList = new ArrayList<>();
            //根据actid 查询
            if (resourceQo.getId() != null) {
                predicateList.add(criteriaBuilder.equal(root.get("id").as(Long.class), resourceQo.getId()));
            }

            //根据name 模糊匹配
            if (StringUtils.isNotBlank(resourceQo.getName())) {
                predicateList.add(criteriaBuilder.like(root.get("name").as(String.class), "%"+ resourceQo.getName()+"%"));
            }

            //根据relativePath 模糊匹配
            if (StringUtils.isNotBlank(resourceQo.getRelativePath())) {
                predicateList.add(criteriaBuilder.like(root.get("relativePath").as(String.class), "%"+ resourceQo.getRelativePath()+"%"));
            }
            //根据上传来源查询
            if(StringUtils.isNotBlank(resourceQo.getApply())){
                predicateList.add(criteriaBuilder.equal(root.get("apply").as(String.class),resourceQo.getApply()));
            }
//
//            //根据创建时间查询
//            if(StringUtils.isNotBlank(resourceQo.getCreateTime())){
//                predicateList.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createTime").as(String.class),resourceQo.getCreateTime()));
//            }
//            //根据更新时间查询
//            if(StringUtils.isNotBlank(resourceQo.getUpdateTime())){
//                predicateList.add(criteriaBuilder.greaterThanOrEqualTo(root.get("updateTime").as(String.class),resourceQo.getUpdateTime()));
//            }


            Predicate[] pre = new Predicate[predicateList.size()];
            criteriaQuery.where(predicateList.toArray(pre));
            return criteriaBuilder.and(predicateList.toArray(pre));
        };

        Page<Resource> actinfoPage = resourceRepository.findAll(specification,pageable);
        return PageResultConverter.convert(actinfoPage);
    }
}
