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
	@CustomRequestInfo(title = "Shopping Mart Application", pageUrl = "index")
	public String index(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

		model.addAttribute("page", "main");

		return basePage;

	}

	@RequestMapping(value = { "/public/catalog", "/public/catalog/", "/public/catalog/{option}" })
	@CustomRequestInfo(title = "Product Catalog", pageUrl = "webpage/catalog-page")
	public String catalog(@PathVariable(required = false) String option, Model model, HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		model.addAttribute("page", "main");
		model.addAttribute("categories", componentService.getAllCategories());
		model.addAttribute("defaultOption", option == null || option.equals("") ? null : option);
		return basePage;

	}

	@RequestMapping(value = { "/public/about" })
	@CustomRequestInfo(title = "About Us", pageUrl = "webpage/about-page")
	public String about(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

		setActivePage(request);

		model.addAttribute("page", "about");
		return basePage;

	}

}
