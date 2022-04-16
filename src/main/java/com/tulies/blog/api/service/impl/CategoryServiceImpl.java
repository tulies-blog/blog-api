package com.tulies.blog.api.service.impl;

import com.tulies.blog.api.beans.qo.ArticleQO;
import com.tulies.blog.api.beans.vo.CategoryNodeVO;
import com.tulies.blog.api.entity.Category;
import com.tulies.blog.api.repository.CategoryRepository;
import com.tulies.blog.api.service.ArticleService;
import com.tulies.blog.api.service.CategoryService;
import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.query.spi.NativeQueryImplementor;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author 王嘉炀
 * @date 2019-10-12 00:09
 */
@Service
public class CategoryServiceImpl implements CategoryService {
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private CategoryRepository categoryRepository;

    @Lazy
    @Autowired
    private ArticleService articleService;

    @Override
    public List<Category> findAll() {
//        this.categoryRepository.findAll()
        Sort sort = Sort.by(Sort.Direction.DESC, "sort");
        return this.categoryRepository.findAll(sort);
    }


    @Override
    public List<CategoryNodeVO> tree() {
        Integer rootId = 0;
        List<Category> categoryList = this.findAll();
        List<CategoryNodeVO> categoryNodeVOList = CategoryNodeVO.tree(categoryList, 0);
        return categoryNodeVOList;
    }


    @Override
    public List<Map<String, Object>> articleCount() {
        String querySql = "select c.*,IFNULL(a.count,0) count from blog_category c left JOIN (select category_id ,COUNT(*) count from blog_article WHERE status=1 GROUP BY category_id) a on c.id = a.category_id ORDER BY c.sort desc,c.id asc";
        Query nativeQuery = entityManager.createNativeQuery(querySql.toString());
        NativeQueryImplementor nativeQueryImplementor = nativeQuery.unwrap(NativeQueryImpl.class)
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        List<Map<String, Object>> resultMap = nativeQueryImplementor.getResultList();
        entityManager.clear();
        return resultMap;
    }

    @Override
    public Category findByUrlName(String urlName) {
        return categoryRepository.findByUrlName(urlName);
    }

    @Override
    public Category findById(Integer id) {
        Optional<Category> record = categoryRepository.findById(id);
        if (!record.isPresent()) {
            return null;
        }
        return record.get();
    }

    @Override
    public void updateArticleCount(Integer id) {
        // 先查询这个tag是否存在
        Category category = this.findById(id);
        if (category == null) {
            return;
        }
        // 先查询这个文章下面的count
        ArticleQO articleQO = new ArticleQO();
        articleQO.setCategoryId(id);
        long count = articleService.count(articleQO);

        // 更新文章count
        category.setArticleCount(count);
        categoryRepository.save(category);
    }
}
