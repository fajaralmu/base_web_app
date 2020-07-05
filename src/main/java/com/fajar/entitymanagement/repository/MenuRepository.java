package com.fajar.entitymanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fajar.entitymanagement.entity.Menu;

public interface MenuRepository extends JpaRepository<Menu, Long> {

 
	List<Menu> findByMenuPage_code(String code);

	Menu findTop1ByUrl(String url);

	Menu findByCode(String code);

}
