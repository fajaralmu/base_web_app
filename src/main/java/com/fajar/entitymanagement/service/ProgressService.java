package com.fajar.entitymanagement.service;

import java.util.HashMap;
import java.util.Map;

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

	private final Map<String, Double> progressData = new HashMap<>();

	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	}

	public void init(String requestId) {
		progressData.put(requestId, 0d);
		sendProgress(0, requestId);
	}

	/**
	 * 
	 * @param taskProgress             progressPoportion for current task
	 * @param maxProgressOfCurrentTask totalProportion for current task
	 * @param overallProcessProportion task Proportion for whole request
	 * @param newRequest
	 * @param requestId
	 */
	public void sendProgress(double taskProgress, double maxProgressOfCurrentTask, double overallProcessProportion,
			boolean newRequest, String requestId) {
		if (newRequest) {
			updateProgress(requestId, 0, newRequest);
		}

		double taskProportion = taskProgress / maxProgressOfCurrentTask;
		double overallProportion = taskProportion * overallProcessProportion;
		updateProgress(requestId, overallProportion, newRequest);

	}
	
	/**
	 * 
	 * @param taskProgress             progressPoportion for current task
	 * @param maxProgressOfCurrentTask totalProportion for current task
	 * @param overallProcessProportion task Proportion for whole request 
	 * @param requestId
	 */
	public void sendProgress(double taskProgress, double maxProgressOfCurrentTask, double overallProcessProportion,
			 String requestId) {
		sendProgress(taskProgress, maxProgressOfCurrentTask, overallProcessProportion, false, requestId);
	}

	private void updateProgress(String requestId, double newProgress, boolean newRequest) {
		
		checkProgressData(requestId);
		final double currentProgress = newRequest? 0: progressData.get(requestId);
		final double overallProgress = currentProgress + newProgress;

		log.info("adding progress: {} for: {}, currentProgress: {} overall: {}", newProgress, requestId, currentProgress, overallProgress);
		
		if (overallProgress >= 100) {
			sendComplete(requestId);
		} else {
			progressData.put(requestId, overallProgress);
			sendProgress(overallProgress, requestId);

		}
	}

	private void checkProgressData(String requestId) {
		if (progressData.get(requestId) == null) {
			progressData.put(requestId, 0d);
		}
	}

	public void sendComplete(String requestId) {
		log.debug("________COMPLETE PROGRESS FOR {}________", requestId);
		sendProgress(98, requestId);
		sendProgress(99, requestId);
		sendProgress(100, requestId);
		progressData.remove(requestId);

	}

	private void sendProgress(double progress, String requestId) {
		log.info("Send Progress: {} to {}", progress, requestId);
		try {
			realtimeService.sendProgress(progress, requestId);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public void sendProgress(double progress, double maxProgress, double percent, boolean newProgress,
			HttpServletRequest httpServletRequest) {
		String requestId =  getRequestId(httpServletRequest);
		this.sendProgress(progress, maxProgress, percent, newProgress, requestId);
	}

	public void sendProgress(double progress, double maxProgress, double percent,
			HttpServletRequest httpServletRequest) {
		sendProgress(progress, maxProgress, percent, false, httpServletRequest);
	}

	public void sendProgress(double percent, HttpServletRequest httpServletRequest) {
		sendProgress(1, 1, percent, httpServletRequest);
	}
	
	static String getRequestId(HttpServletRequest httpServletRequest) {
		return SessionUtil.getPageRequestId(httpServletRequest);
	}
	
	public void sendComplete(HttpServletRequest httpServletRequest) {
		String requestId = getRequestId(httpServletRequest);

		log.info("%%%%%%|COMPLETE PROGRESS|%%%%%%% for {}", requestId);
		realtimeService.sendProgress(100, requestId);

	}
	
	public static void main(String[] ccc) {
		ProgressService ps = new ProgressService();
		String requestId = "q03i4934i93";
		ps.init(requestId);
		log.info("1");
		ps.sendProgress(1, 2, 30, false, requestId);
		log.info("2");
		ps.sendProgress(1, 2, 30, false, requestId);
		log.info("3");
		ps.sendProgress(1, 3, 40, false, requestId);
		log.info("4");
		ps.sendProgress(1, 3, 40, false, requestId);
		log.info("5");
		ps.sendProgress(1, 3, 40, false, requestId);
		log.info("6");
		ps.sendProgress(1, 2, 30, false, requestId);
		log.info("7");
		ps.sendProgress(1, 2, 30, false, requestId);
	}

}
