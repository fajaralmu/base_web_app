package com.fajar.entitymanagement.controller;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fajar.entitymanagement.annotation.Authenticated;
import com.fajar.entitymanagement.dto.WebRequest;
import com.fajar.entitymanagement.dto.WebResponse;
import com.fajar.entitymanagement.service.LogProxyFactory;

@CrossOrigin
@RestController
@Authenticated
@RequestMapping("/api/admin")
public class RestAdminController extends BaseController {
	Logger log = LoggerFactory.getLogger(RestAdminController.class);
	  
	@Autowired
	private RestPublicController restPublicController;

	public RestAdminController() {
		log.info("------------------RestAdminController-----------------");
	}

	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	} 
	 
	
	@PostMapping(value =  "/savepagesequence", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public WebResponse savePageSequence(@RequestBody WebRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws IOException {
		if (!accountService.validateToken(httpRequest)) {
			return WebResponse.failedResponse();
		}
		WebResponse response = componentService.savePageSequence(request);
		return response;
	}


}
