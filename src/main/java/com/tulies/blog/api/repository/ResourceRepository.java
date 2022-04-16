package com.tulies.blog.api.repository;

import com.tulies.blog.api.entity.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ResourceRepository extends JpaRepository<Resource,Long>, JpaSpecificationExecutor {

//    @Query("select r from Resource r where t.id = :id")
//    Resource findById(Long id);
}
