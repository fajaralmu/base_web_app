package com.fajar.entitymanagement.controller;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fajar.entitymanagement.annotation.Authenticated;
import com.fajar.entitymanagement.annotation.CustomRequestInfo;
import com.fajar.entitymanagement.dto.WebRequest;
import com.fajar.entitymanagement.dto.WebResponse;
import com.fajar.entitymanagement.service.LogProxyFactory;

import lombok.extern.slf4j.Slf4j;

@CrossOrigin
@RestController
@RequestMapping("/api/account")
@Slf4j
public class RestAccountController extends BaseController {

	public RestAccountController() {
		log.info("------------------RestAccountController-----------------");
	}

	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	}

	@PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@CustomRequestInfo(withRealtimeProgress = true)
	public WebResponse register(@RequestBody WebRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws Exception {
		log.info("register {}", request);
		WebResponse response = accountService.registerUser(request, httpRequest);
		return response;
	}

	@PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public WebResponse login(@RequestBody WebRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws Exception {
		log.info("login {}", request);
		WebResponse response = accountService.login(request, httpRequest, httpResponse);
		return response;
	}

	@PostMapping(value = "/logout", produces = MediaType.APPLICATION_JSON_VALUE)
	public WebResponse logout(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException {

		boolean success = false;
		if (userSessionService.hasSession(httpRequest, false)) {
			success = accountService.logout(httpRequest);
		}

		return WebResponse.builder().code(success ? "00" : "01").message("SUCCESS LOGOUT: " + success).build();
	}

	@PostMapping(value = "/getprofile", produces = MediaType.APPLICATION_JSON_VALUE)
	@Authenticated
	public WebResponse getprpfile(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException {

		return userSessionService.getProfile(httpRequest);
	}

}
