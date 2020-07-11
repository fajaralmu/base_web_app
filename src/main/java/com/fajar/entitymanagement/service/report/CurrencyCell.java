package com.fajar.entitymanagement.service.report;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;

public class CurrencyCell extends CustomCell { 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1287888395717453220L;

	public CurrencyCell(long value) {
		super();
		this.value = value;
	}

	@Override
	public void setValue(XSSFCell cell) {
		try {
			XSSFDataFormat fmt = cell.getRow().getSheet().getWorkbook().createDataFormat();
			cell.setCellValue(Double.parseDouble(value.toString())); 
			cell.getCellStyle().setDataFormat( fmt.getFormat("#,##0") );
		
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
