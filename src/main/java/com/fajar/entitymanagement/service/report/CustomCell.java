package com.fajar.entitymanagement.service.report;

import java.io.Serializable;

import org.apache.poi.xssf.usermodel.XSSFCell;

import lombok.Data;

@Data
public abstract class CustomCell implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1936743863408885308L;
	protected Object value; 
	protected XSSFCell cell;
	
	@Override
	public String toString() {
		if(null == value) {
			return "";
		} 
		
		return value.toString();
	}
	
	public void avoidNull() {
		if(null == value) {
			value = "";
		}
	}
	
	public abstract void setValue(XSSFCell cell);

}
