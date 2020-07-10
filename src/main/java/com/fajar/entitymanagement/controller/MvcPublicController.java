package com.fajar.entitymanagement.controller;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fajar.entitymanagement.annotation.CustomRequestInfo;
import com.fajar.entitymanagement.service.LogProxyFactory;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class MvcPublicController extends BaseController {

	public MvcPublicController() {
		log.info("---------------------------Mvc Public Controller------------------------------");
	}

	@PostConstruct
	public void init() {
		basePage = webAppConfiguration.getBasePage();
		LogProxyFactory.setLoggers(this);
	}

	@RequestMapping(value = { "/", "index" })
	@CustomRequestInfo(title = "Base Web Application", pageUrl = "index")
	public String index(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

		model.addAttribute("page", "main");

		return basePage;

	} 

	@RequestMapping(value = { "/public/about" })
	@CustomRequestInfo(title = "About Us", pageUrl = "webpage/about-page")
	public String about(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

		model.addAttribute("page", "about");
		return basePage;

	}

}
