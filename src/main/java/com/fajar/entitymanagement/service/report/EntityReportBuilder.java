package com.fajar.entitymanagement.service.report;

import java.io.File;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.fajar.entitymanagement.dto.ReportData;
import com.fajar.entitymanagement.entity.BaseEntity;
import com.fajar.entitymanagement.entity.setting.EntityProperty;
import com.fajar.entitymanagement.service.WebConfigService;
import com.fajar.entitymanagement.util.MyFileUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EntityReportBuilder extends ReportBuilder {
	private List<BaseEntity> entities;
	private EntityProperty entityProperty;
	private final String requestId;

	public EntityReportBuilder(WebConfigService webConfigService, ReportData reportData) {
		super(webConfigService, reportData);
		this.requestId = reportData.getRequestId();
	}
 
	
	@Override
	protected void init() {
		entities = reportData.getEntities();
		entityProperty = reportData.getEntityProperty();
		
	}
	
	@Override
	public File buildReport() { 

		log.info("Writing entity report of: {}", entityProperty.getEntityName());

		String time = getDateTime();
		String sheetName = entityProperty.getEntityName();

		String reportName = webConfigService.getReportPath() + "/" + sheetName + "_" + time +"_"+ requestId+".xlsx";
		XSSFWorkbook xwb = new XSSFWorkbook();
		xsheet = xwb.createSheet(sheetName);

		createEntityTable();

		sendProgress(1, 1, 10 );
		
		File file = MyFileUtil.getFile(xwb, reportName);
		sendProgress(1, 1, 10 );
		return file;
	}

	private void createEntityTable() {
		try {
			Object[] entityValues = ExcelReportUtil.getEntitiesTableValues(entities, entityProperty);
			ExcelReportUtil.createTable(xsheet, entityProperty.getElements().size() + 1, 2, 2, entityValues);

		} catch (Exception e) {
			log.error("Error creating entity excel table");
			e.printStackTrace();
		}
	}

 

}
