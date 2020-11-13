package com.fajar.entitymanagement.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.connector.RequestFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fajar.entitymanagement.annotation.Authenticated;
import com.fajar.entitymanagement.annotation.CustomRequestInfo;
import com.fajar.entitymanagement.dto.WebRequest;
import com.fajar.entitymanagement.service.LogProxyFactory;
import com.fajar.entitymanagement.service.report.ReportService;

import lombok.extern.slf4j.Slf4j;

@CrossOrigin
@Controller
@Authenticated
@RequestMapping("/api/report")
@Slf4j
public class RestReportController {
	
	@Autowired
	private ReportService excelReportService;
	
	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	}
	
	
	@PostMapping(value = "/entity", consumes = MediaType.APPLICATION_JSON_VALUE )
	@CustomRequestInfo(withRealtimeProgress = true)
	public void entityreport(@RequestBody WebRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws Exception {
		log.info("entityreport {}", request); 
		
		File result = excelReportService.generateEntityReport(request, httpRequest);
		
		writeFileReponse(httpResponse, result);
	 
	}
	
	@GetMapping(value = "/hello.js") 
	public void resources( HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws Exception {
		//Authentication goes here or using handler interceptor
		httpResponse.setContentType("text/javascript");
		httpResponse.setHeader("MIME-type", "text/javascript");
		httpResponse.getWriter().write(" function alertMan(msg) { \n alert(\"message:\"+msg); \n } ");
	}
	
	public static void writeFileReponse(HttpServletResponse httpResponse, File file) throws  Exception {
		httpResponse.setHeader("Content-disposition","attachment; filename="+file.getName());
		FileInputStream in = new FileInputStream(file);
		OutputStream out = httpResponse.getOutputStream();

		byte[] buffer= new byte[8192]; // use bigger if you want
		int length = 0;

		while ((length = in.read(buffer)) > 0){
		     out.write(buffer, 0, length);
		}
		in.close();
		out.close();
	}
	
	 
	 

}
