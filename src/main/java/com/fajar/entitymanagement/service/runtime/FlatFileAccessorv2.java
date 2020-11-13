package com.fajar.entitymanagement.service.runtime;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.fajar.entitymanagement.util.CollectionUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FlatFileAccessorv2 {

	@Value("temp/sessionv2/SESSIONS.txt")
	private Resource sessionResourceFile;
	private File sessionFile;

	static final String EXTENSION = ".txt";

	@PostConstruct
	public void init() throws Exception {

		log.info("FlatFileAccessor inits.. ");
		sessionFile = sessionResourceFile.getFile();

		log.info("sessionFile path: {}", sessionFile.getCanonicalPath());
	}

	public void putKeyValue(String key, String json) throws Exception {
		try {

			if (!fileExist(key)) {
				makeFile(key);
			}

			updateFile(key, json);
		} catch (Exception e) {

			e.printStackTrace();
			throw e;
		}

	}

	public synchronized void removeLineWithKey(String key) throws Exception {
		try {

			deleteFile(key);
		} catch (Exception e) {

			e.printStackTrace();
			throw e;
		}
	}

	private void makeFile(String key) {
		String parentDir = sessionFile.getParent();
		try {
			File myObj = new File(parentDir.concat("/").concat(key).concat(EXTENSION));
			if (myObj.createNewFile()) {
				System.out.println("File created: " + myObj.getName());
			} else {
				System.out.println("File already exists.");
			}
		} catch (IOException e) {
			System.out.println("makeFile error occurred.");
			e.printStackTrace();
		}

	}

	private boolean fileExist(String key) {
		String parentDir = sessionFile.getParent();
		try {
			File myObj = new File(parentDir.concat("/").concat(key).concat(EXTENSION));
			return myObj.exists();
		} catch (Exception e) {
			System.out.println("fileExist error occurred.");
			e.printStackTrace();
			return false;
		}

	}

	public String getFileContent(String key) {
		String parentDir = sessionFile.getParent();
		try {
			File myObj = new File(parentDir.concat("/").concat(key).concat(EXTENSION));
			log.debug("get file content v2, exist: {} | {}", myObj.exists(), myObj.getCanonicalPath());
			if (myObj.exists() == false) {
				return null;
			}
			List contents = FileUtils.readLines(myObj);
			String result = contents.size() > 0 ? contents.get(0).toString() : "";
			return result.isEmpty() ? null : result;
		} catch (Exception e) {
			System.out.println("getFileContent error occurred.");
			e.printStackTrace();
			return null;
		}
	}

	private void updateFile(String key, String value) {
		String parentDir = sessionFile.getParent();
		try {
			File myObj = new File(parentDir.concat("/").concat(key).concat(EXTENSION));
			Collection lines = CollectionUtil.listOf(value);
			FileUtils.writeLines(myObj, lines);
		} catch (IOException e) {
			System.out.println("updateFile error occurred.");
			e.printStackTrace();
		}

	}

	private void deleteFile(String key) {
		String parentDir = sessionFile.getParent();
		try {
			File myObj = new File(parentDir.concat("/").concat(key).concat(EXTENSION));
			myObj.delete();

		} catch (Exception e) {
			System.out.println("updateFile error occurred.");
			e.printStackTrace();
		}
	}
}
