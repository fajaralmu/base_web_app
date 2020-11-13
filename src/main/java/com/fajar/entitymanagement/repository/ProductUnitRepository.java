package com.fajar.entitymanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fajar.entitymanagement.entity.Unit;

public interface ProductUnitRepository extends JpaRepository<Unit, Long> {

}
