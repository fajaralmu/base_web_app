package com.fajar.entitymanagement.service.entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.entitymanagement.dto.WebResponse;
import com.fajar.entitymanagement.entity.BaseEntity;
import com.fajar.entitymanagement.entity.User;
import com.fajar.entitymanagement.repository.UserRepository;

@Service
public class UserUpdateService extends BaseEntityUpdateService{

	@Autowired
	private UserRepository userRepository;
	
	@Override
	public WebResponse saveEntity(BaseEntity baseEntity, boolean newRecord,EntityUpdateInterceptor entityUpdateInterceptor) {
		User user = (User) copyNewElement(baseEntity, newRecord);
		User newUser = entityRepository.save(user);
		return WebResponse.builder().entity(newUser).build();
	}
}
