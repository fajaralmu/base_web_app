package com.fajar.entitymanagement.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.fajar.entitymanagement.entity.User;

public interface UserRepository extends JpaRepository< User	, Long> {

	User findByUsernameAndPassword(String username, String password);

	User findByUsername(String username);
	
}