package com.fajar.entitymanagement.service.entity;

import org.springframework.stereotype.Service;

import com.fajar.entitymanagement.dto.WebResponse;
import com.fajar.entitymanagement.entity.BaseEntity;
import com.fajar.entitymanagement.entity.User;

@Service
public class UserUpdateService extends BaseEntityUpdateService {

	@Override
	public WebResponse saveEntity(BaseEntity baseEntity, boolean newRecord,
			EntityUpdateInterceptor entityUpdateInterceptor) {
		User user = (User) copyNewElement(baseEntity, newRecord);
		User newUser = entityRepository.save(user);
		return WebResponse.builder().entity(newUser).build();
	}
}
