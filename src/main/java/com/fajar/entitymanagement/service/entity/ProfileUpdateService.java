package com.fajar.entitymanagement.service.entity;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.entitymanagement.dto.WebResponse;
import com.fajar.entitymanagement.entity.Profile;
import com.fajar.entitymanagement.repository.AppProfileRepository;

@Service
public class ProfileUpdateService extends BaseEntityUpdateService<Profile>{

	@Autowired
	private AppProfileRepository shopProfileRepository;
	
	@Override
	public WebResponse saveEntity(Profile baseEntity, boolean newRecord) {
		 Profile shopProfile =  copyNewElement(baseEntity, newRecord);
		String base64Image = shopProfile.getIconUrl();
		if (base64Image != null && !base64Image.equals("")) {
			try {
				String imageName = fileService.writeImage(baseEntity.getClass().getSimpleName(), base64Image);
				shopProfile.setIconUrl(imageName);
			} catch (IOException e) {

				shopProfile.setIconUrl(null);
				e.printStackTrace();
			}
		} else {
			if (!newRecord) {
				Optional<Profile> dbShopProfile = shopProfileRepository.findById(shopProfile.getId());
				if (dbShopProfile.isPresent()) {
					shopProfile.setIconUrl(dbShopProfile.get().getIconUrl());
				}
			}
		}
		Profile newShopProfile = entityRepository.save(shopProfile);
		return WebResponse.builder().entity(newShopProfile).build();
	}
	
}

