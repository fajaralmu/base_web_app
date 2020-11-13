package com.fajar.entitymanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fajar.entitymanagement.entity.Category;

public interface ProductCategoryRepository extends JpaRepository<Category, Long> {

	List<Category> findByDeletedFalse();

}
