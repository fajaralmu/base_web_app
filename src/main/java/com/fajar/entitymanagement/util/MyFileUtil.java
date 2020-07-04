package com.fajar.entitymanagement.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyFileUtil {
	/**
	 * get file from XSSFWorkbook
	 * 
	 * @param xssfWorkbook
	 * @param fileName
	 * @return
	 */
	public static File getFile(XSSFWorkbook xssfWorkbook, String fileName) {
		/**
		 * Write file to disk
		 */
		File f = new File(fileName);
		try {
			xssfWorkbook.write(new FileOutputStream(f));
			if (f.canRead()) {
				log.info("DONE Writing Report: " + f.getAbsolutePath());
//				return f.getName();
			}
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			try {
				xssfWorkbook.write(bos);
			} finally {
				bos.close();
			}
//			byte[] bytes = bos.toByteArray();
			return f;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
