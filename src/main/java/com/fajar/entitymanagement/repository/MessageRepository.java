package com.fajar.entitymanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fajar.entitymanagement.entity.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {

	List<Message> findByRequestId(String requestId);

}
