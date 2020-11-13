package com.fajar.entitymanagement.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.ModelAndView;

import com.fajar.entitymanagement.dto.KeyValue;
import com.fajar.entitymanagement.entity.Page;
import com.fajar.entitymanagement.entity.Profile;
import com.fajar.entitymanagement.entity.User;
import com.fajar.entitymanagement.service.ComponentService;
import com.fajar.entitymanagement.service.UserAccountService;
import com.fajar.entitymanagement.service.WebConfigService;
import com.fajar.entitymanagement.service.runtime.RuntimeService;
import com.fajar.entitymanagement.service.sessions.RegisteredRequestService;
import com.fajar.entitymanagement.service.sessions.SessionValidationService;
import com.fajar.entitymanagement.service.sessions.UserSessionService;
import com.fajar.entitymanagement.util.ApplicationUtil;
import com.fajar.entitymanagement.util.DateUtil;
import com.fajar.entitymanagement.util.MvcUtil;
import com.fajar.entitymanagement.util.SessionUtil; 

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class BaseController {

	protected String basePage;

	@Autowired
	protected WebConfigService webAppConfiguration;
	@Autowired
	protected UserSessionService userSessionService;
	@Autowired
	protected SessionValidationService sessionValidationService;
	@Autowired
	protected UserAccountService accountService;
	@Autowired
	protected RuntimeService registryService;  
	@Autowired
	protected ComponentService componentService;
	@Autowired
	protected RegisteredRequestService registeredRequestService;

	@ModelAttribute("shopProfile")
	@Deprecated // new @ModelAttribute = 'profile'
	public Profile getShopProfile(HttpServletRequest request) {
//		System.out.println("Has Session: "+userSessionService.hasSession(request, false));
		return webAppConfiguration.getProfile();
	}

	@ModelAttribute("profile")
	public Profile getProfile(HttpServletRequest request) {
		return getShopProfile(request);
	}

	@ModelAttribute("timeGreeting")
	public String timeGreeting(HttpServletRequest request) {
		return DateUtil.getTimeGreeting();
	}

	@ModelAttribute("loggedUser")
	public User getLoggedUser(HttpServletRequest request) {
		if (sessionValidationService.hasSession(request, false)) {
			return userSessionService.getUserFromSession(request);
		} else
			return null;
	}

	@ModelAttribute("ipv4Address")
	public String getIpv4Address(HttpServletRequest request) {
		return ApplicationUtil.getIpv4Address();
	}

	@ModelAttribute("pageIconUrl")
	public String iconUrl(HttpServletRequest request) {
		Profile Profile = webAppConfiguration.getProfile();

		String icon;
		if (null != Profile.getPageIcon()) {
			icon = "/WebAsset/Shop1/Images/ICON/" + Profile.getPageIcon();
		} else {
			icon = "/res/img/javaEE.ico";
		}

		return icon;
	}

	@ModelAttribute("host")
	public String getHost(HttpServletRequest request) {
		return MvcUtil.getHost(request);
	}

	@ModelAttribute("contextPath")
	public String getContextPath(HttpServletRequest request) {
		return request.getContextPath();
	}

	@ModelAttribute("fullImagePath")
	public String getFullImagePath(HttpServletRequest request) {
		return getHost(request) + getContextPath(request) + "/" + getUploadedImagePath(request) + "/";
	}

	@ModelAttribute("imagePath")
	public String getUploadedImagePath(HttpServletRequest request) {
		return webAppConfiguration.getUploadedImagePath();
	}

	@ModelAttribute("pageToken")
	public String pageToken(HttpServletRequest request) {
		try {
			return sessionValidationService.getTokenByServletRequest(request);
		} catch (Exception e) {
			return "";
		}
	}

	@ModelAttribute("requestId")
	public String getPublicRequestId(HttpServletRequest request) {
		try {
			Cookie cookie = getCookie(SessionUtil.JSESSSIONID, request.getCookies());
			String cookieValue = cookie == null ? UUID.randomUUID().toString() : cookie.getValue();
			return registryService.addPageRequest(cookieValue);
		} catch (Exception e) {

			return "";
		}

	}

	@ModelAttribute("registeredRequestId")
	public String getRegisteredRequestId(HttpServletRequest request) {

		return SessionUtil.getSessionRegisteredRequest(request);
	}

	@ModelAttribute("pages")
	public List<Page> pages(HttpServletRequest request) {

		return componentService.getPages(request);
	}

	@ModelAttribute("year")
	public int getCurrentYear(HttpServletRequest request) {
		return DateUtil.getCalendarItem(new Date(), Calendar.YEAR);
	}

	@ModelAttribute("authenticated")
	public boolean authenticated(HttpServletRequest request) {
		return sessionValidationService.hasSession(request);
	}

//	public String activePage(HttpServletRequest request) {
//		return userSessionService.getPageCode(request);
//	}

	/**
	 * ====================================================== Statics
	 * ======================================================
	 * 
	 */

	public static Cookie getCookie(String name, Cookie[] cookies) {
		try {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(name)) {
					return cookie;
				}
			}
		} catch (Exception ex) {
			log.error("ERROR GET COOKIE NAME: {}", name);
		}
		return null;
	}

	public static Cookie getJSessionIDCookie(HttpServletRequest request) {

		return getCookie(SessionUtil.JSESSSIONID, request.getCookies());
	}

	/**
	 * send to login page URL
	 * 
	 * @param request
	 * @param response
	 */
	public static void sendRedirectLogin(HttpServletRequest request, HttpServletResponse response) {
		sendRedirect(response, request.getContextPath() + "/account/login");
	}

	/**
	 * send to specified URL
	 * 
	 * @param response
	 * @param url
	 */
	public static void sendRedirect(HttpServletResponse response, String url) {
		try {
			response.sendRedirect(url);
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	private static void addResourcePaths(ModelAndView modelAndView, String resourceName, String... paths) {
		List<KeyValue<String, String>> resoucePaths = new ArrayList<>();
		for (int i = 0; i < paths.length; i++) {
			KeyValue<String, String> keyValue = new KeyValue<String, String>();
			keyValue.setValue(paths[i]);

			resoucePaths.add(keyValue);
			log.info("{}. Add {} to {} , value: {}", i, resourceName, modelAndView.getViewName(), paths[i]);
		}
		setModelAttribute(modelAndView, resourceName, resoucePaths);
	}

	private static void setModelAttribute(ModelAndView modelAndView, String attrName, Object attrValue) {
		if (null == attrValue) {
			return;
		}
		modelAndView.getModel().put(attrName, attrValue);
	}

	public static void addStylePaths(ModelAndView modelAndView, String... paths) {
		if (null == paths) {
			return;
		}
		addResourcePaths(modelAndView, "additionalStylePaths", paths);
	}

	public static void addJavaScriptResourcePaths(ModelAndView modelAndView, String... paths) {
		if (null == paths) {
			return;
		}
		addResourcePaths(modelAndView, "additionalScriptPaths", paths);
	}

	public static void addTitle(ModelAndView modelAndView, String title) {
		if (null == title || title.isEmpty()) {
			return;
		}
		setModelAttribute(modelAndView, "title", title);
	}

	public static void addPageUrl(ModelAndView modelAndView, String pageUrl) {
		if (null == pageUrl || pageUrl.isEmpty()) {
			return;
		}
		setModelAttribute(modelAndView, "pageUrl", pageUrl);

	}
}
