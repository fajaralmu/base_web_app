package com.fajar.entitymanagement.service;

import static com.fajar.entitymanagement.util.MvcUtil.constructCommonModel;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.fajar.entitymanagement.entity.BaseEntity;
import com.fajar.entitymanagement.entity.Menu;
import com.fajar.entitymanagement.entity.setting.EntityManagementConfig;
import com.fajar.entitymanagement.entity.setting.EntityProperty;
import com.fajar.entitymanagement.repository.EntityRepository;
import com.fajar.entitymanagement.util.CollectionUtil;
import com.fajar.entitymanagement.util.EntityUtil;
import com.fajar.entitymanagement.util.SessionUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EntityManagementPageService {

	@Autowired
	private EntityRepository entityRepository;
	@Autowired
	private MenuInitiationService menuInitiationService;

	public Model setModel(HttpServletRequest request, Model model, String key) throws Exception {

		EntityManagementConfig entityConfig = entityRepository.getConfig(key);

		if (null == entityConfig) {
			throw new IllegalArgumentException("Invalid entity key!");
		}

		HashMap<String, List<?>> additionalListObject = getFixedListObjects(entityConfig.getEntityClass());
		EntityProperty entityProperty = EntityUtil.createEntityProperty(entityConfig.getEntityClass(), additionalListObject );
		model = constructCommonModel(request, entityProperty, model, entityConfig.getEntityClass().getSimpleName(),
				"management");

		String pageCode = getPageCode(entityConfig.getEntityClass(), request);
		model.addAttribute(SessionUtil.PAGE_CODE, pageCode);
		return model;
	}

	private HashMap<String, List<?>> getFixedListObjects(Class<? extends BaseEntity> entityClass) {
		HashMap<String, List<?>> listObject = new HashMap<>();
		try {
			List<Field> fixedListFields = EntityUtil.getFixedListFields(entityClass);

			for (int i = 0; i < fixedListFields.size(); i++) {
				Field field = fixedListFields.get(i);
				Class<? extends BaseEntity> type = (Class<? extends BaseEntity>) field.getType();
				List<? extends BaseEntity> list = entityRepository.findAll(type);
				listObject.put(field.getName(), CollectionUtil.convertList(list));
			}
//		listObject.put("menuPage", CollectionUtil.convertList(componentService.getAllPages()));
		} catch (Exception e) {
			// TODO: handle exception
		}
		return listObject;
	}

	private String getPageCode(Class<? extends BaseEntity> entityClass, HttpServletRequest request) {
		try {
			Menu managementMenu = menuInitiationService.getMenuByCode(entityClass.getSimpleName().toLowerCase());
			return managementMenu.getMenuPage().getCode();
		} catch (Exception e) {
			log.info("getPageCode error");
			e.printStackTrace();
			return null;
		}
	}

}
