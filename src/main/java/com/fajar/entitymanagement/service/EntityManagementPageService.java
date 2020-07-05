package com.fajar.entitymanagement.service;

import static com.fajar.entitymanagement.util.MvcUtil.constructCommonModel;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.fajar.entitymanagement.entity.Menu;
import com.fajar.entitymanagement.entity.setting.EntityManagementConfig;
import com.fajar.entitymanagement.entity.setting.EntityProperty;
import com.fajar.entitymanagement.repository.EntityRepository;
import com.fajar.entitymanagement.util.EntityUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EntityManagementPageService {

	@Autowired
	private EntityRepository entityRepository;
	@Autowired
	private MenuInitiationService menuInitiationService;
	@Autowired
	private UserSessionService userSessionService;

	public Model setModel(HttpServletRequest request, Model model, String key) throws Exception {

		EntityManagementConfig entityConfig = entityRepository.getConfig(key);

		if (null == entityConfig) {
			throw new IllegalArgumentException("Invalid entity key!");
		}

		EntityProperty entityProperty = EntityUtil.createEntityProperty(entityConfig.getEntityClass(), null);
		model = constructCommonModel(request, entityProperty, model, entityConfig.getEntityClass().getSimpleName(),
				"management");

		setActivePage(entityConfig.getEntityClass(), request);

		return model;
	}

	private void setActivePage(Class entityClass, HttpServletRequest request) {
		try {
			Menu managementMenu = menuInitiationService.getMenuByCode(entityClass.getSimpleName().toLowerCase());
			userSessionService.setActivePage(request, managementMenu.getMenuPage().getCode());
		} catch (Exception e) {
			log.info("setActivePage error");
			e.printStackTrace();
		}
	}

}
