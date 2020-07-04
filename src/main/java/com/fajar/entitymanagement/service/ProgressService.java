package com.fajar.entitymanagement.service;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProgressService {
	
	@Autowired
	private RealtimeService2 realtimeService;
	
	private double currentProgress=  0.0;
	
	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	}
	
	public void init(String requestId) {
		currentProgress = 0.0;
		realtimeService.sendProgress(1,requestId);
	}
	
	/**
	 * 
	 * @param progress    progressPoportion for current tax
	 * @param maxProgress totalProportion for current tax
	 * @param percent     tax Proportion for whole request
	 * @param newProgress
	 * @param requestId
	 */
	public void sendProgress(double progress, double maxProgress, double percent, boolean newProgress, String requestId) {
		if(newProgress) {
			currentProgress = 0.0;
		}
		currentProgress+=(progress/maxProgress);
		log.info("%%%%%%|PROGRESS|%%%%%%% : "+currentProgress+" adding :"+progress+"/"+maxProgress+", portion: "+percent+" ==> "+ currentProgress*percent);
		realtimeService.sendProgress(currentProgress*percent, requestId);
	}

	public void sendComplete(String requestId) {
		log.info("%%%%%%|COMPLETE PROGRESS|%%%%%%% for {}", requestId); 
		realtimeService.sendProgress(100, requestId);
		
	}

}
