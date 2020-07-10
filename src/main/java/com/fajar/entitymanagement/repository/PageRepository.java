package com.fajar.entitymanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fajar.entitymanagement.entity.Page;

public interface PageRepository extends JpaRepository<Page, Long> {

	Page findByCode(String code);

	List<Page> findByAuthorized(int i);

//	@Query(nativeQuery = true, value="select * from page where link = ?1 limit 1")
	Page findTop1ByLink(String link);

	List<Page> findByOrderBySequenceAsc();

}
