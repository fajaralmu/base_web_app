package com.fajar.entitymanagement.controller;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fajar.entitymanagement.annotation.Authenticated;
import com.fajar.entitymanagement.annotation.CustomRequestInfo;
import com.fajar.entitymanagement.service.LogProxyFactory;
import com.fajar.entitymanagement.service.MenuInitiationService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("account")
public class MvcAccountController extends BaseController { 
	 
	@Autowired
	private MenuInitiationService menuInitiationService;

	@Autowired
	public MvcAccountController() { 
		log.info("----------------Mvc Account Controller---------------");
	}

	@PostConstruct
	private void init() {
		this.basePage = webAppConfiguration.getBasePage();
		LogProxyFactory.setLoggers(this);
	}

	@RequestMapping(value = { "/login" })
	@CustomRequestInfo(title="Login", pageUrl = "webpage/login-page", stylePaths = "loginpage")
	public String login(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		if (sessionValidationService.hasSession(request, false)) {
			response.sendRedirect(request.getContextPath() + "/admin/home");
		} 

		model.addAttribute("page", "login");
		return basePage;
	}

	@RequestMapping(value = { "/logout" })
	@Authenticated
	public String logout(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		 try {
			userSessionService.removeUserSession(request);
		 }catch (Exception e) {
			// TODO: handle exception
		}

		sendRedirectLogin(request, response);
		return null;
	}

	@RequestMapping(value = { "/register" })
	public String register(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		if (sessionValidationService.hasSession(request)) {
			response.sendRedirect(request.getContextPath() + "/admin/home");
		}
		return "webpage/register-page";
	}
	
	@RequestMapping(value = { "/websetting" })
	@Authenticated
	public void webSetting(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

		String parameter = request.getParameter("action");
		log.info("parameter: {}", parameter);
		if (null != parameter && parameter.equals("resetmenu")) {
			menuInitiationService.resetMenus();
		}
		response.setStatus(302);
		response.setHeader("location", request.getContextPath() + "/admin/home");

	}

}
