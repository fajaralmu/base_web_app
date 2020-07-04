package com.fajar.entitymanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fajar.entitymanagement.entity.ProductCategory;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {

	List<ProductCategory> findByDeletedFalse();

}
