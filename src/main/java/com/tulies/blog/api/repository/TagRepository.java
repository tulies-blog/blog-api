package com.tulies.blog.api.repository;

import com.tulies.blog.api.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TagRepository extends JpaRepository<Tag, Integer>, JpaSpecificationExecutor {
    Tag findByName(String name);
}
