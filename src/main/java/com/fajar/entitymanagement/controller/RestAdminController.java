package com.fajar.entitymanagement.controller;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@CrossOrigin
@RestController
@Authenticated
@RequestMapping("/api/admin")
public class RestAdminController extends BaseController {
	Logger log = LoggerFactory.getLogger(RestAdminController.class); 

	public RestAdminController() {
		log.info("------------------RestAdminController-----------------");
	}

	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	} 
	 
	
	@Authenticated
	@PostMapping(value = "/saveentityorder/{entityName}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public WebResponse savePageSequence(@PathVariable("entityName") String entityName, @RequestBody WebRequest request) {

		WebResponse response = componentService.saveEntitySequence(request, entityName);
		return response;
	}

}
