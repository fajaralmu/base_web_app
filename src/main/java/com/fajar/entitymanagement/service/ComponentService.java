package com.fajar.entitymanagement.service;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.entitymanagement.dto.WebRequest;
import com.fajar.entitymanagement.dto.WebResponse;
import com.fajar.entitymanagement.entity.Menu;
import com.fajar.entitymanagement.entity.Page;
import com.fajar.entitymanagement.entity.ProductCategory;
import com.fajar.entitymanagement.entity.User;
import com.fajar.entitymanagement.entity.UserRole;
import com.fajar.entitymanagement.repository.EntityRepository;
import com.fajar.entitymanagement.repository.MenuRepository;
import com.fajar.entitymanagement.repository.PageRepository;
import com.fajar.entitymanagement.repository.ProductCategoryRepository;
import com.fajar.entitymanagement.util.CollectionUtil;
import com.fajar.entitymanagement.util.EntityUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ComponentService {

	@Autowired
	private ProductCategoryRepository categoryRepository;
	@Autowired
	private UserSessionService userSessionService;
	@Autowired
	private UserAccountService userAccountService;
	@Autowired
	private EntityRepository entityRepository; 
	@Autowired
	private PageRepository pageRepository; 
	@Autowired
	private MenuRepository menuRepository;
	 
	public List<Page> getPages(HttpServletRequest request) {

		boolean hasSession = userSessionService.hasSession(request);

		if (hasSession)
			return pageRepository.findByOrderBySequenceAsc();
		else
			return pageRepository.findByAuthorized(0);
	}

	/**
	 * get page code
	 * 
	 * @param request
	 * @return
	 */
	public String getPageCode(HttpServletRequest request) {
		String uri = request.getRequestURI();
		String link = uri.replace(request.getContextPath(), "");

		log.info("get page from DB with link: {}", link);
		Page page = pageRepository.findTop1ByLink(link);

		log.info("page from db : {}", page);
		if (null == page) {
			return "";
		}

		log.info("page code found: {}", page.getCode());
		return page.getCode();
	}

	public List<Page> getAllPages() {
		return pageRepository.findAll();
	}
 
	public Page getPage(String code, HttpServletRequest request) {
		Page page = pageRepository.findByCode(code);

		if (page.getAuthorized() == 1 && !userSessionService.hasSession(request)) {

			return null;
		}

		List<Menu> menus = getMenuListByPageCode(code);
		page.setMenus(menus);
		return page;
	}

	public WebResponse getMenuByPageCode(String pageCode) {

		List<Menu> menus = getMenuListByPageCode(pageCode);

		return WebResponse.builder().entities(CollectionUtil.convertList(menus)).build();
	}

	

	public List<Menu> getMenuListByPageCode(String pageCode) {

		List<Menu> menus = menuRepository.findByMenuPage_code(pageCode);

		if (menus == null || menus.size() == 0) {

			 log.debug("NO MENU WITH PAGE CODE: {}", pageCode);
		}

		EntityUtil.validateDefaultValues(menus);
		return menus;
	}

	private boolean hasAccess(User user, String menuAccess) {
		UserRole userRole = userAccountService.getRole(user);
		boolean hasAccess = false;

		for (String userAccess : userRole.getAccess().split(",")) {
			if (userAccess.equals(menuAccess)) {
				hasAccess = true;
				break;
			}
		}

		return hasAccess;
	} 
	
	public List<ProductCategory> getAllCategories() {
		return categoryRepository.findByDeletedFalse();
	}
 

	public WebResponse savePageSequence(WebRequest request) {

		List<Page> pages = request.getPages();

		try {

			for (int i = 0; i < pages.size(); i++) {
				Page page = pages.get(i);
				updateSequence(i, page.getId());
			}

			WebResponse response = WebResponse.success();
			return response;

		} catch (Exception e) {
			log.error("Error saving page sequence");
			e.printStackTrace();
			return WebResponse.failed(e.getMessage());
		}
	}

	private void updateSequence(int sequence, Long id) {

		Optional<Page> pageDB = pageRepository.findById(id);
		if (pageDB.isPresent()) {
			Page page = pageDB.get();
			page.setSequence(sequence);
			entityRepository.save(page);
		}
	}

}
