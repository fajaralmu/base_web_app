package com.fajar.entitymanagement.controller;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fajar.entitymanagement.annotation.Authenticated;
import com.fajar.entitymanagement.annotation.CustomRequestInfo;
import com.fajar.entitymanagement.entity.Page;
import com.fajar.entitymanagement.service.LogProxyFactory;
import com.fajar.entitymanagement.util.EntityUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author fajar
 *
 */
@Slf4j
@Controller
@RequestMapping("admin")
@Authenticated
public class MvcAdminController extends BaseController {

	public MvcAdminController() {
		log.info("-----------------Mvc Admin Controller------------------");
	}

	@PostConstruct
	private void init() {
		this.basePage = webAppConfiguration.getBasePage();
		LogProxyFactory.setLoggers(this);
	}

	@RequestMapping(value = { "/home" })
	@CustomRequestInfo(title = "Dashboard", pageUrl = "webpage/home-page")
	public String menuDashboard(Model model, HttpServletRequest request, HttpServletResponse response)
			throws IOException {  

//		model.addAttribute("menus", componentService.getDashboardMenus(request));
		model.addAttribute("imagePath", webAppConfiguration.getUploadedImagePath());
		model.addAttribute("page", "admin");  
		return basePage;
	}
	
	 
	@RequestMapping(value = { "/sidemenudisplayorder" })
	@CustomRequestInfo(title = "Menu Sequence Order", pageUrl = "webpage/sequenceordering", stylePaths = {
			"sequenceordering" })
	public String sideMenuDisplayOrder(Model model, HttpServletRequest request, HttpServletResponse response) {

//		model.addAttribute("pages", componentService.getAllPages());
		model.addAttribute("idField", EntityUtil.getIdFieldOfAnObject(Page.class).getName());
		model.addAttribute("displayField", "name");
		model.addAttribute("entityName", "page");
		model.addAttribute("withAdditionalSetting", true);
		model.addAttribute("resetSequenceLink", "/account/websetting?action=resetmenu"); 
		return basePage;

	}

	 
}
