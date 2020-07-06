package com.fajar.entitymanagement.service.entity;

import org.springframework.stereotype.Service;

import com.fajar.entitymanagement.dto.WebResponse;
import com.fajar.entitymanagement.entity.User;

@Service
public class UserUpdateService extends BaseEntityUpdateService<User> {

	@Override
	public WebResponse saveEntity(User baseEntity, boolean newRecord) {
		User user = copyNewElement(baseEntity, newRecord);
		User newUser = entityRepository.save(user);
		return WebResponse.builder().entity(newUser).build();
	}
}
