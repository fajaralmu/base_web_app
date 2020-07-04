package com.fajar.entitymanagement.service;

import static com.fajar.entitymanagement.util.MvcUtil.constructCommonModel;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.fajar.entitymanagement.entity.setting.EntityManagementConfig;
import com.fajar.entitymanagement.entity.setting.EntityProperty;
import com.fajar.entitymanagement.repository.EntityRepository;
import com.fajar.entitymanagement.util.EntityUtil;

@Service
public class EntityManagementPageService {
	
	@Autowired
	private EntityRepository entityRepository;
	
	public Model setModel(HttpServletRequest request, Model model, String key) throws Exception {
		
		EntityManagementConfig entityConfig = entityRepository.getConfig(key);
		
		if(null == entityConfig) {
			throw new IllegalArgumentException("Invalid entity key!");
		}
		
		EntityProperty entityProperty = EntityUtil.createEntityProperty(entityConfig.getEntityClass(), null); 
		model = constructCommonModel(request, entityProperty, model, entityConfig.getEntityClass().getSimpleName(), "management");
		
		return model;
	}

}
