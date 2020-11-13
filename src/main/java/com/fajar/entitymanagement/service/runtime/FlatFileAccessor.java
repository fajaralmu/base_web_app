package com.fajar.entitymanagement.service.runtime;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
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
public class FlatFileAccessor {

	@Value("temp/session/SESSIONS.txt")
	private Resource sessionResourceFile;
	private File sessionFile; 

	@PostConstruct
	public void init() throws Exception {
		
		log.info("FlatFileAccessor inits.. ");
		sessionFile = sessionResourceFile.getFile();

		log.info("sessionFile path: {}", sessionFile.getCanonicalPath());
		addLineToSession("APP_STARTED "+(new Date()));
		printSessions();
	}

	public synchronized void addLineToSession(Object line) throws Exception {
		try {
			if (null == line) {
				log.info("Add NULL line to session file");
				return;
			}
			List<String> lines = getSessionLines();
			if (lines.size() == 1) {
				lines.add("");
			}
			lines.set(1, "UPDATED=" + new Date()); // 2nd index

			lines.add(String.valueOf(line));

			FileUtils.writeLines(sessionFile, lines);
		} catch (Exception e) {

			e.printStackTrace();
			throw e;
		}
	}

	public synchronized void updateLineAtIndex(int index, Object line) throws Exception {
		try {
			if (null == line) {
				log.info("Add NULL line to session file");
				return;
			}
			List<String> lines = getSessionLines();
			if (lines.size() == 1) {
				lines.add("");
			}
			lines.set(1, "UPDATED=" + new Date());
			lines.set(index, String.valueOf(line));

			FileUtils.writeLines(sessionFile, lines);
		} catch (Exception e) {

			e.printStackTrace();
			throw e;
		}
	}

	private List<String> getSessionLines() {
		try {
			return FileUtils.readLines(sessionFile);
		} catch (IOException e) {
			log.error("ERROR getting file lines");
			e.printStackTrace();
			return CollectionUtil.emptyList();
		}
	}

	public synchronized String printSessions() {
		BufferedReader br = null;
		String result = "";
		try {
			br = new BufferedReader(new FileReader(sessionFile));
			String line;
			while ((line = br.readLine()) != null) {
				result += line + "\n";
				System.out.println(line);
			}
		} catch (Exception e) {
			log.error("Error printing session file");
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException e) {
				//
			}

		}
		return result;
	}

	public String getLineContent(String key) {
		List<String> lines = getSessionLines();
		for (int i = 0; i < lines.size(); i++) {
			final String line = lines.get(i);
			if (line.startsWith(key + "=")) {
				return line.replace(key + "=", "");
			}
		}
		return null;
	}

	public void putKeyValue(String key, String json) throws Exception {
		try {

			String keyValue = key + "=" + json;
			int existingIndex = getLineIndexByKey(key);

			if (existingIndex < 1) {
				addLineToSession(keyValue);
				return;
			}

			updateLineAtIndex(existingIndex, keyValue);
		} catch (Exception e) {

			e.printStackTrace();
			throw e;
		}

	}

	public int getLineIndexByKey(String key) {

		List<String> lines = getSessionLines();

		for (int i = 0; i < lines.size(); i++) {
			if (lines.get(i).startsWith(key + "=")) {
				return i;
			}
		}
		return -1;

	}

	public synchronized void removeLineWithKey(String key) throws Exception {
		try {
			List<String> lines = getSessionLines();
			lines.set(1, "UPDATED=" + new Date());

			for (int i = 0; i < lines.size(); i++) {
				if (lines.get(i).startsWith(key + "=")) {
					lines.remove(i);
					break;
				}
			}

			FileUtils.writeLines(sessionFile, lines);
		} catch (Exception e) {

			e.printStackTrace();
			throw e;
		}
	}
}
