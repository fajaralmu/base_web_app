package com.fajar.entitymanagement.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.fajar.entitymanagement.entity.UserRole;

public interface UserRoleRepository extends JpaRepository< UserRole	, Long> {

	UserRole findByCode(String roleCode);

	 
	
}