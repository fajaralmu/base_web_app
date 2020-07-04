package com.fajar.entitymanagement.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ThreadUtil {

	public static Thread run(Runnable runnable) {
		
		Thread thread  = new Thread(runnable);
		log.info("running thread: {}", thread.getId());
		log.info("active thread: {}", Thread.activeCount());
		thread.start(); 
		return thread;
	}
}
