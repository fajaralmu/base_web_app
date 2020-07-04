package com.fajar.entitymanagement.controller;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fajar.entitymanagement.annotation.Authenticated;
import com.fajar.entitymanagement.dto.WebRequest;
import com.fajar.entitymanagement.dto.WebResponse;
import com.fajar.entitymanagement.service.LogProxyFactory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api/public")
@Authenticated(loginRequired = false)
public class RestPublicController extends BaseController {
 

	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	}

	public RestPublicController() {
		log.info("----------------------Rest Public Controller-------------------");
	}

	@PostMapping(value = "/requestid", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public WebResponse getRequestId(@RequestBody WebRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws IOException {
		log.info("register {}", request);
		WebResponse response = userSessionService.requestId(httpRequest, httpResponse);
		return response;
	}

	@PostMapping(value = "/pagecode")
	public WebResponse getCurrentPageCode(HttpServletRequest request, HttpServletResponse response) {
		validatePageRequest(request);
		return WebResponse.builder().code(super.activePage(request)).build();
	}

	@PostMapping(value = "/menus/{pageCode}")
	public WebResponse getMenusByPage(@PathVariable(value = "pageCode") String pageCode, HttpServletRequest request,
			HttpServletResponse response) {
		validatePageRequest(request);
		return componentService.getMenuByPageCode(pageCode);
	}

	public void validatePageRequest(HttpServletRequest req) {
		boolean validated = userSessionService.validatePageRequest(req);
		if (!validated) {
			throw new RuntimeException("Invalid page request");
		}
	}

}
