package com.fajar.entitymanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fajar.entitymanagement.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

}