package com.fajar.entitymanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fajar.entitymanagement.entity.Profile;

public interface AppProfileRepository extends JpaRepository<Profile, Long> {

	Profile findByAppCode(String martCode);

}
