package com.fajar.entitymanagement.controller;

import static com.fajar.entitymanagement.util.MvcUtil.constructCommonModel;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.support.BindingAwareModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fajar.entitymanagement.annotation.Authenticated;
import com.fajar.entitymanagement.annotation.CustomRequestInfo;
import com.fajar.entitymanagement.entity.Profile;
import com.fajar.entitymanagement.entity.User;
import com.fajar.entitymanagement.entity.setting.EntityProperty;
import com.fajar.entitymanagement.service.EntityManagementPageService;
import com.fajar.entitymanagement.service.EntityService;
import com.fajar.entitymanagement.service.LogProxyFactory;
import com.fajar.entitymanagement.service.WebConfigService;
import com.fajar.entitymanagement.util.CollectionUtil;
import com.fajar.entitymanagement.util.EntityUtil;
import com.fajar.entitymanagement.util.SessionUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author fajar
 *
 */
@Slf4j
@Controller
@RequestMapping("management")
@Authenticated
@CustomRequestInfo(stylePaths = "entitymanagement", pageUrl = "webpage/entity-management-page")
public class MvcManagementController extends BaseController {

	@Autowired
	private EntityService entityService;
	@Autowired
	private EntityManagementPageService entityManagementPageService;

	private static final String ERROR_404_PAGE = "error/notfound";

	public MvcManagementController() {
		log.info("-----------------Mvc Management Controller------------------");
	}

	@PostConstruct
	private void init() {
		basePage = webAppConfiguration.getBasePage();
		LogProxyFactory.setLoggers(this);
	}

	@RequestMapping(value = { "/common/{name}" })
	public String unit(@PathVariable("name") String name, Model model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		model = entityManagementPageService.setModel(request, model, name);
		try {
			BindingAwareModelMap modelImpl = (BindingAwareModelMap) model;
			String pageCode = modelImpl.get(SessionUtil.PAGE_CODE).toString();
			setActivePage(request, pageCode); 
			log.info("Management Page Code: {}", request.getSession().getAttribute(SessionUtil.PAGE_CODE));
		} catch (Exception e) {
			// TODO: handle exception
		}
		return basePage;
	}

	@RequestMapping(value = { "/profile" })
	public String profile(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {

		EntityProperty entityProperty = EntityUtil.createEntityProperty(Profile.class, null);

		model = constructCommonModel(request, entityProperty, model, "Profile", "management");
		// override singleObject
		model.addAttribute("entityId", webAppConfiguration.getProfile().getId());
		model.addAttribute("singleRecord", true);
		
		setActivePage(request, WebConfigService.SETTING);  
		return basePage;
	} 

	/**
	 * RESTRICTED ACCESS
	 * 
	 **/

	@RequestMapping(value = { "/user" })
	public String user(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {

		try {
			checkUserAccess(userService.getUserFromSession(request), "/management/user");
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR_404_PAGE;
		}
		HashMap<String, List<?>> listObject = new HashMap<>();
		listObject.put("userRole", CollectionUtil.convertList(entityService.getAllUserRole()));
		EntityProperty entityProperty = EntityUtil.createEntityProperty(User.class, listObject);
		model = constructCommonModel(request, entityProperty, model, "User", "management");
		
		return basePage;
	}

	/**
	 * 
	 * NON ENTITY
	 * 
	 */

	@RequestMapping(value = { "/appsession" })
	@CustomRequestInfo(title = "Apps Sessions", pageUrl = "shop/app-session")
	public String appsession(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

		try {
			checkUserAccess(userService.getUserFromSession(request), "/management/menu");
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR_404_PAGE;
		}

		model.addAttribute("page", "management");
		return basePage;
	}

	private void checkUserAccess(User user, String url) throws Exception {
//		componentService.checkAccess(user, url);
	}

}
