package com.fajar.entitymanagement.service.entity;

import org.springframework.stereotype.Service;

import com.fajar.entitymanagement.dto.WebResponse;
import com.fajar.entitymanagement.entity.Profile;

@Service
public class ProfileUpdateService extends BaseEntityUpdateService<Profile>{
 
	
	@Override
	public WebResponse saveEntity(Profile baseEntity, boolean newRecord) {
		 Profile shopProfile =  copyNewElement(baseEntity, newRecord);
		 validateEntityFields(shopProfile, newRecord);
		Profile newShopProfile = entityRepository.save(shopProfile);
		return WebResponse.builder().entity(newShopProfile).build();
	}
	
}

