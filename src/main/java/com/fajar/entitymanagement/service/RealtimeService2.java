package com.fajar.entitymanagement.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.fajar.entitymanagement.dto.WebRequest;
import com.fajar.entitymanagement.dto.WebResponse;

@Service
public class RealtimeService2 {
	Logger log = LoggerFactory.getLogger(RealtimeService2.class);

	@Autowired
	private SimpMessagingTemplate webSocket; 
	@Autowired
	private UserSessionService userSessionService;

	public RealtimeService2() {
		LogProxyFactory.setLoggers(this);
		log.info("=======================REALTIME SERVICE 2======================="); 
	}


	public boolean sendUpdateSession(Object payload) {
 
		webSocket.convertAndSend("/wsResp/sessions", payload);

		return true;
	}

	public void sendProgress(double progress, String requestId) {
		System.out.println(">>>>>>>>>>SEND PROGRESS:" + progress + " (" + requestId + ")");
		sendProgress(WebResponse.builder().requestId(requestId).percentage(progress).build());
	}

	public void sendProgress(WebResponse WebResponse) {
		webSocket.convertAndSend("/wsResp/progress/"+WebResponse.getRequestId(), WebResponse);
	}


	public void sendMessageChat(WebResponse response) {
		webSocket.convertAndSend("/wsResp/messages", response); 
	}
	
	private void sendLiveStramResponse(WebResponse response) {
		webSocket.convertAndSend("/wsResp/videostream/"+response.getRequestId(), response);
	}


	public WebResponse stream(WebRequest request) {
		WebResponse response = new WebResponse();
		
		response.setImageData(request.getImageData());
		response.setRequestId(request.getOriginId());
		
		sendLiveStramResponse(response);
		return response;
	}


	public void disconnectLiveStream(WebRequest request) {
		 
		userSessionService.setActiveSession(request.getOriginId(), false);
		WebResponse response = WebResponse.builder().code("01").requestId(request.getOriginId()).build();
		sendLiveStramResponse(response );
	}

}
