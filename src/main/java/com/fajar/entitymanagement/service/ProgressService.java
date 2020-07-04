package com.fajar.entitymanagement.service;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.entitymanagement.util.SessionUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProgressService {

	@Autowired
	private RealtimeService2 realtimeService;

	private double currentProgress = 0.0;

	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	}

	public void init(String requestId) {
		currentProgress = 0.0;
		realtimeService.sendProgress(1, requestId);
	}

	/**
	 * 
	 * @param progress    progressPoportion for current tax
	 * @param maxProgress totalProportion for current tax
	 * @param percent     tax Proportion for whole request
	 * @param newProgress
	 * @param requestId
	 */
	public void sendProgress(double progress, double maxProgress, double percent, boolean newProgress,
			HttpServletRequest httpServletRequest) {
		String requestId = getRequestId(httpServletRequest);

		if (newProgress) {
			currentProgress = 0.0;
		}
		currentProgress += (progress / maxProgress);
		log.info("%%%%%%|PROGRESS|%%%%%%% : " + currentProgress + " adding :" + progress + "/" + maxProgress
				+ ", portion: " + percent + " ==> " + currentProgress * percent);
		realtimeService.sendProgress(currentProgress * percent, requestId);
	}

	public void sendProgress(double progress, double maxProgress, double percent,
			HttpServletRequest httpServletRequest) {
		sendProgress(progress, maxProgress, percent, false, httpServletRequest);
	}

	public void sendProgress(double percent, HttpServletRequest httpServletRequest) {
		sendProgress(1, 1, percent, httpServletRequest);
	}

	public void sendComplete(HttpServletRequest httpServletRequest) {
		String requestId = getRequestId(httpServletRequest);

		log.info("%%%%%%|COMPLETE PROGRESS|%%%%%%% for {}", requestId);
		realtimeService.sendProgress(100, requestId);

	}

	static String getRequestId(HttpServletRequest httpServletRequest) {
		return SessionUtil.getPageRequestId(httpServletRequest);
	}

}
