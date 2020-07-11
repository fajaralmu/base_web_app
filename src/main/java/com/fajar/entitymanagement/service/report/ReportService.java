package com.fajar.entitymanagement.service.report;

import java.io.File;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.entitymanagement.dto.WebRequest;
import com.fajar.entitymanagement.dto.WebResponse;
import com.fajar.entitymanagement.service.EntityService;
import com.fajar.entitymanagement.service.ProgressService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ReportService {
	@Autowired
	private EntityService entityService;
	@Autowired
	private ProgressService progressService;
	@Autowired
	private EntityReportService entityReportService;

	public File generateEntityReport(WebRequest request, HttpServletRequest httpRequest) throws Exception {
		log.info("generateEntityReport");
//		request.getFilter().setLimit(0); 

		WebResponse response = entityService.filter(request);

		progressService.sendProgress(1, 1, 20, true, httpRequest);

		File file = entityReportService.getEntityReport(response.getEntities(), response.getEntityClass(), httpRequest);

		return file;
	}

}
