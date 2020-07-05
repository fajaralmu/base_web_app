package com.fajar.entitymanagement.service;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.entitymanagement.annotation.Dto;
import com.fajar.entitymanagement.entity.Menu;
import com.fajar.entitymanagement.entity.Page;
import com.fajar.entitymanagement.repository.MenuRepository;
import com.fajar.entitymanagement.repository.PageRepository;
import com.fajar.entitymanagement.util.EntityUtil;
import com.fajar.entitymanagement.util.StringUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MenuInitiationService {

	private static final String MANAGEMENT = "management";

	@Autowired
	private WebConfigService webConfigService;

	private List<Type> persistenceEntities;

	@Autowired
	private MenuRepository MenuRepository;

	@Autowired
	private PageRepository PageRepository;

	@PostConstruct
	public void init() {

		try {
			checkDefaultMenu();
			checkManagementPage();
			getPersistenctEntities();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void checkManagementPage() {

		Page managementPage = getPageByCode(MANAGEMENT);
		if (null != managementPage) {
			log.info("managementPage FOUND");
			return;
		}

		log.info("managementPage NOT FOUND. WILL ADD SETTING");
		PageRepository.save(defaultManagementPage());
	}

	private Page defaultManagementPage() {

		Page menuPage = new Page();
		menuPage.setCode(MANAGEMENT);
		menuPage.setCreatedDate(new Date());
		menuPage.setDescription("Default Management Page");
		menuPage.setLink("/webpage/page/" + MANAGEMENT);
		menuPage.setName(MANAGEMENT + "(generated)");
		menuPage.setNonMenuPage(0);
		menuPage.setAuthorized(1);
		menuPage.setSequence(0);
		return menuPage;
	}

	private void getPersistenctEntities() {

		this.persistenceEntities = webConfigService.getEntityClassess();
		for (Type type : persistenceEntities) {
			validateManagementPage((Class) type);

		}
	}

	private void validateManagementPage(Class entityClass) {
		Dto dto = EntityUtil.getClassAnnotation(entityClass, Dto.class);
		if (null == dto)
			return;

		Menu managementMenu = getMenuByCode(entityClass.getSimpleName().toLowerCase());
		if (null == managementMenu) {
			addNewMenuPage(entityClass);
		}

	}

	private void addNewMenuPage(Class entityClass) {
		Dto dto = EntityUtil.getClassAnnotation(entityClass, Dto.class);
		if (null == dto) {
			return;
		}

		boolean commonPage = dto.commonManagementPage();
		String menuCode = entityClass.getSimpleName().toLowerCase();

		Menu menu = new Menu();
		menu.setCode(menuCode);
		menu.setName(StringUtil.extractCamelCase(entityClass.getSimpleName()) + " Management");
		if (commonPage) {
			menu.setUrl("/management/common/" + menuCode);
		} else {
			menu.setUrl("/management/" + menuCode);
		}
		Page menuPage = getPageByCode(MANAGEMENT);
		menu.setMenuPage(menuPage);
		menu.setColor("#ffffff");
		menu.setFontColor("#000000");
		menu.setDescription("Generated Management Page For: "+entityClass.getSimpleName());
		
		
		MenuRepository.save(menu);
	}

	private void checkDefaultMenu() { 
		webConfigService.defaultMenu();
	}

	private Menu getMenuByCode(String code) {
		return MenuRepository.findByCode(code);
	}

	private Page getPageByCode(String code) {
		return PageRepository.findByCode(code);
	}

}
