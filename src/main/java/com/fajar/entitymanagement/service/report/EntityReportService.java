package com.fajar.entitymanagement.service.report;

import java.io.File;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.entitymanagement.dto.ReportData;
import com.fajar.entitymanagement.entity.BaseEntity;
import com.fajar.entitymanagement.entity.User;
import com.fajar.entitymanagement.entity.setting.EntityProperty;
import com.fajar.entitymanagement.service.ProgressService;
import com.fajar.entitymanagement.service.WebConfigService;
import com.fajar.entitymanagement.util.EntityUtil;
import com.fajar.entitymanagement.util.SessionUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EntityReportService {

	@Autowired
	private WebConfigService webConfigService;
	@Autowired
	private ProgressService progressService;

	public File getEntityReport(List<BaseEntity> entities, Class<? extends BaseEntity> entityClass,
			HttpServletRequest httpRequest) throws Exception {
		log.info("Generate entity report: {}", entityClass); 
		User currentUser = SessionUtil.getUserFromRequest(httpRequest); 
		String requestId = currentUser.getRequestId();
		
		EntityProperty entityProperty = EntityUtil.createEntityProperty(entityClass, null);
		ReportData reportData = ReportData.builder().entities(entities).entityProperty(entityProperty).requestId(requestId).build(); 
	
		EntityReportBuilder reportBuilder = new EntityReportBuilder(webConfigService, reportData);
		reportBuilder.setProgressService(progressService);
		
		progressService.sendProgress(1, 1, 10, false, httpRequest);

		File file = reportBuilder.buildReport();
		
		
		
		log.info("Entity Report generated");

		return file;
	}

}
