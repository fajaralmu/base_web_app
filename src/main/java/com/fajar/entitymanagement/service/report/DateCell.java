package com.fajar.entitymanagement.service.report;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;

public class DateCell extends CustomCell{
 
	/**
	 * 
	 */
	private static final long serialVersionUID = 5938145420731956472L;
	private final String pattern;
	private SimpleDateFormat simpleDateFormat;
	
	public DateCell(String dateString, String pattern) {
		 
		this.pattern = pattern;
		simpleDateFormat = new SimpleDateFormat(pattern);
		try {
			this.value = simpleDateFormat.parse(dateString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
	}
	
	public DateCell(Date date, String pattern) {
		 
		this.pattern = pattern;
		simpleDateFormat = new SimpleDateFormat(pattern);
		this.value = simpleDateFormat.format(date);
		 
	}
	@Override
	public void setValue(XSSFCell cell) {
		
		XSSFDataFormat fmt = cell.getRow().getSheet().getWorkbook().createDataFormat();
		cell.setCellValue(value.toString());
		cell.getCellStyle().setDataFormat( fmt.getFormat(pattern) );
		
	}

}
