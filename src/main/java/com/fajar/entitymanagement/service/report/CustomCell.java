package com.fajar.entitymanagement.service.report;

import java.io.Serializable;

import org.apache.poi.xssf.usermodel.XSSFCell;

import lombok.Data;

@Data
public abstract class CustomCell<ValueType> implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1936743863408885308L;
	protected ValueType value;
	
	@Override
	public String toString() {
		if(null == value) {
			return "";
		}
		
		return value.toString();
	}
	
	public abstract void setValue(XSSFCell cell);

}
